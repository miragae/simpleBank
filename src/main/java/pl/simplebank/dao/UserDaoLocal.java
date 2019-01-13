package pl.simplebank.dao;

import pl.simplebank.model.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserDaoLocal extends AbstractDaoLocal<User>{
    User findByLogin(String login);
    List<User> getAllClients();
    List<User> findClientsByQuery(String query);
}
