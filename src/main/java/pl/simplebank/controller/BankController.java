package pl.simplebank.controller;

import pl.simplebank.dao.BankDaoLocal;
import pl.simplebank.model.Bank;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("bankController")
@SessionScoped
public class BankController implements Serializable {

    @EJB
    private BankDaoLocal bankDao;

    private Bank localBank;

    private Bank newBank;

    public Bank getNewBank() {
        if (newBank == null) {
            newBank = new Bank();
        }
        return newBank;
    }

    public void setNewBank(Bank newBank) {
        this.newBank = newBank;
    }

    public Bank getLocalBank() {
        if (localBank == null) {
            localBank = bankDao.findLocalBank();
        }
        return localBank;
    }

    public List<Bank> getAllBanks() {
        return bankDao.getAll();
    }

    public void addBank() {
        boolean firstLocal = bankDao.findLocalBank() == null ? true : false;
        newBank.setLocal(firstLocal);
        bankDao.save(newBank);
        newBank = new Bank();
    }
}
