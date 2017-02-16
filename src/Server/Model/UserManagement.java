package Server.Model;

import javafx.beans.value.ChangeListener;

import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Class that manages the users
 *
 * This class contains methods that manages the users status and
 * which user is registered in SmackChat.
 *
 * Created by Ali on 27.01.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class UserManagement
{
    public static String curLine;
    public static String testLine;
    public static  String usrN;
    public static String usrP;

    private static List<User> userList = new ArrayList<>(); //FXCollections.observableArrayList();
    private static List<ChangeListener<User.Status>> listeners = new ArrayList<>();

    /**
     * This method reads a file which contains registered users.
     * While reading this file this method puts the registered users in a list.
     *
     * @throws IOException when it can not find the given file name.
     */
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

    /** Method that returns registered users.
     *
     * Returns a list of all the registered users.
     *
     * @return all registered users in a list.
     */
    public static String ArraytoString()
    {
        String s = "";
        for(User user : userList)
        {
          s+= user.toString()+'\n';
        }
        return s;
    }

    /** Method which tests whether the username is available.
     *
     * This method iterates through the user list to test
     * whether the username is available.
     *
     * @param un username written by the client.
     * @return true if the username does not exist; false otherwise.
     * @throws IOException if username is null.
     */
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

    /** Method that adds user to file
     *
     * This method registers a new user into user file and
     * adds the user into the user list.
     *
     * @param n name of the user
     * @param p password of the user
     * @throws IOException if the given username or password is null.
     */
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
        //System.out.println(ArraytoString());
    }

    /** Method tests whether the login is correct
     *
     * This method iterates through user list and checks whether
     * the login information given by the client is correct.
     *
     * @param username name of the user
     * @param password password of the user
     * @return true if username and password is correct; false otherwise.
     */
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

    /** Method which returns all users status
     *
     * @return
     */
    public static String getUserStatusList() {
        StringBuffer sb = new StringBuffer();
//int i = 0;
        for (User u: userList)
        {
//            switch (i % 3)
//            {
//                case 0: u.setStatus(User.Status.ONLINE); break;
//                case 1: u.setStatus(User.Status.BUSY); break;
//                case 2: u.setStatus(User.Status.OFFLINE); break;
//            }i++;
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

    public static User getUser(String username)
    {
        for (User user: userList)
        {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    public static List<User> getUsers()
    {
        return userList;
    }

    public static void addStatusListener(ChangeListener<User.Status> listener)
    {
        listeners.add(listener);
    }

    public static void setUserStatus(String username, User.Status status)
    {
        for (User user: userList)
        {
            if (user.getUsername().equals(username))
            {
                setUserStatus(user, status);
                return;
            }
        }
    }

    public static void setUserStatus(User user,  User.Status status) {
        if (user == null) throw new NullPointerException("User can not be null!");

        User.Status oldStatus = user.getStatus();
        user.setStatus(status);
        for (ChangeListener listener: listeners)
            listener.changed(null, oldStatus, status);
    }

}
