package pl.simplebank.dao;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AbstractDaoLocal<T> {
    void save(T item);
    void update(T item);
    List<T> getAll();
    T find(Long id);
}
