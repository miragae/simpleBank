package pl.simplebank.dao;

import pl.simplebank.model.Bank;
import pl.simplebank.model.Bank_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless
public class BankDao extends AbstractDao<Bank> implements BankDaoLocal {
    @Override
    public Class getEntityClass() {
        return Bank.class;
    }

    @Override
    public Bank findLocalBank() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Bank> query = cb.createQuery(Bank.class);
        Root<Bank> from = query.from(Bank.class);
        query.where(cb.equal(from.get(Bank_.local), true));
        query.select(from);
        List<Bank> resultList = getEntityManager().createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public Bank findByPort(String port) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Bank> query = cb.createQuery(Bank.class);
        Root<Bank> from = query.from(Bank.class);
        query.where(cb.equal(from.get(Bank_.port), port));
        query.select(from);
        List<Bank> resultList = getEntityManager().createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
