/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brief;

/**
 *
 * @author cfmak
 */
public class BriefParam {
    public int nPairs;
    public int[] kernelSize;
    public int[] kpSize;
    
    public BriefParam(int npairs)
    {
        nPairs = npairs;
        
        kpSize = new int[7];
        kpSize[0] = 9;
        kpSize[1] = 11;
        kpSize[2] = 13;
        kpSize[3] = 17;
        kpSize[4] = 23;
        kpSize[5] = 27;
        kpSize[6] = 35;
        
        kernelSize = new int[7];
        kernelSize[0] = 3;
        kernelSize[1] = 3;
        kernelSize[2] = 5;
        kernelSize[3] = 5;
        kernelSize[4] = 7;
        kernelSize[5] = 9;
        kernelSize[6] = 12;
    }
}
