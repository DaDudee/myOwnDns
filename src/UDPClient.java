import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UDPClient {
    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            DNSQuestion question = new DNSQuestion("example.com", (short) 1, (short) 1);
            byte[] questionSection = question.toBytes();

            DNSHeader header = new DNSHeader(
                    (short) 0x04D2,  // ID (1234)
                    false,           // qr = query
                    (short) 0,       // opcode
                    false,           // aa
                    false,           // tc
                    true,            // rd
                    false,           // ra
                    (short) 0,       // z
                    (short) 0,       // rcode
                    (short) 1,       // QDCOUNT
                    (short) 0,       // ANCOUNT
                    (short) 0,       // NSCOUNT
                    (short) 0        // ARCOUNT
            );
            byte[] headerBytes = header.toBytes();

            //HEADER in VPRAŠANJE
            ByteBuffer buffer = ByteBuffer.allocate(headerBytes.length + questionSection.length);
            buffer.put(headerBytes);
            buffer.put(questionSection);
            byte[] sendData = buffer.array();

            //poslji
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, 2053);
            socket.send(packet);
            System.out.println("Sent DNS query for: " + question.getQname());

            //cakaj na sporocilo
            byte[] responseBuf = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(responseBuf, responseBuf.length);
            socket.receive(responsePacket);

            //izpisi response
            byte[] respHeaderBytes = new byte[12];
            System.arraycopy(responsePacket.getData(), 0, respHeaderBytes, 0, 12);
            DNSHeader responseHeader = DNSHeader.fromBytes(respHeaderBytes);

            System.out.println("Response Header ID: " + responseHeader.getId());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
