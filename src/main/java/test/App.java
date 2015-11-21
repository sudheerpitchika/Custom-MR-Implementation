package test;

import java.io.InputStream;

public class App {

	public static void main(String[] args) throws Exception {

		// java -jar AB.jar
		
		Process proc = Runtime.getRuntime().exec("java -cp AB.jar test.A");
		proc.waitFor();
		
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();

		byte b[]=new byte[in.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));
        
        b=new byte[err.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));
        
        System.out.println("Done.!!");
	}
}
