package validation;

public class Validation {
    public boolean checkLogin(String login) {
        String regex = "^[a-zA-Z0-9_'.-]{3,20}$";
        return login.matches(regex);
    }

    public boolean checkPassword(String password) {
        String regex = "^[a-zA-Z0-9_'.-]{3,20}$";
        return password.matches(regex);
    }

    public boolean checkFullname(String fullname) {
        String regex = "^[a-zA-Z ]{1,50}$";
        return fullname.matches(regex);
    }

    public boolean checkKeyTyped(char c) {
        return Character.isDigit(c) || Character.isAlphabetic(c) || c == '-' || c == '.' || c == '_' || c == '\'';
    }
}
