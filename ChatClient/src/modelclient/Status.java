package modelclient;

public class Status {
    private String login;
    private boolean isOpen;

    public Status() {
    }

    public Status(String login, boolean isOpen) {
        this.login = login;
        this.isOpen = isOpen;
    }

    public Status(String login) {
        this.login = login;
        this.isOpen = false;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return login + "," + isOpen;
    }
}
