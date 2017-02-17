package Server.Model;

import javafx.concurrent.Task;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/** This class is the server of the program, it creates the server socket
 * and handles multiple clients by giving each client a new thread.
 *
 * Created by Ali on 31.01.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class Server extends Task<Void> {


    final int PORT=8000;
    static ConcurrentHashMap<String, Service> map=new ConcurrentHashMap<>();









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

    public void start()
    {
        System.out.println("Starter tråd");
        Thread th= new Thread(this);
        th.setDaemon(true);
        th.start();
    }
}
