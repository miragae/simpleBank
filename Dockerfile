FROM maven:3.6-jdk-8-slim AS build
RUN mkdir -p /app
WORKDIR /app
ADD src /app/src
ADD pom.xml /app
RUN ls -la
RUN mvn clean install

FROM jboss/wildfly:latest

# Appserver
ENV WILDFLY_USER admin
ENV WILDFLY_PASS password

# Database
ENV DB_NAME bankdb
ENV DB_USER maria
ENV DB_PASS maria
ENV DB_URI db:3306

ENV DATASOURCE_NAME bankDS

ENV MARIADB_CONNECTOR_VERSION 2.3.0
ENV JBOSS_CLI /opt/jboss/wildfly/bin/jboss-cli.sh
ENV DEPLOYMENT_DIR /opt/jboss/wildfly/standalone/deployments/
#ENV JAVA_OPTS

# Setting up WildFly Admin Console
RUN $JBOSS_HOME/bin/add-user.sh -u $WILDFLY_USER -p $WILDFLY_PASS --silent

# Configure Wildfly server
RUN   curl --location --output /tmp/mariadb-java-client-${MARIADB_CONNECTOR_VERSION}.jar --url http://downloads.mariadb.com/Connectors/java/connector-java-${MARIADB_CONNECTOR_VERSION}/mariadb-java-client-${MARIADB_CONNECTOR_VERSION}.jar && \
      bash -c '$JBOSS_HOME/bin/standalone.sh &' && \
      bash -c 'until `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
      $JBOSS_CLI --connect --command="module add --name=org.mariadb --resources=/tmp/mariadb-java-client-${MARIADB_CONNECTOR_VERSION}.jar --dependencies=javax.api,javax.transaction.api" && \
      $JBOSS_CLI --connect --command="/subsystem=datasources/jdbc-driver=mariadb:add(driver-name=mariadb,driver-module-name=org.mariadb)" && \
      $JBOSS_CLI --connect --command="data-source add \
        --name=${DATASOURCE_NAME} \
        --jndi-name=java:/simpleBank/${DATASOURCE_NAME} \
        --user-name=${DB_USER} \
        --password=${DB_PASS} \
        --driver-name=mariadb \
        --connection-url=jdbc:mysql://${DB_URI}/${DB_NAME} \
        --use-ccm=false \
        --max-pool-size=25 \
        --blocking-timeout-wait-millis=5000 \
        --enabled=true" && \
      $JBOSS_CLI --connect --command=":shutdown" && \
      rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* && \
      rm -f /tmp/*.jar

# Expose http and admin ports
EXPOSE 8080


COPY --from=build /app/target/SimpleBank-1.0.war $DEPLOYMENT_DIR/SimpleBank-1.0.war

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
