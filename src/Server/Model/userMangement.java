package Server.Model;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ali on 27.01.2017.
 */
public class userMangement
{
    public static final String filn ="\\Server\\users.txt";


    public String curLine;
    public String usrN;
    public String usrP;

    public userMangement(){}

    ArrayList<User> userList = new ArrayList<>();

    public void readFile() throws IOException {
        BufferedReader rFile = new BufferedReader(new FileReader(filn));
        int i = 0;
        while((curLine = rFile.readLine()) != null)
        {
            //shamil:eple123
            String[] info = curLine.split(":");
            usrN = info[0].toString();
            usrP = info[1].toString();
            User u = new User(usrN, usrP);
            userList.add(i,u);
            i++;
        }
    }
    @Override
    public String toString()
    {
        String s = "";
        for(User user : userList)
        {
            s += user.username + " : " + user.password + "\n";
        }
        return s;
    }


}


