package pl.simplebank.dao;

import pl.simplebank.model.Account;
import pl.simplebank.model.User;

import javax.ejb.Local;

@Local
public interface AccountDaoLocal extends AbstractDaoLocal<Account> {
    Account findByNumber(String number);

    Account findByOwner(User user);
}
