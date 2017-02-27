package Server.Model;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is the server of the program, it creates the server socket
 * and handles multiple clients by starting a new thread for each client.
 * When clients are connected to the server, this class keeps track of each
 * client by using ConcurrentHashMap.
 *
 * @see Service
 *
 */
public class Server extends Task<Void> {

    public final int PORT=8000;
    public static ConcurrentHashMap<String, Service> map = new ConcurrentHashMap<>();

    @Override
    protected Void call() throws Exception {

        try
        {
            ServerSocket sc = new ServerSocket(PORT);
            while (true)
            {
                Socket s = sc.accept();

                System.out.println("Har akseptert client connection " + s.getInetAddress().getHostAddress());

                Service sd = new Service(s, s.getInetAddress().getHostAddress() );
                sd.start();
            }
        }
        catch (IOException | SecurityException e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * This method starts a new thread which listens to incoming connections.
     * This method enables multiple clients to be
     * logged into one server.
     */
    public void start()
    {
        Thread th= new Thread(this);
        th.setDaemon(true);
        th.start();
    }
}
