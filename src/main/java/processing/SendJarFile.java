package processing;

/*public class SendJarFile {
	
}*/

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by sponugot on 11/28/15.
 */
public class SendJarFile {

    public static void main(String[] args) throws IOException {
        File jInFile = new File("send.jar");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(jInFile));
        
        byte[] buffer = new byte[(int)jInFile.length()];
        int len;
        bis.read(buffer);
        
//        bos.write(buffer, 0, buffer.length);
        
/*        File jOutFile = new File("received.jar");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jOutFile));
        copyFiles(bis, bos);
*/ 
        
     }

}