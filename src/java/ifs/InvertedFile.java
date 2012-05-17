/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ifs;
import imageCommon.Keypoint;
import java.util.*;
import sift.*;
import censure.*;
import imageCommon.*;
import java.awt.image.BufferedImage;
/**
 *
 * @author cfmak
 */
public class InvertedFile {
    public HashMap<SiftDescriptor, Vector<ImageEntry>> map;
    
    public InvertedFile()
    {
        map = new HashMap<SiftDescriptor, Vector<ImageEntry>>();
    }
    
    //mat - grayscale image
    public void putImageMatrix(int[][] mat, int id)
    {
        int[][] integral = ImageCommon.IntegrateBoxImage(mat);
        
        for(int i=0; i<3; i++)
        {
            for(int j=0; j<3; j++)
            {
                Keypoint kp = new Keypoint(j*85+42, i*85+42, 7, 1);
                
                SiftDescriptor desc = Sift.GetDescriptor(kp, integral);
                
                ImageEntry entry = new ImageEntry(id, desc.GetHammingEmbedding());
                put(desc, entry);
            }
        }
    }
    
    public void putBufferedImage(BufferedImage img, int imageSize, int id)
    {
        img = ImageCommon.Resize(img, imageSize, imageSize);
        
        int[] rgbs = new int[imageSize*imageSize]; //argb format
        img.getRGB(0, 0, imageSize, imageSize, rgbs, 0, imageSize);
        
        int[][] gray = ImageCommon.GrayScale(rgbs, imageSize, imageSize);
        
        this.putImageMatrix(gray, id);
    }
    
    public void put(SiftDescriptor desc, ImageEntry entry)
    {
        Vector<ImageEntry> v = null;
        if(!map.containsKey(desc))
        {
            v = new Vector(1);
            map.put(desc, v);
        }
        else
            v = map.get(desc);
        v.add(entry);
        Collections.sort(v);
    }
    
    //idf - return the log inverse document frequency
    //assume ImageEntry vector sorted
    public double idf(SiftDescriptor desc)
    {
        if(!map.containsKey(desc))
        {
            return 0; //return 0 so that tf*idf = 0
        }
        
        int df = 1;
        Vector<ImageEntry> v = map.get(desc);
        for(int i=1;i<v.size();i++)
        {
            if(v.elementAt(i).compareTo(v.elementAt(i-1)) != 0)
                df++;
        }
        return 1+Math.log(map.size()/(float)df);
    }
    
    //return the closest codeword (which satisfies hamming embedding) nearest to the query
    public SiftDescriptor NearesrNeighbor(SiftDescriptor query)
    {
        Set<SiftDescriptor> keys = map.keySet();
        Object[] arr = keys.toArray();
        
        SiftDescriptor minDesc = null;
        float minDist = Float.POSITIVE_INFINITY;
        
        for(int i=0;i<arr.length;i++)
        {
            SiftDescriptor s = (SiftDescriptor)arr[i];
            float d = s.DistanceSquared(query);
            
            if(d < minDist)
            {
                minDist = d;
                minDesc = s;
            }
        }
        
        if(map.containsKey(minDesc))
        {
            Vector<ImageEntry> v = map.get(minDesc);
            for(int i=0;i<v.size();i++)
            {
                int[] hamming=v.elementAt(i).GetHammingEmbedding();
                int[] qhamming=query.GetHammingEmbedding();       
                int d = Integer.bitCount(hamming[0]^qhamming[0]) +
                        Integer.bitCount(hamming[1]^qhamming[1]);
                
                if(d <= 24)
                    return minDesc;
            }
        }
        return null;
    }
    
    public Vector<ImageEntry> get(SiftDescriptor codeword)
    {
        return (Vector<ImageEntry>)map.get(codeword);
    }
    
    public int Query(SiftDescriptor[] query)
    {
        HashMap<Integer, Integer> score = new HashMap<Integer, Integer>();
        for(int i=0;i<query.length;i++)
        {
            SiftDescriptor codeword = NearesrNeighbor(query[i]);
            
            Vector<ImageEntry> ie = get(codeword);
            for(int j=0;j<ie.size();j++)
            {
                Integer s = score.get(ie.elementAt(j).GetID());
                if(s!=null)
                    s = new Integer(s.intValue()+1);
                else
                    s = new Integer(1);
                score.put(ie.elementAt(j).GetID(), s);
            }
        }
        
        int maxScore = 0;
        int maxId = 0;
        Object[] keys = score.keySet().toArray();
        for(int i=0;i<keys.length;i++)
        {
            if(score.get((Integer)keys[i])>maxScore)
            {
                maxScore = score.get((Integer)keys[i]).intValue();
                maxId = ((Integer)keys[i]).intValue();
            }
        }
        return maxId;
    }
}
