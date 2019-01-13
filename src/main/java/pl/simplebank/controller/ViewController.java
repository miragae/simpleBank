package pl.simplebank.controller;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("viewController")
@SessionScoped
public class ViewController implements Serializable {

    @Inject
    UserController userController;

    private String menuHeader;
    private String menuId;
    private MenuModel userMenu;

    public String getMenuHeader() {
        return menuHeader;
    }

    public String getMenuId() {
        return menuId;
    }

    public void clearMenu(){
        userMenu = null;
        menuId = null;
        menuHeader = null;
    }

    public MenuModel getUserMenu() {
        if(userMenu == null){
            userMenu = createUserMenu();
        }
        return userMenu;
    }

    public MenuModel createUserMenu(){
        MenuModel menu = new DefaultMenuModel();

        switch(userController.getLoggedUser().getType()) {
            case ADMIN:
                menu.addElement(newMenuItem("USERS", "Users"));
                menu.addElement(newMenuItem("BANKS", "Banks"));
                menu.addElement(newMenuItem("ACCOUNTS", "Accounts"));
                break;
            case EMPLOYEE:
                menu.addElement(newMenuItem("USERS", "Clients"));
                menu.addElement(newMenuItem("OPERATIONS", "Operations"));
                break;
            case CLIENT:
                menu.addElement(newMenuItem("BALANCE", "Balance"));
                menu.addElement(newMenuItem("HISTORY", "History"));
                menu.addElement(newMenuItem("TRANSFER", "Transfer"));
                break;
        }
        return menu;
    }

    public void selectMenuItem(String id, String title) {
        menuId = id;
        menuHeader = title;
    }

    private DefaultMenuItem newMenuItem(String id, String title) {
        DefaultMenuItem item = new DefaultMenuItem(title);
        item.setCommand("#{viewController.selectMenuItem('"+id+"','"+title+"')}");
        item.setUpdate("centerPanel, centerPanelHeader");
        item.setAjax(false);

        return item;
    }
}
