/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brief;

/**
 *
 * @author cfmak
 */



public class BriefDescriptor {
    public int[] desc;
    
    public BriefDescriptor(int bytes)
    {
        desc = new int[bytes/4];
    }
    
    public void SetBit(int x, int pos)
    {
        desc[pos/32] |= (x<<(pos%32));
    }
    
    public int Distance(BriefDescriptor other) throws DescriptorMismatchException
    {
        if(this.desc.length!=other.desc.length)
            throw new DescriptorMismatchException(this.desc.length, other.desc.length);
        
        int d = 0;
        for(int i=0;i<desc.length;i++)
        {
            d+= Integer.bitCount(desc[i]^other.desc[i]); //hamming distance
        }
        return d;
    }
    
    public String toString()
    {
        String s="";
        for(int i=desc.length-1;i>=0;i--)
        {
            s = desc[i]+", "+s; //hamming distance
        }
        return s;
    }
}
