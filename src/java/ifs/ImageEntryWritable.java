/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ifs;

/**
 *
 * @author cfmak
 */
public class ImageEntry implements Comparable<ImageEntry>{
//    private byte[] data;
    private int id;
    private int[] hamming;
    
    public ImageEntry(int imageId, int[] he)
    {
        id = imageId;
        hamming = new int[2];
        hamming[0] = he[0];
        hamming[1] = he[1];
    }
    
    public int GetID()
    {
        return id;
    }
    
    public int[] GetHammingEmbedding()
    {
        return hamming.clone();
    }
    
    public Object clone()
    {
        return new ImageEntry(id, hamming);
    }
    
    public int compareTo(ImageEntry o)
    {
        if(id<o.id)
            return -1;
        else if(id>o.id)
            return 1;
        return 0;
    }
}
