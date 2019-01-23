package pl.simplebank.rest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.simplebank.model.Bank;

import java.io.IOException;

public class BankConnectionService {

    private static final String HTTP_PREFIX = "http://";
    private static final String BANK_PATH = "/SimpleBank/faces/rest/bank/";
    private static final String PATH_SEPARATOR = "/";
    private static final String USER_AGENT = "SimpleBank";

    private static final int SOCKET_TIMEOUT = 2000;
    private static final int CONNECT_TIMEOUT = 2000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 2000;

    public boolean checkIfAccountExists(String accountNumber, Bank bank) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = createBankUrl(accountNumber, bank);
        RequestConfig config = createRequestConfig();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        httpGet.addHeader("User-Agent", USER_AGENT);

        try(CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendTransfer(String accountNumberTo, String accountNumberFrom, String amount, Bank bank) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = createBankUrl(accountNumberTo, accountNumberFrom, amount, bank);
        RequestConfig config = createRequestConfig();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        httpGet.addHeader("User-Agent", USER_AGENT);

        try(CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return response.getStatusLine().getStatusCode() == 200;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String createBankUrl(String accountNumber, Bank bank) {
        StringBuilder url = new StringBuilder();
        url.append(HTTP_PREFIX)
                .append(bank.getIp())
                .append(":")
                .append(bank.getPort())
                .append(BANK_PATH)
                .append(accountNumber);
        return url.toString();
    }

    private String createBankUrl(String accountNumberTo, String accountNumberFrom, String amount, Bank bank) {
        StringBuilder url = new StringBuilder(createBankUrl(accountNumberTo, bank));
        url.append(PATH_SEPARATOR)
                .append(accountNumberFrom)
                .append(PATH_SEPARATOR)
                .append(amount);

        return url.toString();
    }

    private RequestConfig createRequestConfig(){
        return RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();
    }
}
