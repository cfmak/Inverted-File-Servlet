package fs;

import ifs.InvertedFile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cfmak
 */
public class FileSystem {
    private FileSystemConfig config;
    private ServletContext sc;
    public HashMap<Integer, String> map;
    
    public FileSystem(ServletContext context)
    {
        sc = context;
        map = new HashMap<Integer, String>();
    }
    
    public BufferedImage ReadLocalImageFile(String relPath)
    {
        File file = new File(sc.getRealPath(relPath));
        try {
            BufferedImage img = ImageIO.read(file);
            return img;
        } catch (IOException ex) {
            
        }
        return null;
    }
    
//    public BufferedImage ReadLocalImageFile(File absPath)
//    {
//        try {
//            BufferedImage img = ImageIO.read(absPath);
//            return img;
//        } catch (IOException ex) {
//            
//        }
//        return null;
//    }
    
    public void PutLocalImageFile(InvertedFile ifs, int imgSize, String relPath)
    {
        File file = new File(sc.getRealPath(relPath));
        PutLocalImageFile(ifs, imgSize, file);
    }
    
    public void PutLocalImageFile(InvertedFile ifs, int imgSize, File file)
    {
        BufferedImage img;
        try {
            img = ImageIO.read(file);
            if(img!=null)
            {
                int id = map.size()+1;
                map.put(new Integer(id), file.getAbsolutePath());
                ifs.putBufferedImage(img, imgSize, id);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void PutLocalDirectory(InvertedFile ifs, int imgSize, 
            String relDirectoryPath, boolean recursive)
    {
        File dir = new File(sc.getRealPath(relDirectoryPath));
        PutLocalDirectoryHelper(ifs, imgSize, dir, recursive);
    }
    
    private void PutLocalDirectoryHelper(InvertedFile ifs, int imgSize, 
            File dir, boolean recursive)
    {
        for (File child : dir.listFiles()) {
            if (".".equals(child.getName()) || "..".equals(child.getName())) {
                continue;  // Ignore the self and parent aliases.
            }
            // Do something with child
            if (child.isDirectory() && recursive)
            {
                PutLocalDirectoryHelper(ifs, imgSize, child, recursive);
            }
            else
            {
                PutLocalImageFile(ifs, imgSize, child);
            }
        }
    }
    
    public String get(int id)
    {
        return map.get(id);
    }
}
