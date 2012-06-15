/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fs;
import javax.servlet.ServletContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
/**
 *
 * @author cfmak
 */
public class FileSystemConfig {
    public Configuration hbaseconf;
    public boolean isLocal;
    public ServletContext sc;
    
    public FileSystemConfig(Configuration hbaseConfig, ServletContext context, boolean islocal)
    {
        isLocal = islocal;
        sc = context;
        hbaseconf = hbaseConfig;
    }
}
