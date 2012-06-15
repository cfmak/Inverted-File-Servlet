/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ifs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.WritableComparable;

/**
 *
 * @author cfmak
 */
public class ImageEntryWritable implements WritableComparable{
//    private byte[] data;
    private int id;
    private int[] hamming;
    
    public ImageEntryWritable() {
        hamming = new int[2];
    }
    
    public ImageEntryWritable(int imageId, int[] he)
    {
        id = imageId;
        hamming = new int[2];
        hamming[0] = he[0];
        hamming[1] = he[1];
    }
    
    public ImageEntryWritable(byte[] imageId, byte[] he)
    {
        id = Integer.parseInt(Bytes.toString(imageId));
        hamming = new int[2];
        hamming[0] = he[0];
        hamming[1] = he[1];
        
        ByteBuffer bb = ByteBuffer.wrap(he);
        IntBuffer ib = ((ByteBuffer) bb.rewind()).asIntBuffer();
        ib.get(hamming, 0, 2);
    }
    
    public static int SizeInBytes()
    {
        return 12;
    }    
    
    public int GetID()
    {
        return id;
    }
    
    public int[] GetHammingEmbedding()
    {
        return hamming.clone();
    }
    
    public static int TotalLengthInBytes()
    {
        return Integer.SIZE/8*3;
    }
    
    public static int HammingLengthInBytes()
    {
        return Integer.SIZE/8*2;
    }
    
    public byte[] HammingToBytes()
    {
        byte[] bytes = new byte[HammingLengthInBytes()];
        
        byte[] tmp2 = ByteBuffer.allocate(4).putInt(hamming[0]).array();
        byte[] tmp3 = ByteBuffer.allocate(4).putInt(hamming[1]).array();
        
        int i=0;
        for(int j=0;j<4;j++)
            bytes[i++] = tmp2[j];
        for(int j=0;j<4;j++)
            bytes[i++] = tmp3[j];
        
        return bytes;
    }
    
    //return desc and hamming as a byte array
//    public byte[] toBytes()
//    {
//        byte[] bytes = new byte[TotalLengthInBytes()];
//        
//        byte[] tmp1 = ByteBuffer.allocate(4).putInt(id).array();
//        byte[] tmp2 = ByteBuffer.allocate(4).putInt(hamming[0]).array();
//        byte[] tmp3 = ByteBuffer.allocate(4).putInt(hamming[1]).array();
//        
//        int i=0;
//        for(int j=0;j<4;j++)
//            bytes[i++] = tmp1[j];
//        for(int j=0;j<4;j++)
//            bytes[i++] = tmp2[j];
//        for(int j=0;j<4;j++)
//            bytes[i++] = tmp3[j];
//        
//        return bytes;
//    }
//    
//    public void fromBytes(byte[] b) throws IOException
//    {
//        if(b.length != TotalLengthInBytes())
//            throw new IOException("fromBytes - wrong length of input byte[] b");
//        ByteBuffer bb = ByteBuffer.wrap(b);
//        IntBuffer ib = ((ByteBuffer) bb.rewind()).asIntBuffer();
//        ib.get(id);
//        ib.get(hamming[0]);
//        ib.get(hamming[1]);
//    }
    
    public Object clone()
    {
        return new ImageEntryWritable(id, hamming);
    }

    @Override
    public void write(DataOutput d) throws IOException {
        System.out.println("write: "+id);
        d.writeInt(id);
        d.writeInt(hamming[0]);
        d.writeInt(hamming[1]);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        id = di.readInt();
        System.out.println("read: "+id);
        hamming[0]=di.readInt();
        hamming[1]=di.readInt();
    }

    @Override
    public int compareTo(Object t) {
        ImageEntryWritable o = (ImageEntryWritable)t;
        if(id<o.id)
            return -1;
        else if(id>o.id)
            return 1;
        return 0;
    }
    
    public String toString()
    {
        return ""+id;
    }
}
