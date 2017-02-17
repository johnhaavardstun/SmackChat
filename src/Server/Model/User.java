package Server.Model;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import javafx.beans.value.ObservableValue;

/** Class that creates a user which can log in SmackChat.
 *
 * Created by Ali on 27.01.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class User
{
    /**
     * The status of a user can either be online
     * , busy or offline.
     */
    public enum Status{
        ONLINE,
        BUSY,
        OFFLINE
    }

    private String username;
    private String password;

    private String ipadress;
    private int port;

    private Status status;

    /**
     * This constructs a user with a specified name and password.
     * The status is set to offline.
     *
     * @param name the name of the user
     * @param password the password of the user
     */
    public User(String name, String password)
    {
        this.username=name;
        this.password=password;
        this.status = Status.OFFLINE;
    }

    /**
     * This method returns true if the specified password is
     * equal to the user password.
     * @param pass the password you are testing
     * @return true if specified password is equal; false otherwise.
     */
    public boolean passwordCheck(String pass){

        return password.equals(pass);
    }

    /**
     * This sets the ip address of this user.
     * @param ipadress the ip address of the user
     */
    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }

    /**
     * This returns the ip address of this user
     * if the user is not offline.
     * Otherwise it will return null.
     *
     * @return the ip address of the user
     */
    public String getIpadress() { return status != Status.OFFLINE ? ipadress : null; }

    /**
     * This sets the port number of this user.
     *
     * @param port the port number of this user
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * This returns the port number of this user
     * if the user is not offline.
     * Otherwise it will return -1.
     *
     * @return the port number of this user
     */
    public int getPort() { return status != Status.OFFLINE ? port : -1; }

    /**
     * This sets the status of this user to the specified status.
     *
     * @param status the status of this user
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * This returns the status of this user.
     *
     * @return the status of this user.
     */
    public Status getStatus() { return status; }

    /**
     * This returns the username of this user.
     *
     * @return the username of this user
     */
    public String getUsername() { return username; }

    /**
     * This returns the username and the password of this user.
     *
     * @return the username and password of this user
     */
    public String toString()
    {
        String s="Username:"+username+"    password:"+password;
        return s;
    }

}




