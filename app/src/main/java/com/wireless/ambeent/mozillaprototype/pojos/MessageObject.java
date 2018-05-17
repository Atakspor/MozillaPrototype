package com.wireless.ambeent.mozillaprototype.pojos;

import java.util.Objects;

public class MessageObject {

    private String id;

    private String message;

    private String sender;

    private String receiver;

    public MessageObject(String id, String message, String sender) {
        this.id = id;
        this.message = message;
        this.sender = sender;
    }

    public MessageObject(String id, String message, String sender, String receiver) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageObject that = (MessageObject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
