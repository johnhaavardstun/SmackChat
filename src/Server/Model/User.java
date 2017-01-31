package Server.Model;

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

    String username;
    String password;

    String ipadress;
    int port;

    Status status;

    public User(String name, String password)
    {
        this.username=name;
        this.password=password;


    }

    public boolean passwordCheck(String pass){

        return password.equals(pass);
    }


    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public String toString()
    {
        String s="Username:"+username+"    password:"+password;
        return s;
    }

}




