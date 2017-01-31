package Server.Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ali on 27.01.2017.
 */
public class userMangement
{


    public static String curLine;
    public static  String usrN;
    public static String usrP;


   static ArrayList<User> userList = new ArrayList<>();

    public static void readFile() throws IOException {


        BufferedReader rFile = new BufferedReader(new FileReader("./src/Server/users.txt"));
        while((curLine = rFile.readLine()) != null)
        {

            String[] info = curLine.split(":");


            usrN = info[0];
            usrP = info[1];

            User u = new User(usrN, usrP);
            userList.add(u);
        }
        rFile.close();
       System.out.println(ArraytoString());

    }

    public static   String  ArraytoString()
    {
        String s = "";
        for(User user : userList)
        {
          s+= user.toString()+'\n';
        }
        return s;
    }


}


