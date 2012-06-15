package fs;

import ifs.ImageEntryWritable;
import ifs.InvertedFile;
import imageCommon.ImageCommon;
import imageCommon.Keypoint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import sift.Sift;
import sift.SiftDescriptor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cfmak
 */
public class FileSystem {
    //if FS is local, use ServletContext
    //if FS is a DB, use conf
    private FileSystemConfig conf;
    public HashMap<Integer, String> map;
    
//    public FileSystem(ServletContext context)
//    {
//        sc = context;
//        map = new HashMap<Integer, String>();
//    }
//    
    public FileSystem(FileSystemConfig config)
    {
        map = new HashMap<Integer, String>();
        conf = config;
    }
    
    public BufferedImage ReadLocalImageFile(String relPath) throws IOException
    {
        if(!conf.isLocal)
            throw new IOException("FileSystem is not local");
        
        File file = new File(conf.sc.getRealPath(relPath));
        BufferedImage img = ImageIO.read(file);
        return img;
    }
    
    public byte[] GetDBImageFile(String pictogramKey) throws IOException
    {
        if(conf.isLocal)
            throw new IOException("FileSystem is local");
        HTable table = new HTable(conf.hbaseconf, "pictogram_table");
        Get get = new Get(Bytes.toBytes(pictogramKey));
        
        get.addColumn(Bytes.toBytes("pic"), Bytes.toBytes("ori_mime_type"));
        Result result = table.get(get);
        byte[] imageInByte = result.getValue(Bytes.toBytes("pic"), Bytes.toBytes("ori_mime_type"));
        for(int i=0;i<imageInByte.length;i++)
            System.out.print((char)imageInByte[i]);
        return imageInByte;
        
        //read to BufferedImage
//        InputStream in = new ByteArrayInputStream(imageInByte);
//        BufferedImage bImageFromConvert = ImageIO.read(in);
//
//        return null;
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
    
    //mat - grayscale image
    public void PutImageMatrix(InvertedFile ifs, int[][] mat, int id)
    {
        int[][] integral = ImageCommon.IntegrateBoxImage(mat);
        
        ArrayList<ImageEntryWritable> v = null;
        for(int i=0; i<3; i++)
        {
            for(int j=0; j<3; j++)
            {
                Keypoint kp = new Keypoint(j*85+42, i*85+42, 7, 1);
                
                SiftDescriptor desc = Sift.GetDescriptor(kp, integral);
                
                ImageEntryWritable entry = new ImageEntryWritable(id, desc.GetHammingEmbedding());
                v = ifs.put(desc, entry, false);
            }
        }
        if(v != null)
            Collections.sort(v);
    }
    
    public void PutBufferedImage(InvertedFile ifs, BufferedImage img, int imageSize, int id)
    {
        img = ImageCommon.Resize(img, imageSize, imageSize);
        
        int[] rgbs = new int[imageSize*imageSize]; //argb format
        img.getRGB(0, 0, imageSize, imageSize, rgbs, 0, imageSize);
        
        int[][] gray = ImageCommon.GrayScale(rgbs, imageSize, imageSize);
        
        PutImageMatrix(ifs, gray, id);
    }
    
    public void PutLocalImageFile(InvertedFile ifs, int imgSize, String relPath) throws IOException
    {
        if(!conf.isLocal)
            throw new IOException("FileSystem is not local");
        File file = new File(conf.sc.getRealPath(relPath));
        PutLocalImageFile(ifs, imgSize, file);
    }
    
    public void PutLocalImageFile(InvertedFile ifs, int imgSize, File file) throws IOException
    {
        if(!conf.isLocal)
            throw new IOException("FileSystem is not local");
        BufferedImage img;
        try {
            img = ImageIO.read(file);
            if(img!=null)
            {
                //int id = map.size()+1;
                int id = Integer.parseInt(file.getName().split("\\.")[0]);
                map.put(new Integer(id), file.getAbsolutePath());
                PutBufferedImage(ifs, img, imgSize, id);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void PutLocalDirectory(InvertedFile ifs, int imgSize, 
            String relDirectoryPath, boolean recursive) throws IOException
    {
        if(!conf.isLocal)
            throw new IOException("FileSystem is not local");
        File dir = new File(conf.sc.getRealPath(relDirectoryPath));
        PutLocalDirectoryHelper(ifs, imgSize, dir, recursive);
    }
    
    private void PutLocalDirectoryHelper(InvertedFile ifs, int imgSize, 
            File dir, boolean recursive) throws IOException
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
