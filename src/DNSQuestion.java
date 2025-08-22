import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DNSQuestion {
/*
    public static void main(String[] args) {
        String domena = "test.com";
        System.out.println(domena);

        byte[] biti = encodeDNS(domena);
        System.out.println(Arrays.toString(biti));

        ByteBuffer buf = ByteBuffer.allocate(biti.length);
        buf.put(biti);
        buf.flip(); // reset position before reading

        String domena2 = decodeDNS(buf);
        System.out.println(domena2);
    }
*/
    private String qname;   // Domain name (variable length)
    private short qtype;    // Type of query (16 bits)
    private short qclass;   // Class of query (16 bits)

    public DNSQuestion(String qname, short qtype, short qclass){
        this.qname = qname;
        this.qtype = qtype;
        this.qclass = qclass;
    }

    public byte[] toBytes(){
        byte[] biti = Transcoding.encodeDNS(this.qname);
        ByteBuffer buf = ByteBuffer.allocate(biti.length + 4); //4 za qtype in qclass ... ??
        buf.put(buf);
        buf.putShort(this.qtype);
        buf.putShort(this.qclass);
        return buf.array();
    }

    public static DNSQuestion fromBytes(ByteBuffer buf){
        String domena = Transcoding.decodeDNS(buf);
        short qt = buf.getShort();
        short qc = buf.getShort();
        return new DNSQuestion(domena, qt, qc);
    }

}
