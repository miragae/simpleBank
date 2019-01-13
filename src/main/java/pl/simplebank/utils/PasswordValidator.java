package pl.simplebank.utils;

import org.mindrot.jbcrypt.BCrypt;
import pl.simplebank.model.User;

public final class PasswordValidator {

    public static final boolean validatePassword(String password, User user) {
        return BCrypt.checkpw(password, user.getPassword());
    }

    public static final String getPasswordHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
