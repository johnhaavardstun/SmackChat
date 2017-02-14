package Client.Model;

import Server.Model.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Ali on 13.02.2017.
 */
public class ChatSession extends Task<Void> {

    Socket sc;
    String ip;
    int port;
    ServerSocket ssc;

PrintWriter pwriter;


//To contact
public ChatSession(String ip, int port)
{

    this.ip=ip;
    this.port=port;
    try {
        System.out.println("connecter");
        this.sc= new Socket(ip,port);
        System.out.println("ferdig connected");

    } catch (IOException e) {
        e.printStackTrace();
    }

}

//to make yourself connectable bang bing bong
    public ChatSession( )
    {

        try {
            this.ssc= new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected Void call() throws Exception {
            if(sc == null)
                        sc=ssc.accept();


               System.out.println("kommer seg forbi");
                pwriter= new PrintWriter(sc.getOutputStream());
        messageIn min= new messageIn(sc);
        min.start();
        System.out.println("ferdig!");

        return  null;

    }


    public void start()
    {
        Thread t= new Thread(this);
        t.start();
        t.setUncaughtExceptionHandler((thr, e) -> {
            this.setException(e);
        });

    }
    public void sendMessage(String massage)
    {
        pwriter.write(massage);
        pwriter.flush();

    }


    public String getServerIP()
 {

     String s= null;
     try {
         s = ssc.getInetAddress().getLocalHost().getHostAddress();
     } catch (UnknownHostException e) {
         e.printStackTrace();
     }
     return s;
 }

 public int getServerPort()
 {
     int i=ssc.getLocalPort();
     return i;
 }



    private class messageIn extends javafx.concurrent.Service<Void>
    {
        Socket socket;


        private messageIn(Socket socket)
        {
            this.socket=socket;
        }

        @Override
        protected Task<Void> createTask() {
            Task<Void> task= new Task<Void>() {


                @Override
                protected Void call() throws Exception {


                    try {

                        BufferedReader iin= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String s;
                        while ((true)) {

                            System.out.println("leser meldinger");

                            if((s=iin.readLine())!=null)
                            {
                                updateMessage("MESSAGE!"+s);
                                System.out.print(s);
                            }



                            System.out.println("melding lest");


                        }

                    } catch (IOException e) {
                        this.updateMessage("CHATCLOSED");
//                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return  null;

                }

            };



            return task;
        }
    }


}





