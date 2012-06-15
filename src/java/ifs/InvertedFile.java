/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ifs;
import fs.FileSystemConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sift.SiftDescriptor;

/**
 *
 * @author cfmak
 */
public class InvertedFile {
    protected ConcurrentHashMap<SiftDescriptor, ArrayList<ImageEntryWritable>> map;
    protected FileSystemConfig fsconf;
    
    public InvertedFile(FileSystemConfig conf)
    {
        map = new ConcurrentHashMap<SiftDescriptor, ArrayList<ImageEntryWritable>>();
        fsconf = conf;
    }
    
    public ArrayList<ImageEntryWritable> get(SiftDescriptor codeword)
    {
        return (ArrayList<ImageEntryWritable>)map.get(codeword);
    }
    
    public ArrayList<ImageEntryWritable> put(SiftDescriptor desc, ImageEntryWritable entry, boolean sortEntryVector)
    {
        ArrayList<ImageEntryWritable> v = null;
        if(!map.containsKey(desc))
        {
            v = new ArrayList(1);
            map.put(desc, v);
        }
        else
            v = map.get(desc);
        v.add(entry);
        if(sortEntryVector)
            Collections.sort(v);
        return v;
    }
    
    public void SortImageEntryVector(SiftDescriptor desc)
    {
        if(map.containsKey(desc))
        {
            ArrayList<ImageEntryWritable> v = map.get(desc);
            Collections.sort(v);
        }
    }
    
    //idf - return the log inverse document frequency
    //assume ImageEntryWritable ArrayList sorted
    public double idf(SiftDescriptor desc)
    {
        if(!map.containsKey(desc))
        {
            return 0; //return 0 so that tf*idf = 0
        }
        
        int df = 1;
        ArrayList<ImageEntryWritable> v = map.get(desc);

        for(int i=1;i<v.size();i++)
        {
            if(v.get(i).compareTo(v.get(i-1)) != 0)
                df++;
        }
        return 1+Math.log(map.size()/(float)df);
    }
    
    //return the closest codeword (which satisfies hamming embedding) nearest to the query
    public SiftDescriptor NearestNeighbor(SiftDescriptor query)
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
            ArrayList<ImageEntryWritable> v = map.get(minDesc);
            for(int i=0;i<v.size();i++)
            {
                int[] hamming=v.get(i).GetHammingEmbedding();
                int[] qhamming=query.GetHammingEmbedding();       
                int d = Integer.bitCount(hamming[0]^qhamming[0]) +
                        Integer.bitCount(hamming[1]^qhamming[1]);
                
                if(d <= 24)
                    return minDesc;
            }
        }
        return null;
    }
    
    
    
    public int Query(SiftDescriptor[] query)
    {
        HashMap<Integer, Integer> score = new HashMap<Integer, Integer>();
        for(int i=0;i<query.length;i++)
        {
            SiftDescriptor codeword = NearestNeighbor(query[i]);
            
            ArrayList<ImageEntryWritable> ie = get(codeword);
            for(int j=0;j<ie.size();j++)
            {
                Integer s = score.get(ie.get(j).GetID());
                if(s!=null)
                    s = new Integer(s.intValue()+1);
                else
                    s = new Integer(1);
                score.put(ie.get(j).GetID(), s);
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
