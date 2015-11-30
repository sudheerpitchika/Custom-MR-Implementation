package test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;


public class App {

	public static void main(String[] args) throws Exception {

		// java -jar AB.jar
		
		String key = "";
		String val = "";
		
		String a = "java -cp AB.jar test.A "+key+" "+val;
		
/*		Process proc = Runtime.getRuntime().exec("java -cp AB.jar test.A "+ key+" ");
		proc.waitFor();
		
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();

		byte b[]=new byte[in.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));
        
        b=new byte[err.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));
        
        System.out.println("Done.!!");*/
		
		
		URL url = new URL("file:A.jar"); 
        URLClassLoader loader = new URLClassLoader (new URL[] {url});
        Class<?> cl = Class.forName ("Demo", true, loader);
        String printString = "Print this";
        Method printit = cl.getMethod("test",ArrayList.class );
        Constructor<?> ctor = cl.getConstructor(); //One has to pass arguments if constructor takes input arguments.
        Object instance = ctor.newInstance();
        ArrayList<String> list = new ArrayList<String>();
        list.add("hello");
        Object value = printit.invoke(instance,list);
        ArrayList<String>  s = (ArrayList) value;
        System.out.println("returned "+s.size());
        for(String str: s)
        {
            System.out.println(str);
            
        }
        loader.close ();
	}
}
