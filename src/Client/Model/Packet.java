package Client.Model;


import java.io.Serializable;

/**
 * This class contains all the packets exchanged between client and server.
 * Each packet contains a unique packet id and a string message/data.
 *
 */
public class Packet implements Serializable
{

    static final long serialVersionUID = 69L;

    /**
     * All predefined packet id a packet can have.
     */
    public enum PacketId
    {
        LOGIN                           ((byte) 10),
        LOGIN_OK                        ((byte) 11),
        WRONG_LOGIN                     ((byte) 19),
        REGISTER                        ((byte) 20),
        REGISTER_OK                     ((byte) 21),
        USERNAME_TAKEN                  ((byte) 29),
        USER_LIST_REQUEST               ((byte) 30),
        USER_LIST                       ((byte) 31),
        CHAT_CONNECTION_REQUEST_SERVER  ((byte) 40),
        CHAT_CONNECTION_REQUEST_CLIENT  ((byte) 41),
        CHAT_CONNECTION_ACCEPTED        ((byte) 42),
        CHAT_CONNECTION_INFORMATION     ((byte) 43),
        CHAT_CONNECTION_REFUSED         ((byte) 49),
        CHAT_MESSAGE                    ((byte) 50),
        CHAT_STOP                       ((byte) 58),
        CHAT_STOP_ACKNOWLEDGED          ((byte) 59),
        CHANGE_STATUS_BUSY              ((byte) 60),
        CHANGE_STATUS_ONLINE            ((byte) 61),
        BAD_REQUEST                     ((byte) 127);

        private final byte id;

        PacketId(byte id)
        {
            this.id = id;
        }

    }

    private PacketId packetId;
    private String message;

    /**
     * This constructs a packet by specifying packet id and message/data.
     *
     * @param packetId the packet id of this packet
     * @param message the message of this packet
     */
    public Packet(PacketId packetId, String message)
    {
        this.packetId = packetId;
        this.message  = message;

    }

    /**
     * This method returns the packet id of this packet
     *
     * @return the packet id of this packet
     */
    public PacketId getPacketId() {
        return packetId;
    }

    /**
     * This method returns the message of this packet
     *
     * @return the message of this packet
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString()
    {
        return getPacketId() + ": " + getMessage();
    }
}
