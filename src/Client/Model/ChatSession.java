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
    private ObjectOutputStream oos;
    private ObjectInputStream ois;


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
        oos = new ObjectOutputStream(sc.getOutputStream());
        ois = new ObjectInputStream((sc.getInputStream()));
        messageIn min= new messageIn(this);
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
        System.out.println("Sender melding: " + massage);
        System.out.println(sc.getLocalAddress() + ":" + sc.getLocalPort() + " --> " + sc.getInetAddress().getHostAddress() + ":" + sc.getPort());
        pwriter.println(massage);
//        pwriter.write(massage);
//        pwriter.flush();

    }


    public void  sendData(Packet packet) throws IOException {
        System.out.println("data bir sendt");
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
//                        System.out.println(socket.getLocalAddress() + ":" + socket.getLocalPort() + " --> " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                        BufferedReader iin= new BufferedReader(new InputStreamReader(sc.getInputStream()));
                        String s;
//                        while (( s = iin.readLine() ) != null) {
//
//                            System.out.println("leser meldinger");
//
//
////                            if()
////                            {
//                                System.out.print("Mottok chat: ");
//                                updateMessage("MESSAGE!"+s);
//                                System.out.println(s);
////                            }
//
//
//
//                            System.out.println("melding lest");
//
//
//                        }

                        while ((true))
                        {
                            Packet packet;
                            if((packet=(Packet)ois.readObject())!=null)
                            {
                                //   System.out.println(packet.getMessage()+"   "+packet.getPacketid());
                                System.out.println("mottatt: " + packet.getPacketid() + ":" + packet.getMessage());
                                chat.updateMessage(System.currentTimeMillis() + "@CHATMESSAGE!" + packet.getMessage());

                            }
                        }

                    } catch (IOException e) {
                        chat.updateMessage(System.currentTimeMillis() + "@CHATCLOSED!");
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





