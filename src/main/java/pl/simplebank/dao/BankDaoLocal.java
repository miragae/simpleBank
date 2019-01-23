package pl.simplebank.dao;

import pl.simplebank.model.Bank;

import javax.ejb.Local;

@Local
public interface BankDaoLocal extends AbstractDaoLocal<Bank> {
    Bank findLocalBank();
    Bank findByIp(String ip);
}
