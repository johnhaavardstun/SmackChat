package Client.Model;


import java.io.Serializable;

/**
 * This class contains all the packets exchanged between client and server.
 * Each packet contains a unique packet id and a string message/data.
 *
 * Created by Ali on 30.01.2017.
 * @version IntelliJ IDEA 2016.3.4
 */
public class Packet implements Serializable{


    static final long serialVersionUID = 69L;

    /**
     * All predefined packet id a packet can have.
     */
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
     CONNECTIONINFORMATION( (byte) 350),
     CHATMESSAGE((byte) 400),
     CHANGEBUSY((byte) 480),
     CHANGEONLINE((byte) 485);

     private final byte ID;

     private Packetid(byte id)
     {
         this.ID=id;
     }

 }

    private Packetid packetid;
    private String message;

    /**
     * This constructs a packet by specifying packet id and message/data.
     *
     * @param packetid the packet id of this packet
     * @param message the message of this packet
     */
    public Packet(Packetid packetid,String message)
    {
        this.packetid=packetid;
        this.message=message;

    }

    /**
     * This method returns the packet id of this packet
     *
     * @return the packet id of this packet
     */
    public Packetid getPacketid() {
        return packetid;
    }

    /**
     * This method returns the message of this packet
     *
     * @return the message of this packet
     */
    public String getMessage() {
        return message;
    }
}
