package pl.simplebank.controller;

import pl.simplebank.dao.AccountDaoLocal;
import pl.simplebank.model.Account;
import pl.simplebank.model.User;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static java.math.BigDecimal.ZERO;

@Named("accountController")
@SessionScoped
public class AccountController implements Serializable {

    @EJB
    AccountDaoLocal accountDao;

    @Inject
    BankController bankController;

    private Account selectedAccount;

    public Account getSelectedAccount() {
        if (selectedAccount == null) {
            selectedAccount = new Account();
        }
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public void generateNewAccount(User user) {
        Account account = new Account();
        account.setBank(bankController.getLocalBank());
        account.setBalance(ZERO);
        account.setOwner(user);
        account.setNumber(generateUniqueAccountNumber());

        accountDao.save(account);
    }

    private String generateUniqueAccountNumber() {
        Random generator = new Random();
        String newAccount;
        do {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 26; i++) {
                sb.append(generator.nextInt(10));
            }
            newAccount = sb.toString();
        } while (accountDao.findByNumber(newAccount) != null);

        return newAccount;
    }

    public boolean isSelectedAccountEmpty() {
        return getSelectedAccount().getBalance() == null || ZERO.compareTo(getSelectedAccount().getBalance()) == 0;
    }

    public void updateSelectedAccountBalance(BigDecimal amount) {
        selectedAccount.setBalance(selectedAccount.getBalance().add(amount));
        accountDao.update(selectedAccount);
    }

    public List<Account> getAllAccounts() {
        return accountDao.getAll();
    }
}
