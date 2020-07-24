package client;

public interface MessageListener {
    void onMessage(String fromLogin, String toLogin, String msgBody, String isRead);
}
