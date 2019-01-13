package pl.simplebank.dao;

import pl.simplebank.model.Account;
import pl.simplebank.model.Operation;

import javax.ejb.Local;
import java.util.List;

@Local
public interface OperationDaoLocal extends AbstractDaoLocal<Operation> {
    List<Operation> findByAccount(Account account);
}
