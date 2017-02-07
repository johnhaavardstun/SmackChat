package Server.Model;

import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * Created by Ali on 27.01.2017.
 */
public class User
{
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

    public User(String name, String password)
    {
        this.username=name;
        this.password=password;
        this.status = Status.OFFLINE;

    }

    public boolean passwordCheck(String pass){

        return password.equals(pass);
    }


    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }
    public String getIpadress() { return status != Status.OFFLINE ? ipadress : null; }

    public void setPort(int port) {
        this.port = port;
    }
    public int getPort() { return status != Status.OFFLINE ? port : -1; }

    public void setStatus(Status status) {
        this.status = status;
    }
    public Status getStatus() { return status; }

    public String getUsername() { return username; }

    public String toString()
    {
        String s="Username:"+username+"    password:"+password;
        return s;
    }

}




