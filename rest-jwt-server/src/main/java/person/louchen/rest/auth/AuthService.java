package person.louchen.rest.auth;


import person.louchen.rest.user.User;

public interface AuthService {
    User register(User userToAdd);
    String login(String username, String password);
    String refresh(String oldToken);
}
