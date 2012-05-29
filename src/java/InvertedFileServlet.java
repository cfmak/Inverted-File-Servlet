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
import org.apache.commons.lang3.StringUtils;
import com.google.gson.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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
    
    private byte[] testcode;
    private byte[] testcodebase64;
    private String testcodebase64str;
    
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
        
        //ReadTestImage();        
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
        
        int k=0;
        
        byte[] bytes = new byte[Float.SIZE/8*128*9];
        for(int i=0;i<9;i++)
        {
            byte[] tmp = testdesc[i].toBytes();
            for(int j=0;j<tmp.length;j++)
                bytes[k++] = tmp[j];
                    
        }
        
//        String FILENAME = "/Users/cfmak/truth_desc.dat";
//        DataOutputStream os;
//        try {
//            os = new DataOutputStream(new FileOutputStream(FILENAME));
//            os.write(bytes);
//            os.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(InvertedFileServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }catch (IOException ex) {
//            Logger.getLogger(InvertedFileServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
        testcode = bytes;
        testcodebase64 = Base64.encodeBase64(bytes);
        testcodebase64str = new String(testcodebase64);
        testcodebase64str = StringUtils.replaceChars(testcodebase64str, "+/", "-_");

        String FILENAME = "/Users/cfmak/workspace/InvertedFile/truth_base64.txt";
        DataOutputStream os;
        try {
            os = new DataOutputStream(new FileOutputStream(FILENAME));
            os.writeBytes(testcodebase64str);
            os.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvertedFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(InvertedFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        response.setHeader("Content-Type", "text/javascript; charset=utf8");
        PrintWriter out = response.getWriter();
        
//        String callback = request.getParameter("callback");
        
//        int id = ifs.Query(testdesc);
        
        
	String queryBase64Str = request.getParameter("query");
        System.out.println("queryBase64Str="+queryBase64Str);
        if(queryBase64Str == null)
        {
            out.print("{}");
            return;
        }
        
//        if(queryBase64Str.compareTo(testcodebase64str)!=0)
//        {
//            System.out.println(queryBase64Str.length());
//            System.out.println(testcodebase64str.length());
//            byte[] queryStrBytes = queryBase64Str.getBytes();
//            byte[] testcodebase64strBytes = testcodebase64str.getBytes();
//            
//            if(!queryStrBytes.equals(testcodebase64strBytes))
//            {
//                for(int i=0;i<testcodebase64strBytes.length;i++)
//                {
//                    if(queryStrBytes[i] != testcodebase64strBytes[i])
//                    {
//                        System.out.println(i+" "+testcodebase64strBytes[i]+" "+queryStrBytes[i]);
//                    }
//                }
//            }
//            int i=0;
//        }
        
        //convert modified URL-safe base64 to original base64
        queryBase64Str = StringUtils.replaceChars(queryBase64Str, "-_", "+/");
        byte[] query = Base64.decodeBase64(queryBase64Str);
        
        //check query length
        if(query.length != SiftDescriptor.LengthInBytes() * 9)
        {
            out.print("{}");
            return;
        }
//        byte[] query = Base64.decodeBase64(testcodebase64str);
        
        //Make query => 9 Sift Descriptors
        ByteBuffer bb = ByteBuffer.wrap(query);
        FloatBuffer fb = ((ByteBuffer) bb.rewind()).asFloatBuffer();
        
        float[] arr = new float[128];
        SiftDescriptor[] desc = new SiftDescriptor[9];
        for(int j=0;j<9;j++)
        {
            fb.get(arr, 0, 128);
            desc[j] = new SiftDescriptor(arr);
            
            System.out.println(desc[j].toString());
        }

        int id = ifs.Query(desc);
        
        //output jsonp
//        if(callback != null)
//            out.println(callback+"(");
        //output json (very strict, no newline, no extra comma)
        out.print("[{ \"dataset\":[");
        Object[] keys = fs.map.keySet().toArray();
        for(int i=0;i<keys.length;i++)
        {
            out.print("{\""+keys[i]+"\":\""+fs.map.get(keys[i])+"\"}");
            if(i<keys.length-1)
                out.print(",");
//            out.println("<br>");
        }
        out.print("]},");
        out.print("{\"result\":"+id+"}");
        out.print("]");
        
//        if(callback != null)
//            out.println(");");
        
//        Object[] desc = ifs.map.keySet().toArray();
//        for(int i=0;i<desc.length;i++)
//        {
//            out.println(((SiftDescriptor)desc[i]).toString());
//            out.println("<br>");
//        }
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
        processRequest(request, response);
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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    

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
