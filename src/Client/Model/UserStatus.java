package Client.Model;


/**
 * This class sets the status of a user.
 *
 */
public class UserStatus
{
    /**
     * The status of a user can either be online busy or offline.
     */
    public enum Status {
        ONLINE,
        BUSY,
        OFFLINE
    }


    private final String userName;
    private Status status;

    /**
     * This constructs the user status by specifying username and status.
     *
     * @param userName the username of this user
     * @param status the status of this user
     */
    public UserStatus(String userName, Status status) {
        this.userName = userName;
        this.status = status;
    }

    /**
     * This method returns the username of this user.
     * @return the username of this user
     */
    public String getUserName() { return userName; };

    /**
     * This method returns the status of this user
     * @return the status of this user
     */
    public Status getStatus() { return status; }

    /**
     * This method sets a new status of this user by specifying
     * the status.
     *
     * @param status the new status of this user
     */
    public void setStatus(Status status) { this.status = status; }

    /**
     * This method returns a string that represents the username and status
     * of this user
     *
     * @return a string representing the username and status
     */
    @Override
    public String toString() {
        return userName + " (" + status.toString() + ")";
    }
}
