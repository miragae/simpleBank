package pl.simplebank.dao;

import pl.simplebank.model.Account;
import pl.simplebank.model.Operation;
import pl.simplebank.model.Operation_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class OperationDao extends AbstractDao<Operation> implements OperationDaoLocal{
    @Override
    public Class getEntityClass() {
        return Operation.class;
    }

    @Override
    public void save(Operation item) {
        item.setOperationTime(LocalDateTime.now());
        super.save(item);
    }

    @Override
    public List<Operation> findByAccount(Account account) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Operation> query = cb.createQuery(Operation.class);
        Root<Operation> from = query.from(Operation.class);
        query.where(cb.or(
                cb.equal(from.get(Operation_.fromAccount), account),
                cb.equal(from.get(Operation_.toAccount), account)
        ));
        query.select(from);
        query.orderBy(cb.desc(from.get(Operation_.operationTime)));
        return getEntityManager().createQuery(query).getResultList();
    }
}
