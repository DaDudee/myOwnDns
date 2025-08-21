import java.net.SocketAddress;

public class Packet {
    private byte[] data;
    private SocketAddress packet;

    public Packet(byte[] data, SocketAddress packet){
        this.data = data;
        this.packet = packet;
    }

    public byte[] getData(){
        return data;
    }
    public SocketAddress getAddress(){
        return packet;
    }
}
