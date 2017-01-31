package Server.Model;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by Ali on 31.01.2017.
 */
public class Server implements Runnable  {


    ArrayList<User> user= new ArrayList<>();



    final String IP="127.0.0.1";
    final int PORT=5000;

    ServerSocket sc;

    public Server() throws IOException {
    }


    public void serverRun()
    {



    }


    @Override
    public void run() {

    }
}
