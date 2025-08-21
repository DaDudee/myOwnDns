import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UDPClient {

    public static void main(String[] args) {
        DatagramSocket socket = null;

        try{
            // Priprava question section
            byte[] questionSection = new byte[]{
                    0x0c, 'c','o','d','e','c','r','a','f','t','e','r','s',
                    0x02, 'i','o',
                    0x00,
                    0x00, 0x01, // Type A
                    0x00, 0x01  // Class IN
            };

            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");

            // Header
            DNSHeader header = new DNSHeader(
                    (short) 0x04D2, // id (1234)
                    false,          // qr (query)
                    (short) 0,
                    false,
                    false,
                    true,
                    false,
                    (short) 0,
                    (short) 0,
                    (short) 1, // QDCOUNT = 1
                    (short) 0,
                    (short) 0,
                    (short) 0
            );
            byte[] headerBytes = header.toBytes();

            // Združimo header in question section
            ByteBuffer buffer = ByteBuffer.allocate(headerBytes.length + questionSection.length);
            buffer.put(headerBytes);
            buffer.put(questionSection);
            byte[] sendData = buffer.array();

            // Pošljemo
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, 2053);
            socket.send(packet);
            System.out.println("Sent DNSHeader with question section");

            // Čakamo na paket (odgovor)
            byte[] responseBuf = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(responseBuf, responseBuf.length);
            socket.receive(responsePacket);

            // Prvih 12 byteov za header
            byte[] respHeaderBytes = new byte[12];
            System.arraycopy(responsePacket.getData(), 0, respHeaderBytes, 0, 12);
            DNSHeader responseHeader = DNSHeader.fromBytes(respHeaderBytes);

            // Izpišemo samo ID
            System.out.println("Header (ID) : " + responseHeader.getId());

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }
    }
}
