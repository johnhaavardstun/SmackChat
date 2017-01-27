package Server.Model;

/**
 * Created by Ali on 27.01.2017.
 */
public class User
{
    String username;
    String password;


    public User(String name, String password)
    {
        name=this.username;
        password=this.password;


    }

    public boolean passwordCheck(String pass){

        return password.equals(pass);
    }



}




