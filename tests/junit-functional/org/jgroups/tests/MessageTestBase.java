package org.jgroups.tests;

import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.protocols.PingHeader;
import org.jgroups.protocols.TpHeader;
import org.jgroups.protocols.pbcast.NakAckHeader2;
import org.jgroups.util.Util;
import org.testng.Assert;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Supplier;

/**
 * Base class for common methods
 * @author Bela Ban
 * @since  5.0.0
 */
public class MessageTestBase {
    protected static final short UDP_ID=101;
    protected static final short PING_ID=102;
    protected static final short NAKACK_ID=103;

    protected static void addHeaders(Message msg) {
        TpHeader tp_hdr=new TpHeader("DemoChannel2");
        msg.putHeader(UDP_ID, tp_hdr);
        PingHeader ping_hdr=new PingHeader(PingHeader.GET_MBRS_REQ).clusterName("demo-cluster");
        msg.putHeader(PING_ID, ping_hdr);
        NakAckHeader2 nak_hdr=NakAckHeader2.createXmitRequestHeader(Util.createRandomAddress("S"));
        msg.putHeader(NAKACK_ID, nak_hdr);
    }

    protected static Message makeReply(Message msg) {
        Message reply=msg.create().get().setDest(msg.getSrc());
        if(msg.getDest() != null)
            reply.setSrc(msg.getDest());
        return reply;
    }

    protected static void _testSize(Message msg) throws Exception {
        int size=msg.size();
        byte[] serialized_form=Util.streamableToByteBuffer(msg);
        System.out.println("size=" + size + ", serialized size=" + serialized_form.length);
        Assert.assertEquals(size, serialized_form.length);
    }

    protected static byte[] marshal(Message msg) throws Exception {
        return Util.streamableToByteBuffer(msg);
    }

    protected static Message unmarshal(Class<? extends Message> cl, byte[] buf) throws Exception {
        return Util.streamableFromByteBuffer(cl, buf);
    }


    protected static class DummyHeader extends Header {
        protected short num;

        public DummyHeader() {}
        public DummyHeader(short num) {this.num=num;}

        public short                      getMagicId() {return 1600;}
        public Supplier<? extends Header> create() {return DummyHeader::new;}
        public short  getNum()                                 {return num;}
        public int    serializedSize()                         {return 0;}
        public void   writeTo(DataOutput out) throws Exception {}
        public void   readFrom(DataInput in)  throws Exception {}
        public String toString()                               {return "DummyHeader(" + num + ")";}
    }
}