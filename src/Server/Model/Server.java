package Server.Model;

import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ali on 31.01.2017.
 */
public class Server extends Task<Void> {


    final int PORT=8000;


    public Server()
    {

    }



    @Override
    protected Void call() throws Exception {

            ServerSocket sc = new ServerSocket(PORT);

            System.out.println("Har laget serverSocket!");

            while(true)
            {
                Socket s= sc.accept();

                System.out.println("Har akseptert client connection"+ s.getInetAddress().getHostAddress());

                Service sd= new Service(s,s.getInetAddress().getHostAddress() );
                sd.start();
            }

        }

        {

        }




    public  void start()
    {
        System.out.println("Starter tråd");
        Thread th= new Thread(this);
        th.start();
    }
}
