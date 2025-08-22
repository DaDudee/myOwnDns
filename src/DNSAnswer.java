import java.nio.ByteBuffer;

public class DNSAnswer {
    private String name;    //domain name (or pointer)
    private short atype;    //TYPE – 2 bytes
    private short aclass;   //CLASS – 2 bytes
    private int ttl;        //TTL – 4 bytes (Time to Live)
    private byte[] rdata;   //RDATA – variable length (resource data)

    public DNSAnswer(String name, short atype, short aclass, int ttl, byte[] rdata) {
        this.name = name;
        this.atype = atype;
        this.aclass = aclass;
        this.ttl = ttl;
        this.rdata = rdata;
    }

    public byte[] toBytes(){
        byte[] biti = Transcoding.encodeDNS(this.name);
        ByteBuffer buf = ByteBuffer.allocate(biti.length + 2 + 2 + 4 + 2 + rdata.length);
        buf.put(biti);
        buf.putShort(this.atype);
        buf.putShort(this.aclass);
        buf.putInt(this.ttl);
        buf.putShort((short)rdata.length);
        buf.put(rdata);
        return buf.array();
    }

    public DNSAnswer fromBytes(ByteBuffer buf){
        String name = Transcoding.decodeDNS(buf);
        short at = buf.getShort();
        short ac = buf.getShort();
        int ttl = buf.getInt();
        short rdlength = buf.getShort();
        byte[] rdata = new byte[rdlength];
        buf.get(rdata);
        return new DNSAnswer(name,at,ac,ttl,rdata);
    }
}
