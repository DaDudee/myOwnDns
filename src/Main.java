import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main {
    public static void main(String[] args) {
        try {
            UDP udpSocket = new UDP(2053);
            System.out.println("UDP poslusa na portu: " + udpSocket.getPort());

            // skos posilja in sprejema
            while (true) {
                udpSocket.posiljajInSprejemaj();
            }
        }
        catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
