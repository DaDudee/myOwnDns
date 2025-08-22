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

        // cakamo na paket
        byte[] buf = new byte[512];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        socket.receive(datagramPacket);
        System.out.println("Received data from: " + datagramPacket.getAddress() + ":" + datagramPacket.getPort());

        // prvih 12 byteov, za HEADER
        byte[] respHeaderBytes = new byte[12];
        System.arraycopy(datagramPacket.getData(), 0, respHeaderBytes, 0, 12);
        DNSHeader receivedHeader = DNSHeader.fromBytes(respHeaderBytes);

        // dobimo QUESTION section (vse po 12. bajtu)
        int questionLength = datagramPacket.getLength() - 12;
        byte[] questionBytes = new byte[questionLength];
        System.arraycopy(datagramPacket.getData(), 12, questionBytes, 0, questionLength);
        DNSQuestion question = DNSQuestion.fromBytes(ByteBuffer.wrap(questionBytes));

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

        //zgradimo answer
        byte[] ipBytes = {(byte)127, 0, 0, 1};  // RDATA (IPv4)

        DNSAnswer answer = new DNSAnswer(
                question.getQname(), // same name as question
                (short) 1,           // TYPE A
                (short) 1,           // CLASS IN
                60,                  // TTL (seconds)
                ipBytes
        );

        byte[] responseBytes = answer.toBytes();

        //vse skupaj
        ByteBuffer buffer = ByteBuffer.allocate(respHeaderBytes.length + questionBytes.length + responseBytes.length);
        buffer.put(respHeaderBytes);
        buffer.put(questionBytes);
        buffer.put(responseHeaderBytes);
        byte[] responseData = buffer.array();

        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,  datagramPacket.getAddress(), datagramPacket.getPort());
        socket.send(responsePacket);
        System.out.println("Sent to: " + question.getQname());
    }

    public void close(){
        socket.close();
    }

    public int getPort(){
        return socket.getLocalPort();
    }
}
