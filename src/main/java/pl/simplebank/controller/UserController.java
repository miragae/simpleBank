package pl.simplebank.controller;

import pl.simplebank.dao.AccountDaoLocal;
import pl.simplebank.dao.OperationDaoLocal;
import pl.simplebank.dao.UserDaoLocal;
import pl.simplebank.model.Account;
import pl.simplebank.model.Operation;
import pl.simplebank.model.User;
import pl.simplebank.model.UserType;
import pl.simplebank.utils.PasswordValidator;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pl.simplebank.model.UserType.ADMIN;
import static pl.simplebank.model.UserType.CLIENT;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    @Inject
    AccountController accountController;
    @Inject
    ViewController viewController;

    @EJB
    private UserDaoLocal userDao;
    @EJB
    private AccountDaoLocal accountDao;
    @EJB
    private OperationDaoLocal operationDao;

    private User newUser;
    private User selectedUser;
    private User loggedUser;
    private String username;
    private String password;
    private Account userAccount;

    public User getNewUser() {
        if(newUser == null){
            newUser = new User();
        }
        return newUser;
    }

    public User getSelectedUser() {
        if (selectedUser == null) {
            selectedUser = new User();
        }
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Account getUserAccount() {
        return userAccount;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean renderUserView() {
        return loggedUser != null;
    }

    public void logout() {
        loggedUser = null;
        viewController.clearMenu();
    }

    public void login() {
        User user = userDao.findByLogin(username);
        if (user == null) {
            addFacesMessage("Can't log in.", "No such user.");
        } else {
            boolean passwordValid = PasswordValidator.validatePassword(password, user);
            if (passwordValid) {
                loggedUser = user;
                if(isClient()) {
                    accountController.setSelectedAccount(accountDao.findByOwner(loggedUser));
                }
            } else {
                addFacesMessage("Can't log in.", "Invalid password.");
            }
        }
    }

    public boolean isAdmin() {
        return loggedUser != null && ADMIN.equals(loggedUser.getType());
    }

    public boolean isClient() {
        return loggedUser != null && CLIENT.equals(loggedUser.getType());
    }

    public void addUser() {
        if(!isAdmin()) {
            newUser.setType(CLIENT);
        }
        newUser.setPassword(PasswordValidator.getPasswordHash(newUser.getPassword()));
        userDao.save(newUser);
        if(CLIENT.equals(newUser.getType())) {
            accountController.generateNewAccount(newUser);
        }
        newUser = new User();
    }

    public List<User> getAllUsers() {
        if(isAdmin()){
            return userDao.getAll();
        } else {
            return userDao.getAllClients();
        }
    }

    public void selectUserDetails(User user) {
        userAccount = accountDao.findByOwner(user);
        if(userAccount == null) {
            userAccount = new Account();
        }
    }

    public List<Operation> getAllUserOperations() {
        if (accountController.getSelectedAccount().getId() != null) {
            return operationDao.findByAccount(accountController.getSelectedAccount());
        } else {
            return Collections.emptyList();
        }
    }

    public List<UserType> getUserTypes() {
        return Arrays.asList(UserType.values());
    }

    public List<User> completeClients(String query) {
        return userDao.findClientsByQuery(query);
    }

    public void selectUser() {
        accountController.setSelectedAccount(accountDao.findByOwner(selectedUser));
    }

    private void addFacesMessage(String summary, String detail) {
        FacesMessage msg = new FacesMessage(summary, detail);
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        if (currentInstance != null) {
            currentInstance.addMessage(null, msg);
        }
    }
}
