import java.nio.ByteBuffer;

public class DNSHeader {
    private short id;       //A random ID assigned to query packets. Response packets must reply with the same ID.
    private boolean qr;     //1 for a reply packet, 0 for a question packet.
    private short opcode;   //Specifies the kind of query in a message.
    private boolean aa;     //1 if the responding server "owns" the domain queried, i.e., it's authoritative.
    private boolean tc;     //1 if the message is larger than 512 bytes. Always 0 in UDP responses.
    private boolean rd;     //Sender sets this to 1 if the server should recursively resolve this query, 0 otherwise.
    private boolean ra;     //Server sets this to 1 to indicate that recursion is available.
    private short z;        //Used by DNSSEC queries. At inception, it was reserved for future use.
    private short rcode;    //Response code indicating the status of the response.
    private short qdCount;  //Number of questions in the Question section.
    private short anCount;  //Number of records in the Answer section.
    private short nsCount;  //Number of records in the Authority section.
    private short arCount;  //Number of records in the Additional section.

    public DNSHeader(short id, boolean qr, short opcode, boolean aa, boolean tc, boolean rd, boolean ra, short z, short rcode, short qdCount, short anCount, short nsCount, short arCount) {
        this.id = id;
        this.qr = qr;
        this.opcode = opcode;
        this.aa = aa;
        this.tc = tc;
        this.rd = rd;
        this.ra = ra;
        this.z = z;
        this.rcode = rcode;
        this.qdCount = qdCount;
        this.anCount = anCount;
        this.nsCount = nsCount;
        this.arCount = arCount;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putShort(id);

        int flags = 0;
        flags |= (qr ? 1 : 0) << 15;    //ISTO, samo left shift in ali (|)
        flags |= (opcode & 0xF) << 11;
        flags |= (aa ? 1 : 0) << 10;
        flags |= (tc ? 1 : 0) << 9;
        flags |= (rd ? 1 : 0) << 8;
        flags |= (ra ? 1 : 0) << 7;
        flags |= (z & 0x7) << 4;
        flags |= (rcode & 0xF);

        buffer.putShort((short) flags);
        buffer.putShort(qdCount);
        buffer.putShort(anCount);
        buffer.putShort(nsCount);
        buffer.putShort(arCount);
        return buffer.array();
    }

    public static DNSHeader fromBytes(byte[] data) {
        if (data.length < 12) {
            throw new IllegalArgumentException("Premalo bajtov za DNS header!");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        short id = buffer.getShort();
        short flags = buffer.getShort();

        boolean qr = (flags >> 15 & 1) == 1;    //preveri ali je bit 1 na zadnjem mestu, maskira vse razen 1
        short opcode = (short) ((flags >> 11) & 0xF);   //4 bite premaknemo na zadnja mesta
        boolean aa = (flags >> 10 & 1) == 1;    //preveri ali je 10. bit enak 1
        boolean tc = (flags >> 9 & 1) == 1;     //ali je 9. bit 1
        boolean rd = (flags >> 8 & 1) == 1;     //....
        boolean ra = (flags >> 7 & 1) == 1;
        short z = (short) ((flags >> 4) & 0x7); //premakne za 4 bite, ohrani zadnja 3 mesta
        short rcode = (short) (flags & 0xF);    // pobere zadnje 4 bite iz "flags"

        short qdCount = buffer.getShort();
        short anCount = buffer.getShort();
        short nsCount = buffer.getShort();
        short arCount = buffer.getShort();

        return new DNSHeader(id, qr, opcode, aa, tc, rd, ra, z, rcode, qdCount, anCount, nsCount, arCount);
    }

    public short getId() { return id; }
    public void setId(short id) { this.id = id; }

    public boolean isQr() { return qr; }
    public void setQr(boolean qr) { this.qr = qr; }

    public short getOpcode() { return opcode; }
    public void setOpcode(short opcode) { this.opcode = opcode; }

    public boolean isAa() { return aa; }
    public void setAa(boolean aa) { this.aa = aa; }

    public boolean isTc() { return tc; }
    public void setTc(boolean tc) { this.tc = tc; }

    public boolean isRd() { return rd; }
    public void setRd(boolean rd) { this.rd = rd; }

    public boolean isRa() { return ra; }
    public void setRa(boolean ra) { this.ra = ra; }

    public short getZ() { return z; }
    public void setZ(short z) { this.z = z; }

    public short getRcode() { return rcode; }
    public void setRcode(short rcode) { this.rcode = rcode; }

    public short getQdCount() { return qdCount; }
    public void setQdCount(short qdCount) { this.qdCount = qdCount; }

    public short getAnCount() { return anCount; }
    public void setAnCount(short anCount) { this.anCount = anCount; }

    public short getNsCount() { return nsCount; }
    public void setNsCount(short nsCount) { this.nsCount = nsCount; }

    public short getArCount() { return arCount; }
    public void setArCount(short arCount) { this.arCount = arCount; }
}
