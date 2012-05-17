/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imageCommon;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author cfmak
 */
public class ImageCommon {
    public static BufferedImage Resize(BufferedImage img, int w, int h)
    {
        BufferedImage tmpImage = new BufferedImage(w,h,
                    BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = tmpImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(img, 0, 0, w,h, null);
        
        return tmpImage;
    }
    
    public static int[][] GrayScale(int[] rgbs, int w, int h)
    {
        if(rgbs == null)
            return null;
        if(w*h!=rgbs.length)
            return null;
        
        int[][] gray = new int[h][w];
        
        for(int i=0;i<h;i++)
        {
            for(int j=0;j<w;j++)
            {
                int r = ((rgbs[i*w+j]&0x00ff0000)>>16);
                int g = ((rgbs[i*w+j]&0x0000ff00)>>8);
                int b = (rgbs[i*w+j]&0x000000ff);
                gray[i][j] = (r+g+b)/3;
            }
        }
        
        return gray;
    }
    
    public static int[][] IntegrateBoxImage(int[][] buf)
    {
        int bufy = buf.length;
        int bufx = buf[0].length;
        
        int[][] integralBoxImg = new int[bufy][bufx];
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
        return integralBoxImg;
    }
}
