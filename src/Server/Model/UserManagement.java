package Server.Model;

import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that manages the users
 * <p>This class contains methods that manages the users status and
 * which user is registered in SmackChat.</p>
 *
 */
public class UserManagement
{


    private static List<User> userList = new ArrayList<>(); //FXCollections.observableArrayList();
    private static List<UserStatusChangeListener> listeners = new ArrayList<>();

    @FunctionalInterface public interface UserStatusChangeListener
    {
        void onChange(User u);
    }

    /**
     * <h1>Method that reads a file</h1>
     *
     * <p>This method reads a file which contains registered users.
     * While reading this file this method puts the registered users in a list.</p>
     *
     * @throws IOException when it can not find the given file name.
     */
    public static void readFile() throws IOException
    {
        String curLine;
        String usrN;
        String usrP;

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

    /**
     * Method that returns registered users.
     *
     * <p>Returns a list of all the registered users.</p>
     *
     * @return all registered users in a list.
     */
    public static String ArraytoString()
    {
        String s = "";
        for(User user : userList)
        {
          s += user.toString()+'\n';
        }
        return s;
    }

    /**
     * Method which tests whether the username is available.
     *
     * <p>This method iterates through the user list to test
     * whether the username is available.</p>
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

    /**
     * Method that adds user to file
     *
     * <p>This method registers a new user into user file and
     * adds the user into the user list.</p>
     *
     * @param n name of the user
     * @param p password of the user
     * @throws IOException if the given username or password is null.
     */
    public static void addUserToFile(String n, String p) throws IOException
    {
        String usrN;
        String usrP;
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
     * <p>This method iterates through user list and checks whether
     * the login information given by the client is correct.</p>
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

    /** Method that returns all users status
     *
     * <p>This method goes through user list and sets all of the users status
     * to either online, busy or offline.</p>
     * <p>This method returns a string where the syntax is: status+username+"\n".
     * The status is equal to 1, 2 or 3 given the status of the user.</p>
     *
     * @return contains the statuses of all users.
     */
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

    /** Method that returns a user
     *
     * <p>This method iterates through user list and returns the user
     * if the given username is correct.</p>
     *
     * @param username name of the user you want to get.
     * @return exists it returns the user; null otherwise.
     */
    public static User getUser(String username)
    {
        for (User user: userList)
        {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    /** Method that returns user list.
     *
     * @return list of registered users
     */
    public static List<User> getUsers()
    {
        return userList;
    }

    /** Method that adds a user status to a listener
     *
     * @param listener to a user status
     */
    public static void addStatusListener(UserStatusChangeListener listener)
    {
        listeners.add(listener);
    }

    /** Method that sets a user status
     *
     * <p>This method changes the status of the user to either online, busy or offline.</p>
     *
     * <p>It iterates through user list and changes the user status
     * given by the status you set in the status parameter.</p>
     *
     * @param username name of the user
     * @param status the status of the user to be changed to.
     */
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

    /**
     *
     * @param user the user object, @{@link User}
     * @param status the status of the user to be changed to.
     */
    public static void setUserStatus(User user,  User.Status status) {
        if (user == null) throw new NullPointerException("User can not be null!");

        User.Status oldStatus = user.getStatus();
        user.setStatus(status);
        for (UserStatusChangeListener listener: listeners)
            listener.onChange(user);
    }

}
