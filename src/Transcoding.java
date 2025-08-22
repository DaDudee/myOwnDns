import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Transcoding {
    public static String decodeDNS(ByteBuffer buf){
        StringBuilder dns = new StringBuilder();

        while (true){
            byte dolzina = buf.get();
            if(dolzina == 0) {
                break;
            }

            byte[] label = new byte[dolzina];
            buf.get(label);
            dns.append(new String(label, StandardCharsets.UTF_8)).append('.');
        }

        if(dns.length() > 0){
            dns.setLength(dns.length() - 1);
        }
        return dns.toString();
    }

    //"example.com" -> [7]example[3]com[0]
    public static byte[] encodeDNS(String dns){
        String[] completeDNS = dns.split("\\.");
        ByteBuffer buf = ByteBuffer.allocate(dns.length() + completeDNS.length + 1); // +length bytes + 0

        for(int i = 0; i < completeDNS.length; i++){
            String section = completeDNS[i];
            buf.put((byte)section.length());

            for(int j = 0; j < section.length(); j++){
                buf.put((byte) section.charAt(j));
            }
        }
        buf.put((byte) 0); //konec domen
        return buf.array();
    }
}
