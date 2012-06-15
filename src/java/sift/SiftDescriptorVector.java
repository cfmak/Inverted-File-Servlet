/*
 * This class is here for easy serialization for an array of SiftDescriptor
 */
package sift;
import java.io.IOException;
import java.util.Vector;
/**
 *
 * @author cfmak
 */
public class SiftDescriptorVector extends Vector<SiftDescriptorWritable> 
{   
    public byte[] toBytes()
    {
        int k=0;
        byte[] result = new byte[SiftDescriptorWritable.TotalLengthInBytes()*size()];
        for(int i=0;i<size();i++)
        {
            byte[] tmp = elementAt(i).toBytes();
            for(int j=0;j<tmp.length;j++)
            {
                result[k++] = tmp[j];
            }
        }
        return result;
    }
    
    public void fromBytes(byte[] b) throws IOException
    {
        int n = b.length / SiftDescriptorWritable.TotalLengthInBytes();
        byte[] tmp = new byte[SiftDescriptorWritable.TotalLengthInBytes()];
        int k=0;
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<SiftDescriptorWritable.TotalLengthInBytes();j++)
            {
                tmp[j] = b[k++];
            }
            SiftDescriptorWritable desc = new SiftDescriptorWritable(tmp);
            this.add(desc);
        }
    }
}
