/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ifs;

import sift.SiftDescriptor;
import java.util.*;

/**
 *
 * @author cfmak
 */
public class Kmean {
    private Vector<SiftDescriptor> centers;
    private float[] scores;
    private float totalScore;
    
    public Kmean()
    {
        
    }
    
    //return cluster centers as a Vector of SiftDescriptor
    public Vector<SiftDescriptor> Cluster(Vector<SiftDescriptor> data, int nClusters)
    {
//        k= nClusters;
//        centers = new Vector<SiftDescriptor>();
//        scores = new float[k];
        
        PickStartingPoints(data, nClusters);
        totalScore = Float.POSITIVE_INFINITY;
        return (Vector<SiftDescriptor>) Lloyd(data).clone();
    }
    
    private void PickStartingPoints(Vector<SiftDescriptor> data, int k)
    {
        if(k>data.size())
        {
            k = data.size();
            scores = new float[k];
            centers = (Vector<SiftDescriptor>) data.clone(); //in this case, data.size() should be small
            return;
        }
        
        //pick random starting point for now...
        scores = new float[k];
        centers = new Vector<SiftDescriptor>();
            
        HashSet<Integer> set = new HashSet<Integer>(k);
        Random r = new Random();
        for(int i=0;i<k;i++)
        {
            Integer tmp = new Integer(r.nextInt(data.size()));
            while(!set.add(tmp))//if tmp already exists, get another number
            {
                tmp = new Integer(r.nextInt(data.size()));
            }
            centers.add((SiftDescriptor)data.elementAt(tmp.intValue()).clone());
        }
    }
    
    //return cluster centers as a Vector of SiftDescriptor
    private Vector<SiftDescriptor> Lloyd(Vector<SiftDescriptor> data)
    {
        float oldTotalScore = totalScore;
        totalScore=0;
        for(int j=0;j<centers.size();j++)
        {
            scores[j] = 0;
        }
        for(int i=0;i<data.size();i++)
        {
            float mindist = Float.POSITIVE_INFINITY;
            int mincenter = -1;
            for(int j=0;j<centers.size();j++)
            {
                float d = centers.elementAt(j).DistanceSquared(data.elementAt(j));
                if(d < mindist)
                {
                    mindist = d;
                    mincenter = j;
                }
            }
            
            scores[mincenter] += mindist;
            totalScore+=mindist;
        }
        if( totalScore / oldTotalScore < 0.95f ) //not converged
        {
            return Lloyd(data);
        }
        return centers;
    }
}
