/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package censure;

/**
 *
 * @author cfmak
 */
public class CensureParam {
    public int imageSize;
    public float nonMaxSuppThreshold;
    public float harrisThreshold;
    
    public CensureParam(int imgSize, float nonMaxSuppressionThreshold, float harrisLineTestThreshold)
    {
        imageSize = imgSize;
        nonMaxSuppThreshold = nonMaxSuppressionThreshold;
        harrisThreshold = harrisLineTestThreshold;
    }
}
