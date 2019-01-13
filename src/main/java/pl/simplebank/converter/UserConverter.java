package pl.simplebank.converter;

import pl.simplebank.dao.UserDaoLocal;
import pl.simplebank.model.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class UserConverter implements Converter {

    @Inject
    private UserDaoLocal userDao;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null || value.equals("null")) {
            return null;
        }
        return userDao.find(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((User) value).getId());
    }
}
