package Server.Model;

import java.io.*;
import java.lang.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Ali on 27.01.2017.
 */
public class UserManagement
{
    public static String curLine;
    public static String testLine;
    public static  String usrN;
    public static String usrP;

    private static ArrayList<User> userList = new ArrayList<>();

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

    public static String ArraytoString()
    {
        String s = "";
        for(User user : userList)
        {
          s+= user.toString()+'\n';
        }
        return s;
    }

    // Metode som itererer gjennom userList til å teste om brukernavn eller passord eksisterer fra før av.
    public static boolean userExistTest(String un) throws IOException
    {
        if(userList.isEmpty()) { return true; } // Betydning: finnes ingen brukere i filen.
        else {
            // Iterator som kjører gjennom alle registrerte brukere.
           for(Iterator it = userList.iterator(); it.hasNext();) {

            User u = (User) it.next();
            if(un.equals(u.getUsername())) return false;    // Returnerer false hvis bruker eksisterer
           }
           return true;
        }

    }

    public static void addUserToFile(String n, String p) throws IOException
    {
        BufferedWriter wFile = new BufferedWriter(new FileWriter("./src/Server/users.txt", true));

        usrN = n;
        usrP = p;
        User newUser = new User(n,p);

        wFile.newLine();                    // Lager ny linje etter siste bruker i filen.
        wFile.write(usrN + ":" + usrP);  // Skriver inn ny bruker ved endepunket i filen.
        userList.add(newUser);

        wFile.close();
        System.out.println(ArraytoString());
    }

    public static Boolean checkIfLoginCorrect(String username, String password)
    {
        for (User user : userList)
        {
            if(user.getUsername().equals(username))
            {
                return user.passwordCheck(password);
            }

        }
        return false;
    }

    public static String getUserStatusList() {
        StringBuffer sb = new StringBuffer();

        for (User u: userList)
        {
            switch (u.getStatus())
            {
                case ONLINE:  sb.append(1); break;
                case BUSY:    sb.append(2); break;
                case OFFLINE: sb.append(3); break;
            }

            sb.append(u.getUsername());
            sb.append("\n");
        }

        return sb.toString();
    }

}
