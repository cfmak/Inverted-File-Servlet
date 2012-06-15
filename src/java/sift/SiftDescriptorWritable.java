/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sift;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

/**
 *
 * @author cfmak
 */
public class SiftDescriptorWritable extends SiftDescriptor implements WritableComparable{
   
    public SiftDescriptorWritable()
    {
        desc = new float[SiftLength()];
        hamming = new int[2];
    }
    
    public SiftDescriptorWritable(SiftDescriptorWritable other)
    {
        desc = other.desc.clone();
        hamming = other.hamming.clone();
    }
    
    public SiftDescriptorWritable(float[] sift)
    {
        desc = sift.clone();
        
        hamming = new int[2];
        HammingEmbedding();
    }
    
    public SiftDescriptorWritable(byte[] b) throws IOException
    {
        desc = new float[SiftLength()];
        hamming = new int[2];
        fromBytes(b);
    }
       
    @Override
    public void write(DataOutput out) throws IOException {
        out.write(this.toBytes());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        byte[] bytes = new byte[TotalLengthInBytes()];
        in.readFully(bytes);
        fromBytes(bytes);
    }

    @Override
    public int compareTo(Object t) {
        SiftDescriptorWritable other = (SiftDescriptorWritable)t;
        for(int i=0;i<SiftLength();i++)
        {
            if(desc[i] > other.desc[i])
                return 1;
            else if(desc[i] < other.desc[i])
                return -1;
        }
        return 0;
    }
}
