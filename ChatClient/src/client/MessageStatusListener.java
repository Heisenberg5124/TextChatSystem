package client;

public interface MessageStatusListener {
    void open(String sender, String recipient);
    void close(String sender, String recipient);
}
