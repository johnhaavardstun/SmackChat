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


    final int PORT=5000;





    @Override
    protected Void call() throws Exception {
        try (ServerSocket sc = new ServerSocket(PORT);)
        {

            System.out.println("Har laget server!");

            while(true)
            {
                System.out.println("Aksepterter start");

                Socket s= sc.accept();

                System.out.println("Har akseptert"+ s.getInetAddress().getHostAddress());

                Service sd= new Service(s,s.getInetAddress().getHostAddress() );
                sd.start();
            }

        }

    }


    public  void startThread()
    {
        System.out.println("Starter tråd");
        Thread th= new Thread(this);
        th.start();
    }
}
