package pl.simplebank.dao;

import pl.simplebank.model.Account;
import pl.simplebank.model.Account_;
import pl.simplebank.model.User;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless
public class AccountDao extends AbstractDao<Account> implements AccountDaoLocal{

    @Override
    public Class getEntityClass() {
        return Account.class;
    }

    @Override
    public Account findByNumber(String number) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        query.where(cb.equal(from.get(Account_.number), number));
        query.select(from);
        List<Account> resultList = getEntityManager().createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public Account findByOwner(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        query.where(cb.equal(from.get(Account_.owner), user));
        query.select(from);
        List<Account> resultList = getEntityManager().createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

}
