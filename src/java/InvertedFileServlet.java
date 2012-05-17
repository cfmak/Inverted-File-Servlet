/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import imageCommon.Keypoint;
import sift.*;
import fs.*;
import ifs.*;

import imageCommon.ImageCommon;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.*;
import javax.servlet.*;

/**
 *
 * @author cfmak
 */
public class InvertedFileServlet extends HttpServlet {

    private InvertedFile ifs;
    private FileSystem fs;
    private ServletContext sc;
    
    private SiftDescriptor[] testdesc;
    
    // This Happens Once and is Reused
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        sc = config.getServletContext();
        fs = new FileSystem(sc);
        ifs = new InvertedFile();
//        BufferedImage img = fs.ReadLocalImageFile("/images/IMG_0183.JPG");
//        if(img!=null)
//            ifs.putBufferedImage(img, 256);
        fs.PutLocalDirectory(ifs, 256, "/images", true);
        
        ReadTestImage();        
    }
    
    public void ReadTestImage()
    {
        int imageSize = 256;
        BufferedImage img = fs.ReadLocalImageFile("/images/IMG_0183.JPG");
        img = ImageCommon.Resize(img, imageSize,imageSize);
        int[] rgbs = new int[imageSize*imageSize]; //argb format
        img.getRGB(0, 0, imageSize, imageSize, rgbs, 0, imageSize);
        int[][] gray = ImageCommon.GrayScale(rgbs, imageSize, imageSize);
        int[][] integral = ImageCommon.IntegrateBoxImage(gray);
        
        testdesc = new SiftDescriptor[9];
        for(int i=0; i<3; i++)
        {
            for(int j=0; j<3; j++)
            {
                Keypoint kp = new Keypoint(j*85+42, i*85+42, 7, 1);
                Sift sift = new Sift();
                
                testdesc[i*3+j] = sift.GetDescriptor(kp, integral);
            }
        }
    }
           
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        PrintWriter out = response.getWriter();
        
//        int id = ifs.Query(testdesc);
        
        SiftDescriptor[] desc = new SiftDescriptor[9];
	String queryStr = request.getParameter("query");
        byte[] query = Base64.decodeBase64(queryStr);
        float[] arr = new float[128];
        
        for(int j=0;j<9;j++)
        {
            for(int i=0;i<128*4;i+=4)
            {
                int bit32 =   (query[i] << 24)
                            | (query[i+1] << 16)
                            | (query[i+2] << 8)
                            | (query[i+3]);
                arr[i/4] = Float.intBitsToFloat(bit32);
            }
            desc[j] = new SiftDescriptor(arr);
        }
        
        int id = ifs.Query(desc);
//        out.println(id);

//	out.println("<html>");
//	out.println("<body>");
        Object[] keys = fs.map.keySet().toArray();
        for(int i=0;i<keys.length;i++)
        {
            out.println(keys[i]+" "+fs.map.get(keys[i]));
//            out.println("<br>");
        }
        out.println(id);
        
//        Object[] desc = ifs.map.keySet().toArray();
//        for(int i=0;i<desc.length;i++)
//        {
//            out.println(((SiftDescriptor)desc[i]).toString());
//            out.println("<br>");
//        }
        
        
//	out.println("</body>");
//	out.println("</html>");
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
