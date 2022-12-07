package app;

import java.util.Objects;

public class User {
    private final long id;
    private final String name;
    private final String role;
    private String messageText;
    private String messageId;


    public User(long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && name.equals(user.name) && Objects.equals(role, user.role) && messageText.equals(user.messageText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role, messageText);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", message='" + messageText + '\'' +
                '}';
    }
}
