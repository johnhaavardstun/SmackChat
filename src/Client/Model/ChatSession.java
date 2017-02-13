package Client.Model;

import Server.Model.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
        this.sc= new Socket(ip,port);
    } catch (IOException e) {
        e.printStackTrace();
    }

}

//to make yourself connectable bang bing bong
    public ChatSession( )
    {

        this.port=port;
        try {
            this.ssc= new ServerSocket(0);
            this.sc=ssc.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected Void call() throws Exception {
        pwriter= new PrintWriter(sc.getOutputStream());
        messageIn min= new messageIn(sc);
        min.start();

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



    public String getServerIP()
 {

     String s=ssc.getInetAddress().toString();
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

   public void sendMessage( String massage)
   {
       pwriter.write(massage);
    pwriter.flush();

   }
}





