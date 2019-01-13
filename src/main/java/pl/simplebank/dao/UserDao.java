package pl.simplebank.dao;

import pl.simplebank.model.User;
import pl.simplebank.model.User_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static pl.simplebank.model.UserType.CLIENT;

@Stateless
public class UserDao extends AbstractDao<User> implements UserDaoLocal {
    @Override
    public Class getEntityClass() {
        return User.class;
    }

    @Override
    public User findByLogin(String login) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> from = query.from(User.class);
        query.where(cb.equal(from.get(User_.login), login));
        query.select(from);
        List<User> resultList = getEntityManager().createQuery(query).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public List<User> getAllClients() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> from = query.from(User.class);
        query.where(cb.equal(from.get(User_.type), CLIENT));
        query.select(from);
        return getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public List<User> findClientsByQuery(String stringQuery) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> from = query.from(User.class);
        List<Predicate> predicates = new ArrayList<>();
        for(String subQuery : stringQuery.split(" ")) {
            predicates.add(cb.like(from.get(User_.firstName), subQuery + '%'));
            predicates.add(cb.like(from.get(User_.lastName), subQuery + '%'));
            predicates.add(cb.like(from.get(User_.idNumber), subQuery + '%'));
        }
        query.where(cb.and(
                cb.equal(from.get(User_.type), CLIENT),
                cb.or(predicates.toArray(new Predicate[0]))));
        query.orderBy(cb.asc(from.get(User_.id)));
        query.select(from);
        return getEntityManager().createQuery(query).getResultList();
    }
}
