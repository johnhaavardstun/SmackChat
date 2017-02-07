package Client.Model;

import Server.Model.User;

/**
 * Created by JohnHusfloen on 07/02/2017.
 */
public class UserStatus
{
    public enum Status {
        ONLINE,
        BUSY,
        OFFLINE
    }

    private final String userName;
    private Status status;

    public UserStatus(String userName, Status status) {
        this.userName = userName;
        this.status = status;
    }

    public String getUserName() { return userName; };
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return userName + " (" + status.toString() + ")";
    }
}
