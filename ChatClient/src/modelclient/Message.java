package modelclient;

import java.io.Serializable;

public class Message implements Serializable {
    private String sender, recipient, body, timeCreate;
    private boolean isRead;

    public Message() {
    }

    public Message(String sender, String recipient, String body) {
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
    }

    public Message(String sender, String recipient, String body, String timeCreate, boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
        this.timeCreate = timeCreate;
        this.isRead = isRead;
    }

    public Message(String sender, String recipient, String body, boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
        this.isRead = isRead;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", body='" + body + '\'' +
                ", timeCreate='" + timeCreate + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
