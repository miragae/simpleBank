package pl.simplebank.controller;

import pl.simplebank.dao.AccountDaoLocal;
import pl.simplebank.dao.BankDaoLocal;
import pl.simplebank.dao.OperationDaoLocal;
import pl.simplebank.model.Account;
import pl.simplebank.model.Bank;
import pl.simplebank.model.Operation;
import pl.simplebank.rest.BankConnectionService;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static pl.simplebank.model.OperationType.*;

@Named("operationController")
@SessionScoped
public class OperationController implements Serializable {
    @Inject
    AccountController accountController;

    @EJB
    OperationDaoLocal operationDao;
    @EJB
    AccountDaoLocal accountDao;
    @EJB
    BankDaoLocal bankDao;

    private BankConnectionService bankConnectionService = new BankConnectionService();

    private Operation newOperation;
    private Account transferAccount;

    public Operation getNewOperation() {
        if(newOperation == null) {
            newOperation = new Operation();
        }
        return newOperation;
    }

    public void setNewOperation(Operation newOperation) {
        this.newOperation = newOperation;
    }

    public Account getTransferAccount() {
        if(transferAccount == null) {
            transferAccount = new Account();
        }
        return transferAccount;
    }

    public void setTransferAccount(Account transferAccount) {
        this.transferAccount = transferAccount;
    }

    public void findTransferAccount() {
        String transferAccountNumber = transferAccount.getNumber();
        if(transferAccountNumber.length() != 26) {
            addFacesMessage("Can't find account.", "Account number length different than 26");
            return;
        }

        Account account = accountDao.findByNumber(transferAccountNumber);
        if (account != null) {
            if(account.getBank().isLocal()) {
                transferAccount = account;
                return;
            }

            if(bankConnectionService.checkIfAccountExists(account.getNumber(), account.getBank())) {
                transferAccount = account;
                return;
            }
        } else {
            List<Bank> allBanks = bankDao.getAll();
            for(Bank bank : allBanks) {
                if(!bank.isLocal()) {
                    if(bankConnectionService.checkIfAccountExists(transferAccountNumber, bank)) {
                        account = new Account();
                        account.setBank(bank);
                        account.setNumber(transferAccountNumber);
                        accountDao.save(account);
                        transferAccount = account;
                        return;
                    }
                }
            }
            addFacesMessage("Can't find account.", "");
        }
    }

    public void deposit() {
        newOperation.setType(DEPOSIT);
        newOperation.setToAccount(accountController.getSelectedAccount());
        executeOperation(newOperation.getAmount());
    }

    public void withdraw() {
        newOperation.setType(WITHDRAW);
        newOperation.setFromAccount(accountController.getSelectedAccount());
        executeOperation(newOperation.getAmount().negate());
    }

    public void transfer() {
        newOperation.setType(TRANSFER);
        newOperation.setFromAccount(accountController.getSelectedAccount());
        newOperation.setToAccount(transferAccount);
        if(transferAccount.getBank().isLocal()) {
            transferAccount.setBalance(transferAccount.getBalance().add(newOperation.getAmount()));
            accountDao.update(transferAccount);
        } else {
            String accountTo = transferAccount.getNumber();
            String accountFrom = accountController.getSelectedAccount().getNumber();
            String amount = newOperation.getAmount().toString();
            Bank bankTo = transferAccount.getBank();
            boolean successfulTransfer = bankConnectionService.sendTransfer(accountTo, accountFrom, amount, bankTo);
            if (!successfulTransfer) {
                addFacesMessage("Can't send bank transfer.", bankTo.getName()+" rejected transfer");
                return;
            }
        }
        executeOperation(newOperation.getAmount().negate());
    }

    private void executeOperation(BigDecimal amount) {
        if(ZERO.compareTo(newOperation.getAmount()) != 0) {
            accountController.updateSelectedAccountBalance(amount);
            operationDao.save(newOperation);
        }
        transferAccount = new Account();
        newOperation = new Operation();
    }

    private void addFacesMessage(String summary, String detail) {
        FacesMessage msg = new FacesMessage(summary, detail);
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        if (currentInstance != null) {
            currentInstance.addMessage(null, msg);
        }
    }
}
