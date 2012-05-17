/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package censure;

import imageCommon.Keypoint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Vector;
//import java.util.HashSet;

/**
 *
 * @author cfmak
 */
public class CensureDetector {
    //Octagon dimension
    private static int[][] mnInner = 
    {
        {3,0}, {3,1}, {3,2}, {5,2}, {5,3}, {5,4}, {5,5}
    };
        
    private static int[][] mnOuter=
    {
        {5,2},{5,3},{7,3},{9,4},{9,7},{13,7},{15,10}
    };
    
    private static float[] scaleInner=
    {
        0.000435729847494553f, 0.000186741363211951f,
        0.000105988341282459f, 5.68343279340722e-5f,
        4.04285425510410e-5f, 3.03997568019456e-5f,
        2.37670825906120e-5f
    };
        
    private static float[] scaleOuter=
    {
        6.53594771241830e-5f, 5.15995872033024e-5f,
        3.63108206245461e-5f, 2.17864923747277e-5f,
        1.22549019607843e-5f, 8.03600128576021e-6f,
        4.66853408029879e-6f
    };
        
    private static int[] filterSize=
    {
        9,11,13,17,23,27,35
    };
    
    private static int[] extFilterSize=
    {
        17,19,25,29,41,49,65
    };
    
    private CensureParam param;
    
    private int[][] integralBoxImg;
    private int[][] integralSlant1Img; // slant /
    private int[][] integralSlant2Img; // slant \
    
    private float[][][] filterResponse;
    
    private int[][] buf;
    private int bufx;
    private int bufy;
    
    public void SetCensureParameters(CensureParam p)
    {
        param = p;
    }
    
    public CensureDetector(CensureParam p)
    {
        param = p;
//        mnInner = new int[7][2];
//        mnOuter = new int[7][2];
//        scaleOuter = new float[7];
//        scaleInner = new float[7];
//        filterSize = new int[7];
//        extFilterSize = new int[7];
//        
//        mnInner[0][0]=3; //m
//        mnInner[0][1]=0; //n
//        mnInner[1][0]=3;
//        mnInner[1][1]=1;
//        mnInner[2][0]=3;
//        mnInner[2][1]=2;
//        mnInner[3][0]=5;
//        mnInner[3][1]=2;
//        mnInner[4][0]=5;
//        mnInner[4][1]=3;
//        mnInner[5][0]=5;
//        mnInner[5][1]=4;
//        mnInner[6][0]=5;
//        mnInner[6][1]=5;
        
//        mnOuter[0][0]=5;
//        mnOuter[0][1]=2;
//        mnOuter[1][0]=5;
//        mnOuter[1][1]=3;
//        mnOuter[2][0]=7;
//        mnOuter[2][1]=3;
//        mnOuter[3][0]=9;
//        mnOuter[3][1]=4;
//        mnOuter[4][0]=9;
//        mnOuter[4][1]=7;
//        mnOuter[5][0]=13;
//        mnOuter[5][1]=7;
//        mnOuter[6][0]=15;
//        mnOuter[6][1]=10;
        
        
        //weight to the outer filter response to keep DC response = 0
//        scaleOuter[0] = 6.53594771241830e-05f;
//        scaleOuter[1] = 5.15995872033024e-05f;
//        scaleOuter[2] = 3.63108206245461e-05f;
//        scaleOuter[3] = 2.17864923747277e-05f;
//        scaleOuter[4] = 1.22549019607843e-05f;
//        scaleOuter[5] = 8.03600128576021e-06f;
//        scaleOuter[6] = 4.66853408029879e-06f;

        //weight to the inner filter response to keep DC response = 0
//        scaleInner[0] =0.000435729847494553f;
//        scaleInner[1] =0.000186741363211951f;
//        scaleInner[2] =0.000105988341282459f;
//        scaleInner[3] =5.68343279340722e-05f;
//        scaleInner[4] =4.04285425510410e-05f;
//        scaleInner[5] =3.03997568019456e-05f;
//        scaleInner[6] =2.37670825906120e-05f;
        
        //width=height of the filter size = 2*mnOuter + mnInner
//        filterSize[0] = 9;
//        filterSize[1] = 11;
//        filterSize[2] = 13;
//        filterSize[3] = 17;
//        filterSize[4] = 23;
//        filterSize[5] = 27;
//        filterSize[6] = 35;
        
        //extended filter size, for Brief detector
//        extFilterSize[0] = 17;
//        extFilterSize[1] = 19;
//        extFilterSize[2] = 25;
//        extFilterSize[3] = 29;
//        extFilterSize[4] = 41;
//        extFilterSize[5] = 49;
//        extFilterSize[6] = 65;
    }
    
    
//    public void injectBuffer()
//    {
//        bufx = bufy = 256;
//        buf = new int[bufy][bufx];
//        
//        for(int i=0;i<bufy;i++)
//        {
//            for(int j=0;j<bufx;j++)
//            {
//                buf[i][j] = 128;
//            }
//        }
//        int y=90;
//        buf[y][92]=buf[y][93]=buf[y][94]=buf[y][95]=buf[y][96]=255;
//        y=91;
//        buf[y][91]=buf[y][92]=buf[y][93]=buf[y][94]=buf[y][95]=buf[y][96]=buf[y][97]=255;
//        y=92;
//        buf[y][90]=buf[y][91]=buf[y][92]=buf[y][93]=buf[y][94]=buf[y][95]=buf[y][96]=buf[y][97]=buf[y][98]=255;
//        y=93;
//        buf[y][90]=buf[y][91]=buf[y][92]=buf[y][96]=buf[y][97]=buf[y][98]=255;
//        y=94;
//        buf[y][90]=buf[y][91]=buf[y][92]=buf[y][96]=buf[y][97]=buf[y][98]=255;
//        y=95;
//        buf[y][90]=buf[y][91]=buf[y][92]=buf[y][96]=buf[y][97]=buf[y][98]=255;
//        y=96;
//        buf[y][90]=buf[y][91]=buf[y][92]=buf[y][93]=buf[y][94]=buf[y][95]=buf[y][96]=buf[y][97]=buf[y][98]=255;
//        y=97;
//        buf[y][91]=buf[y][92]=buf[y][93]=buf[y][94]=buf[y][95]=buf[y][96]=buf[y][97]=255;
//        y=98;
//        buf[y][92]=buf[y][93]=buf[y][94]=buf[y][95]=buf[y][96]=255;
//    }
    
    public boolean SetImageBuffer(int[][] buffer)
    {
        if(buffer == null || buffer[0] == null)
            return false;
        buf = buffer.clone();
        bufx = buf[0].length;
        bufy = buf.length;
        IntegrateBoxImage();
        IntegrateSlantImage();
        return true;
    }
    
    public void IntegrateBoxImage()
    {
        integralBoxImg = new int[bufy][bufx];
        integralBoxImg[0][0]=buf[0][0];
        for(int i=1;i<bufy;i++)
        {
            integralBoxImg[i][0] = integralBoxImg[i-1][0] + buf[i][0];
        }
        for(int i=1;i<bufx;i++)
        {
            integralBoxImg[0][i] = integralBoxImg[0][i-1] + buf[0][i];
        }
        for(int i=1;i<bufy;i++)
        {
            for(int j=1;j<bufx;j++)
            {
                integralBoxImg[i][j] = buf[i][j]+integralBoxImg[i][j-1]+
                        integralBoxImg[i-1][j] - integralBoxImg[i-1][j-1];
            }
        }
    }
    
    public int[][] GetIntegralBoxImage()
    {
        return integralBoxImg;
    }
    
    public void IntegrateSlantImage()
    {
        integralSlant1Img = new int[bufy][bufx];
        integralSlant2Img = new int[bufy][bufx];
        
        integralSlant1Img[0][0] = buf[0][0];
        integralSlant2Img[0][0] = buf[0][0];
        for(int i=1;i<bufx;i++)
        {
            integralSlant1Img[0][i] = integralSlant1Img[0][i-1] + buf[0][i];
            integralSlant2Img[0][i] = integralSlant2Img[0][i-1] + buf[0][i];
        }
        int rowIntegral = 0;
        for(int i=1;i<bufy;i++)
        {
            rowIntegral = 0;
            for(int j=0;j<bufx;j++)
            {
                rowIntegral += buf[i][j];
                integralSlant1Img[i][j] = integralSlant1Img[i-1][j+1<bufx?j+1:bufx-1]+rowIntegral;
                if(j-1>=0)
                    integralSlant2Img[i][j] = integralSlant2Img[i-1][j-1]+rowIntegral;
                else
                    integralSlant2Img[i][j] = rowIntegral;
            }
        }
    }
    
    public int OctagonFilter(int x, int y, int[] mn)
    {
        if(mn==null) 
            return -1;
        
        int m = mn[0];
        int n = mn[1];
        
        int outUp = 0;
        int outBot = 0;
        if(n>0)
        {
            outUp = (integralSlant2Img[y-m/2-1][x+n+m/2-1] - 
                integralSlant2Img[y-m/2-1-n][x+n+m/2-1-n]) - 
                (integralSlant1Img[y-m/2-1][x-n-m/2] - 
                integralSlant1Img[y-m/2-1-n][x-n-m/2+n]);
            
            outBot = (integralSlant1Img[y+m/2+n][x+m/2] - 
                integralSlant1Img[y+m/2][x+n+m/2]) - 
                (integralSlant2Img[y+m/2+n][x-m/2-1] - 
                integralSlant2Img[y+m/2][x-n-m/2-1]);
        }
        int outCenter = integralBoxImg[y+m/2][x+n+m/2]
                - integralBoxImg[y-m/2-1][x+n+m/2]
                - integralBoxImg[y+m/2][x-n-m/2-1]
                + integralBoxImg[y-m/2-1][x-n-m/2-1];
        return outUp+outBot+outCenter;
    }
    
    private float CensureFilter(int x, int y, int lv)
    {
        float out = OctagonFilter(x,y,mnOuter[lv]);
        float in = OctagonFilter(x,y,mnInner[lv]);
        float v = scaleOuter[lv]*(out-in) - scaleInner[lv]*in;
        return scaleOuter[lv]*(out-in) - scaleInner[lv]*in;
    }
    
    //GetKeypoints - First apply CensureFilter, then look for local extrema 
    public Vector<Keypoint> GetKeypoints()
    {
        filterResponse = new float[7][bufy][bufx];
//        int margin = mnOuter[6][0]/2 + mnOuter[6][1]+1;
        
        for(int lv=0;lv<7;lv++)
        {
            int margin = extFilterSize[lv+1>6?6:lv+1]/2;
            for(int i=margin;i<bufy-margin;i++)
            {
                for(int j=margin;j<bufx-margin;j++)
                {
                    filterResponse[lv][i][j] = CensureFilter(j,i,lv);
                }
            }
        }
        Vector<Keypoint> v = FindKeypoints();
        
//        for(int i=0;i<v.size();i++)
//            System.out.println(v.elementAt(i));
        return v;
    }
    
    
    //look for local extrema, do harris test
    private Vector<Keypoint> FindKeypoints()
    {
        Vector<Keypoint> v = new Vector<Keypoint>();
//        int margin = mnOuter[6][0]/2 + mnOuter[6][1]+1;
        for(int lv=0;lv<7;lv++)
        {
            int margin = extFilterSize[lv+1>6?6:lv+1]/2;
            for(int i=margin;i<bufy-margin;i++)
            {
                for(int j=margin;j<bufx-margin;j++)
                {
                    Keypoint kp = IsLocalExtremum(j,i,lv,param.nonMaxSuppThreshold);
                    if(kp!=null)
                    {
                        if(HarrisTest(kp.x, kp.y, kp.lv, param.harrisThreshold))
                            v.add(kp);
                    }
                }
            }
        }
        return v;
    }
    
    //find out if filterResponse[lv][y][x] is a local max or min
    //locality is defined as the closest 3x3x3 block in the lv-x-y domain.    
    private Keypoint IsLocalExtremum(int x, int y, int lv, float threshold)
    {        
        if(Math.abs(filterResponse[lv][y][x])<threshold)
            return null;
        if(x==214 && y==143 && lv==5)
            System.out.println();
        boolean max = true;
        boolean min = true;
        
        //first ensure "array out of bounds" won't happen to the Brief descriptor
        int lvstart = lv-1<0?0:lv-1;
        while(lvstart<7 && (x<extFilterSize[lvstart]/2 || y<extFilterSize[lvstart]/2))
        {
            lvstart++;
        }
        if(lvstart>lv+1 || lvstart>=7)
            return null;
        
        int lvend = lv+1>=7?6:lv+1;
        while(lvend>=0 && (x>param.imageSize-extFilterSize[lvend] || y>param.imageSize-extFilterSize[lvend]))
        {
            lvend--;
        }
        if(lvend<lv || lvend<lvstart)
            return null;
        
        for(int l=lvstart; l<=lvend; l++)
        {
            for(int i=y-1; i<=y+1; i++)
            {
                for(int j=x-1; j<=x+1; j++)
                {
                    if(i==y && j==x &&l==lv)
                        continue;
                    if(filterResponse[lv][y][x] < filterResponse[l][i][j])
                    {
                        max = false;
                    }
                    else if(filterResponse[lv][y][x] > filterResponse[l][i][j])
                    {
                        min = false;
                    }
                    if(!max && !min)
                        return null;
                }
            }
        }
        
        if(max || min)
            return new Keypoint(x,y,lv, filterResponse[lv][y][x]);
        return null;
    }
    
    private boolean HarrisTest(int x, int y, int lv, float threshold)
    {
        return true;
//        int filterLength = filterSize[lv];
//        float[] Lx = new float[filterLength-1];
//        float[] Ly = new float[filterLength-1];
//        
//        int jj=0;
//        for(int j=x-filterLength/2+1;j<=x+filterLength/2;j++)
//        {
//            Lx[jj++] = filterResponse[lv][y][j] - filterResponse[lv][y][j-1];
//        }
//        jj=0;
//        for(int j=y-filterLength/2+1;j<=y+filterLength/2;j++)
//        {
//            Ly[jj++] = filterResponse[lv][j][x] - filterResponse[lv][j-1][x];
//        }
//        
//        float[][] h = new float[2][2];
//        for(int i=0;i<filterLength-1;i++)
//        {
//            h[0][0] += Lx[i]*Lx[i];
//            h[0][1] += Lx[i]*Ly[i];
//            h[1][1] += Ly[i]*Ly[i];
//        }
////        h[2][1] = h[1][2];
//        
//        float trace = h[0][0] + h[1][1];
//        float det = h[0][0]*h[1][1] - h[0][1]*h[0][1];
//        
//        float a = trace*trace*0.25f-det;
//        if(a<0)
//        {
//            return false;
//        }
//        
//        float b = (float) Math.sqrt(a);
//        float lambda1 = (float) (trace/2 + Math.sqrt(a));
//        float lambda2 = (float) (trace/2 - Math.sqrt(a));
//        float ratio = Math.abs(lambda1)/Math.abs(lambda2);
//        if(ratio < 10f && ratio > 0.1f)
//            return true;
//        return false;
    }
}
