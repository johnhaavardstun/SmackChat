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

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String chattingWith;


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

//to make yourself connectable
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

        System.out.println("Doing stuff");
        oos = new ObjectOutputStream(sc.getOutputStream());
        ois = new ObjectInputStream((sc.getInputStream()));
        messageIn min= new messageIn(this);
        min.start();
        System.out.println("Done!");

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


    public void  sendData(Packet packet) throws IOException {
        System.out.println("Sending data");
        oos.writeObject(packet);
        oos.flush();
    }


    public String getServerIP()
    {
        String s= null;
        s = ssc.getInetAddress().getHostAddress();
        return s;
    }

     public int getServerPort()
     {
         int i=ssc.getLocalPort();
         return i;
     }
     public void setChatter(String s)
     {
         chattingWith=s;
     }

    public String getChattingWith()
    {
        return chattingWith;
    }


    private class messageIn extends javafx.concurrent.Service<Void>
    {
        ChatSession chat;


        private messageIn(ChatSession chatSession)
        {
            this.chat = chatSession;
        }

        @Override
        protected Task<Void> createTask() {
            Task<Void> task= new Task<Void>() {


                @Override
                protected Void call() throws Exception {


                    try {
                        System.out.println(sc.getLocalAddress() + ":" + sc.getLocalPort() + " --> " + sc.getInetAddress().getHostAddress() + ":" + sc.getPort());


                        while ((true))
                        {
                            Packet packet;
                            if((packet=(Packet)ois.readObject())!=null)
                            {
                                //   System.out.println(packet.getMessage()+"   "+packet.getPacketid());
                                System.out.println("Recived: " + packet.getPacketid() + ":" + packet.getMessage());
                                chat.updateMessage(System.currentTimeMillis() + "@CHATMESSAGE!" + packet.getMessage());

                            }
                        }

                    } catch (IOException e) {
                        chat.updateMessage(System.currentTimeMillis() + "@CHATCLOSED!");

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





