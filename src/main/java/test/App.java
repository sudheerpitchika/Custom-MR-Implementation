package test;

import java.net.InetAddress;


public class App {

	public static void main(String[] args) throws Exception {

		// java -jar AB.jar
		
		/*String key = "";
		String val = "";
		
		String a = "java -cp AB.jar test.A "+key+" "+val;*/
		
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
		
		
/*		URL url = new URL("file:A.jar"); 
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
        loader.close ();*/
		
//		BufferedInputStream bis = null;
/*		FileInputStream bis = null;
		
		int chunkId=0;
		int offset=0;
		int length=40;
		
		String fileName = "inputdata.txt";
		File jInFile = new File(fileName);
		try {
			bis = new FileInputStream(jInFile);
			//bis = new BufferedInputStream(fis);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		byte[] byteData = new byte[length];
		
//		System.out.println("bis.read: "+bis.read());		
		for(int i=0; i<4; i++){
			
			try {
				bis = new FileInputStream(jInFile);
				System.out.println("Trying to read from "+offset+" to "+(offset+length-1));
				bis.read(byteData, offset, length-1);
				
				System.out.println(new String(byteData));
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Length: "+byteData.length);
		offset += length;
		}*/
		
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*		File transferFile = new File("inputdata.txt");
        //byte[] bytearray = new byte[(int) transferFile.length()];
		byte[] bytearray = new byte[10000];
        System.out.println("Size: "+bytearray.length);

        FileInputStream fin = new FileInputStream(transferFile);
        BufferedInputStream bin = new BufferedInputStream(fin);

        int length = (int) transferFile.length();
        int start=0, splitSize = 6291456;
        splitSize=4096;
        int curLength = splitSize;
        int count = 1;
        int i=0;

        for(int end = 0; end < length; end++) {
            end = start+splitSize;

            if(end > length)
                curLength = length - start;

            System.out.println("Sending Files "+count++);
            bin.read(bytearray, start, curLength);
            
            System.out.println(new String(bytearray));
            start = end;
        }
        System.out.println("File transfer complete");*/
		
/*		String remoteAddr = "/127.0.0.1:57050";
		String[] parts = remoteAddr.split(":");
		
		String port = parts[1];
		String ip = remoteAddr.split(":")[0].substring(1);
		System.out.println(port+"\t"+ip);*/
		
		
		InetAddress IP=InetAddress.getLocalHost();
		System.out.println("IP of my system is := "+IP.getHostAddress());
		System.out.println("\t"+InetAddress.getLocalHost().getHostAddress());
		
	}
}
