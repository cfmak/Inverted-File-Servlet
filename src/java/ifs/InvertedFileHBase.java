/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ifs;

import fs.FileSystemConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import sift.SiftDescriptorWritable;

/**
 *
 * @author cfmak
 */
public class InvertedFileHBase extends InvertedFile{
    public InvertedFileHBase(FileSystemConfig conf)
    {
        super(conf);
    }
    
    //read inverted file from HBase
    public void ReadInvertedFile() throws IOException
    {
        System.out.println("InvertedFileHBase.ReadInvertedFile()");
        String clusterTableName = fsconf.hbaseconf.get("cluster_table_name");
        //check table exists
        if(clusterTableName == null)
        {
            throw new IOException("InvertedFileHBase:ReadInvertedFile() - "
                    + "property \"cluster_table_name\" is not set in FileSystemConfig.HBaseConfiguration");
        }
        HBaseAdmin hba = new HBaseAdmin( fsconf.hbaseconf );
        if(!hba.tableExists(clusterTableName))
        {
            throw new IOException("InvertedFileHBase:ReadInvertedFile() - Table "
                    +fsconf.hbaseconf.get("cluster_table_name")+" does not exist");
        }
        
        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false);
        scan.setBatch(500);
        HTablePool pool = new HTablePool(fsconf.hbaseconf, 500);
        HTableInterface table = pool.getTable(clusterTableName);

        //scan all rows, each row ID is a cluster center descriptor
        //columns are unused.
        ResultScanner scanner = table.getScanner(scan);
        for(Result result : scanner)
        {
            byte[] descbyte = result.getRow();
            SiftDescriptorWritable center = new SiftDescriptorWritable(descbyte);
            System.out.println(center);
            int i=0;
            ArrayList<ImageEntryWritable> v = null;
            while(result.containsColumn(Bytes.toBytes("imageEntry"), Bytes.toBytes("id_"+i)) &&
                    result.containsColumn(Bytes.toBytes("imageEntry"), Bytes.toBytes("he_"+i)))
            {
                byte[] imgidbyte = result.getValue(Bytes.toBytes("imageEntry"), Bytes.toBytes("id_"+i));
                byte[] hebyte = result.getValue(Bytes.toBytes("imageEntry"), Bytes.toBytes("he_"+i));
                ImageEntryWritable imgEntry = new ImageEntryWritable(imgidbyte, hebyte);
                
                System.out.println(imgEntry);
                v = put(center, imgEntry, false);
                i++;
            }
            if(v != null)
                Collections.sort(v);
        }
        scanner.close();
        pool.putTable(table);
        pool.close();
    }
}
