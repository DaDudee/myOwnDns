import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class UDP {
    private DatagramSocket socket;

    public UDP(int port) throws IOException {
        socket = new DatagramSocket(port);
    }

    public void posiljajInSprejemaj() throws IOException {
        byte[] buf = new byte[512];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

        // cakamo na paket
        socket.receive(datagramPacket);
        System.out.println("Received data from: " + datagramPacket.getAddress() + ":" + datagramPacket.getPort());

        // prvih 12 byteov, za header
        byte[] respHeaderBytes = new byte[12];
        System.arraycopy(datagramPacket.getData(), 0, respHeaderBytes, 0, 12);
        DNSHeader receivedHeader = DNSHeader.fromBytes(respHeaderBytes);

        // dobimo question section (vse po 12. bajtu)
        int questionLength = datagramPacket.getLength() - 12;
        byte[] questionBytes = new byte[questionLength];
        System.arraycopy(datagramPacket.getData(), 12, questionBytes, 0, questionLength);

        System.out.println("Header (ID) : " + receivedHeader.getId());

        // odgovorimo z istim ID
        DNSHeader responseHeader = new DNSHeader(
                receivedHeader.getId(), // isti ID
                true,           // qr = response
                (short) 0,
                false,
                false,
                true,
                true,
                (short) 0,
                (short) 0,
                (short) 1, // QDCOUNT ostane 1
                (short) 0,
                (short) 0,
                (short) 0
        );
        byte[] responseHeaderBytes = responseHeader.toBytes();

        // združimo header in question section (ni vec sporočila)
        ByteBuffer responseBuffer = ByteBuffer.allocate(responseHeaderBytes.length + questionBytes.length);
        responseBuffer.put(responseHeaderBytes);
        responseBuffer.put(questionBytes);
        byte[] responseData = responseBuffer.array();

        // pošljemo
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, datagramPacket.getSocketAddress());
        socket.send(responsePacket);
        System.out.println("Sent data to: " + responsePacket.getAddress() + ":" + responsePacket.getPort());
    }

    public void close(){
        socket.close();
    }

    public int getPort(){
        return socket.getLocalPort();
    }
}
