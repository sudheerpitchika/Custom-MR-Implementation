package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


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
		
		
/*		InetAddress IP=InetAddress.getLocalHost();
		System.out.println("IP of my system is := "+IP.getHostAddress());
		System.out.println("\t"+InetAddress.getLocalHost().getHostAddress());*/
		
		
		/*
		  RandomAccessFile raf = new RandomAccessFile("inputdata.txt", "r");
	        long numSplits = 10; 
	        long sourceSize = raf.length();
	        long bytesPerSplit = sourceSize/numSplits ;
	        long remainingBytes = sourceSize % numSplits;
	        long startPosition = 0;
	        for(int i=1; i <= numSplits; i++) {
	            String s = readContent(raf,startPosition,bytesPerSplit);
	            System.out.println(s);
	            startPosition +=bytesPerSplit;
	        }
	        bytesPerSplit = 27;
	        String s = readContent(raf,0,bytesPerSplit, sourceSize);
            System.out.println(s);*/
				
		/*File file = new File("Output");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		else{
			System.out.println("Already exists");
			String[]entries = file.list();
			
			for(String s: entries){
			    File currentFile = new File(file.getPath(),s);
			    currentFile.delete();
				System.out.println(s);
			}
//			System.out.println("Cleared");
		}*/
		
		

/*		RandomAccessFile raf = new RandomAccessFile("OutputFiles/inputdata.txt", "r");
		byte[] dataBytes = new byte[30];
		raf.seek(0);
		raf.read(dataBytes);
		System.out.println(new String(dataBytes));*/
		
		

		
/*		String fileName = "output/output-"+1+".txt";
		FileOutputStream reduceOs = new FileOutputStream(fileName);
		reduceOs.write(fileName.getBytes());
		reduceOs.close();
		System.out.println("Done.!");*/
		

	}
	
    public static String readContent(RandomAccessFile raf,long startPosition,long length, long size) throws IOException{
        raf.seek(startPosition);
        byte[] buf = new byte[(int) length];
        raf.read(buf);
        
        if(startPosition+length < size){
        	
        	byte[] buf2 = new byte[1];
        	raf.read(buf2);
        	String next = new String(buf2);
        	
        	if(next.equals(" ")){
        		System.out.println("next char: "+next);
        		System.out.println(new String(buf));
        	}
        	else{
        		int i=0;
        		while(true){
        			raf.seek(startPosition+length+i--);
        			raf.read(buf2);
                	String prev = new String(buf2);
                	if(prev.equals(" ")){
                		break;
                	}
                	System.out.println("prev: "+prev);
                		
        		}
        		
        	}
        }
        return new String(buf);
    }
}
