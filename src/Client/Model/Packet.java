package Client.Model;


import java.io.Serializable;

/**
 * Created by Ali on 30.01.2017.
 */
public class Packet implements Serializable{


    static final long serialVersionUID = 69L;

    public enum Packetid
 {
     LOGIN(((byte)50)),
     REGISTER((byte)60),
     WRONGLOGIN((byte)55),
     BADREQUEST((byte)90),
     LOGINOK((byte) 58),
     REGISTEROK((byte) 40),
     USERNAMETAKEN((byte) 61),
     USERLISTREQUEST((byte) 100),
     USERLIST((byte) 101),
     CONNECTIONREQUEST((byte) 300),
     CONNECTIONREFUSED((byte) 305),
     CONNECTIONACCEPTED((byte) 310),
     INCOMINGCONNECTION( (byte) 320),
     CONNECTIONINFORMATION( (byte) 350);

     private final byte ID;

     private Packetid(byte id)
     {
         this.ID=id;
     }

 }

    private Packetid packetid;
    private String message;


    public Packet(Packetid packetid,String message)
    {
        this.packetid=packetid;
        this.message=message;

    }


    public Packetid getPacketid() {
        return packetid;
    }

    public String getMessage() {
        return message;
    }
}
