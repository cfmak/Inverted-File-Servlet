/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imageCommon;

/**
 *
 * @author cfmak
 */
public class Keypoint {
    public int x;
    public int y;
    public int lv;
    public float v;
    
    public Keypoint(int posx, int posy)
    {
        x=posx;
        y=posy;
        lv = -1;
    }
    
    public Keypoint(int posx, int posy, int level, float value)
    {
        x=posx;
        y=posy;
        lv=level;
        v = value;
    }
    
    public String toString()
    {
        return x+" "+y+" "+lv+" "+v+"; ";
    }
    
}
