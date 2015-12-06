/*
Conversation opened. 6 messages. All messages read.

Skip to content
Using UNC Charlotte Mail with screen readers
Sudheer
Search



Mail
COMPOSE
Labels
Inbox
Starred
Sent Mail
Drafts (2)
Trash
Not imp
Univ Imp
More 
Hangouts

 
 
 
  More 
3 of 442  
 
Expand all Print all In new window
Thread pool for requests
Inbox
x 

Pitchika, Sudheer		Dec 3 (3 days ago)
else if(cmdString.equals("RETURN_VALUES_FOR_KEY")){ final ReturnValueForKey r...
3 older messages

Pitchika, Sudheer		3:46 PM (21 hours ago)
public ArrayList<String> valueForKeyLocationSet(String key, ArrayList<Locatio...

Pitchika, Sudheer <psudheer@uncc.edu>
Attachments6:24 PM (18 hours ago)

to me 
Worker Program updated with buffered writer

Attachments area
Preview attachment WorkerProgram.java

Text
WorkerProgram.java
	
Click here to Reply or Forward
Using 0.57 GB
Program Policies
Powered by Google
Last account activity: 2 minutes ago
Open in 1 other location  Details
*/

package endmodules;

import io.netty.commands.CommandsClient;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyLocation;
import io.netty.commands.CommandsProtocol.KeyLocationsSet;
import io.netty.commands.CommandsProtocol.KeyValuesSet;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Slave;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.protobuf.ByteString;

import config.RunConfig;

public class WorkerProgram {
	TaskTracker tt;
	DataNode dn;
	
	static String complexDelimiter = "#%@@%#";
	
	//used to send values to reducers
	Map<String, LocationMeta> keyAndFileLocationMap; //stores keys and location(offset) in file, and let of the value for key
	Map<String, ArrayList<LocationMeta>> keysAndMapperLocations;
	Map<String, ArrayList<String>> keyValuesInMap;
	Map<String, ArrayList<String>> keyValuesInReducer;
	ArrayList<String> keyList;
	ArrayList<Integer> fileChunkIdsList;
	HashMap<Integer,RandomAccessFile> fileStreams;
	java.util.concurrent.ConcurrentHashMap<String, ArrayList<String>> keyValuesInConcurrentMap;
	
	ExecutorService executorService = Executors.newFixedThreadPool(20);
//	HashMap<Integer, Long> fileSizes;
	String inputData;
	FileOutputStream reduceOs;
	static int reducersProcessed = 0;
	BlockingQueue<String> completedReducerKeys = new LinkedBlockingQueue<String>();
	int inputDataChunkId;

	public WorkerProgram(){

		complexDelimiter = "#";
		fileStreams = new HashMap<Integer,RandomAccessFile>();
//		fileSizes = new HashMap<Integer,Long>();
		fileChunkIdsList = new ArrayList<Integer>();
		inputData = null;
	}
	
	public void createJar(ByteString bs) throws IOException{
		
		createDirectoryIfNotExists(RunConfig.jarFileDirectory);
		String fileName = RunConfig.jarFileDirectory + "/" + "MFReceived.jar"; 
		
		byte[] buffer = bs.toByteArray();
		File jarOutFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jarOutFile));
        bos.write(buffer, 0, buffer.length);
        bos.flush();
        bos.close();
        System.out.println("Created Jar.!");        
	} 
	
	public void acceptData(int inputChunkId, String inputDataString){
		
		keyValuesInMap = new HashMap<String, ArrayList<String>>();
		keyAndFileLocationMap = new HashMap<String, LocationMeta>();
		
		inputData = inputDataString;
		this.inputDataChunkId = inputChunkId;
		byte[] buffer = inputDataString.getBytes();
		
		//fileChunkIdsList.add(inputChunkId);
		
		createDirectoryIfNotExists(RunConfig.inputFilesDirectory);
		String fileName = RunConfig.inputFilesDirectory + "/" + "receivedData-" + inputChunkId + ".txt";
		
		//File jOutFile = new File(directory, fileName);
		File jOutFile = new File(fileName);
        BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(jOutFile));
	        bos.write(buffer, 0, buffer.length);
	        bos.flush();
	        bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void openAllFiles() throws Exception{
		System.out.println("******* OPENING FILES ******* "+fileChunkIdsList.size());
		for(int chunkId : fileChunkIdsList){
			String fileName = RunConfig.tempFilesDirectory + "/" + "tempFile-" + chunkId + ".txt";
			RandomAccessFile raf = new RandomAccessFile(fileName, "r");
			fileStreams.put(chunkId,raf);
//			fileSizes.put(chunkId, file.length());
		}
		System.out.println("******* OPENING FILES ******* "+fileChunkIdsList.size());
	}
	
	
	//start a thread and listen for master program

	public void startProgram(String type){ //run map or reducer task
		//
		
	}
	
	public void startMapFunction() throws Exception{
		// run map class in jar
		
		String urlString = "file:"+RunConfig.jarFileDirectory+"/MFReceived.jar";
		URL url = new URL(urlString);
		
        URLClassLoader loader = new URLClassLoader (new URL[] {url});
        Class<?> cl = Class.forName ("userprogram.MapFunction", true, loader);
        Method printit = cl.getMethod("map",String.class, String.class, WorkerProgram.class );
        Constructor<?> ctor = cl.getConstructor(); //One has to pass arguments if constructor takes input arguments.
        Object instance = ctor.newInstance();
        Object value = printit.invoke(instance,"chunk-1",inputData, Slave.worker);
        loader.close ();
        System.out.println("Map completed, ele count "+keyValuesInMap.size());
	}

	public void createDirectoryIfNotExists(String directory){
		File dir = new File(directory);
		if (!dir.exists()) {
			if (dir.mkdir()) {

			} else {
				System.out.println("Failed to create directory! "+directory);
			}
		}
	}
	
	public void startReduceFunction() throws Exception{
		
		// open output file to write data into, write(key, value) function
		
		createDirectoryIfNotExists(RunConfig.outputFilesDirectory);
		
		String fileName = RunConfig.outputFilesDirectory+"/"+"output-"+reducersProcessed+".txt";
		reduceOs = new FileOutputStream(fileName);

		// run reduce class in jar
		String urlString = "file:"+RunConfig.jarFileDirectory+"/MFReceived.jar";
		// URL url = new URL("file:jar/MFReceived.jar");
		URL url = new URL(urlString);
		
        URLClassLoader loader = new URLClassLoader (new URL[] {url});
        Class<?> cl = Class.forName ("userprogram.ReduceFunction", true, loader);
        Method printit = cl.getMethod("reduce",String.class, ArrayList.class, WorkerProgram.class );
        Constructor<?> ctor = cl.getConstructor(); //One has to pass arguments if constructor takes input arguments.
        Object instance = ctor.newInstance();
        
        System.out.println("Accessing key values in reducer");        
        Set<String> keySet = keysAndMapperLocations.keySet();
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        
        for(String key : keySet){
        	// Object value = printit.invoke(instance,key,keyValuesInReducer.get(key), Slave.worker);
        	RunReducerClass runReducer = new RunReducerClass(executor,instance, printit, key);
        	/*Thread reducerTrhead = new Thread(runReducer);
        	reducerTrhead.start();*/
        	runReducer.run();
   //     	Thread.sleep(100);
System.out.println("*** "+key);
        }

        for(int i=0; i < keySet.size(); i++){
        	completedReducerKeys.take();
        }

        executor.shutdown();
        reduceOs.close();
        loader.close ();
        System.out.println("Reduce: completed-"+reducersProcessed);
		
		reducersProcessed++;

	}

	public void receiveData(String inputData){
		this.inputData = inputData;
	}
	
	public void startCombiner() throws Exception{
		//sort values in the map
		Set<String> keySet = keyValuesInMap.keySet();
		keyList = new ArrayList<String>();
		keyList.addAll(keySet);
		//Collections.sort(keyList);
	}

	
	public void sendKeyAndLocationsToShuffler() throws Exception{
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("ACCEPT_DATA_SHUFFLER");
				
		Set<String> keysSet = keyAndFileLocationMap.keySet();
		for(String key : keysSet){
			//keyLocationsMap
			KeyLocation.Builder keyLocation = KeyLocation.newBuilder();
			
			LocationMeta locationMeta = keyAndFileLocationMap.get(key);
			Location.Builder location = Location.newBuilder();
			location.setChunk(locationMeta.getChunkId());
			location.setIp(locationMeta.getIp());
			location.setStart(locationMeta.getStart());
			location.setLength(locationMeta.getLength());
			
			keyLocation.setKey(key);
			keyLocation.setLocation(location);
			
			command.addKeyLocationsMap(keyLocation);
	//		System.out.println(key+"\t"+location.toString());
		}
		
		//CommandsClient shuffleClient = new CommandsClient("127.0.0.1", "8477");
		CommandsClient shuffleClient = new CommandsClient(RunConfig.shuffleServerIp, RunConfig.shuffleServerPort);
		shuffleClient.startConnection();
		shuffleClient.sendCommand(command.build());
		// *************** closing connection
		shuffleClient.closeConnection();
	}
	
	public Map<String, LocationMeta> getKeyAndLocations(){
		// sends the key set available at the worker when the shuffeler requests
		// send keys and their locations too (offset, length, chunk id)
				
		// RETURN_KEYS_AND_LOCATIONS
		
		return keyAndFileLocationMap;
	}
	
	public ArrayList<String> valueForKey(String key, LocationMeta location) throws Exception{
		//get the value from the file(for corresponding chunk) //requested by reducer
		
		RandomAccessFile raf = fileStreams.get(location.getChunkId());

		System.out.println(key+"\t"+location.getChunkId()+"\t"+location.getStart()+"\t"+location.getLength());
		
		byte[] dataBytes = new byte[location.getLength()];
//		System.out.println("***"+location.getStart());
		raf.seek(location.getStart());
		raf.read(dataBytes);
		
		String dataString = new String(dataBytes);
		dataString = dataString.trim();
		System.out.println("Data String: "+dataString);
		String[] splits = dataString.split(complexDelimiter);
		
		ArrayList<String> values = new ArrayList<String> ( Arrays.asList(splits));
		
		values.remove(0);
		
		System.out.println("*** Returning values for key: "+key+"\t size: "+values.size());
		return values;
	}

	
	
	public ArrayList<String> valueForKeyLocationSet(String key, ArrayList<LocationMeta> locations) throws Exception{
		//get the value from the file(for corresponding chunk) //requested by reducer
		
		ArrayList<String> valuesList = new ArrayList<String>();
		
		for(LocationMeta location : locations)
		{
			RandomAccessFile raf = fileStreams.get(location.getChunkId());
	
			//System.out.println(key+"\t"+location.getChunkId()+"\t"+location.getStart()+"\t"+location.getLength());
			
			byte[] dataBytes = new byte[location.getLength()];

			raf.seek(location.getStart());
			raf.read(dataBytes);
			
			String dataString = new String(dataBytes);
			dataString = dataString.trim();
			//System.out.println("Data String: "+dataString);
			
			int keyIndex = dataString.indexOf(key);
			if(keyIndex < 0){
				
				/*int correction = 30;
				long offset = Long.max(location.getStart()-correction, 0);
				dataBytes = new byte[(int)offset+location.getLength()+correction];
				raf.seek(location.getStart());
				raf.read(dataBytes);

				dataString = new String(dataBytes);
				dataString = dataString.trim();
				keyIndex = dataString.indexOf(key);
				
				if(keyIndex >= 0){
					System.out.println(" ***** CATCH INDEX ****** ");
					dataString = dataString.substring(keyIndex, keyIndex+location.getLength()-1);
				}
				else{
					System.out.println(" ***** ERROR INDEX ****** ");
					continue;
				}*/
				System.out.println(" ***** ERROR INDEX ****** ");
				continue;
			}
			if(keyIndex > 0){
				dataString = dataString.substring(keyIndex);
				dataBytes = new byte[keyIndex];
				raf.seek(location.getStart() + location.getLength());
				raf.read(dataBytes);
				dataString = dataString + new String(dataBytes);
			}
			
			String[] splits = dataString.split(complexDelimiter);
			ArrayList<String> values = new ArrayList<String> ( Arrays.asList(splits));
			values.remove(0);
			valuesList.addAll(values);
		}
// uncomment this
//		System.out.println("*** Returning values for key: "+key+"\t size: "+valuesList.size());
		return valuesList;
	}
	
	public void acceptKeyAndMapperLocationSetFromShuffler( Map<String, ArrayList<LocationMeta>> map){
		//reducer server
		//read from buffer and add to keysAndMapperLocations
		
		System.out.println("initializing key values in reducer");
		
		keyValuesInReducer = new HashMap<String, ArrayList<String>>();
		keysAndMapperLocations = new HashMap<String, ArrayList<LocationMeta>>();

		
		this.keysAndMapperLocations = map;
	}
	

	
	public void emit(String key, String value){
		//keyValuesMap
		
//		System.out.println("In Emit Function "+key+"\t"+value);
		key = key.trim();
		key = key.replaceAll("\\s", "");
		key = key.replaceAll("\\n ", "");
		key = key.replaceAll("#", "");
		
		if(value != null && value.length() > 0){
				if(keyValuesInMap.containsKey(key)){
					ArrayList<String> values = keyValuesInMap.get(key);
					values.add(value);
					keyValuesInMap.put(key, values);
				}else{
					ArrayList<String> values = new ArrayList<String>();
					values.add(value);
					keyValuesInMap.put(key, values);
				}
		}
	}
	

	public void write(String key, String value) throws IOException{
		String outVal = key+"\t"+value;
		reduceOs.write(outVal.getBytes());
		reduceOs.write("\n".getBytes());
	}
	
	//call this once map() is completed
	// writeKeyValuesToMultipleFileAndCreateTable
	public void writeKeyValuesToFileAndCreateTable() throws Exception{
		
		createDirectoryIfNotExists(RunConfig.tempFilesDirectory);
		String fileName = RunConfig.tempFilesDirectory+"/"+"tempFile-"+inputDataChunkId+".txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		
		int start = 0;
		
		for(String key : keyList){
			
/*			key = key.trim();
			key = key.replaceAll("\n ", "");
*/			
			if(key.length() > 0){
				ArrayList<String> valuesList = keyValuesInMap.get(key);
				
				byte[] dataBytes = key.getBytes();
				fos.write(dataBytes);
				
				int dataBytesLength = key.length();
				String value = "";
				
				for(String val : valuesList){
					value = value + complexDelimiter + val;
				}
					
	//			System.out.println(key+"\t"+value.length());
				dataBytes = value.getBytes();
				fos.write(dataBytes);
				dataBytesLength += value.length();
	
				String ip = InetAddress.getLocalHost().getHostAddress(); //ip of this machine
				LocationMeta location = new LocationMeta(start, dataBytesLength, inputDataChunkId,ip);
				
				start =  start + dataBytesLength;
				
				keyAndFileLocationMap.put(key, location);
	//			System.out.println(key+"\t"+location.toString());
			}
		}		
		fos.close();
	}
	
	
	
	
	

	
	
	
	
	public HashMap<String, LocationMeta> writeToFile(int chunkId, int start, int end, ArrayList<String> localKeyList, Map<String, ArrayList<String>> finalKeyValuesInMap) throws IOException{
		
		createDirectoryIfNotExists(RunConfig.tempFilesDirectory);
		String fileName = RunConfig.tempFilesDirectory+"/"+"tempFile-"+chunkId+".txt";
		// FileOutputStream fos = new FileOutputStream(fileName);
		int offset = 0;


		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));



		HashMap<String, LocationMeta> keyAndFileLocationMapLocal = new HashMap<String, LocationMeta>();
		
		long first = System.currentTimeMillis();
		System.out.println("--------------------------- START "+chunkId);
		
		for(int i=start; i < end && i < localKeyList.size(); i++){
			String key =null;

				key = localKeyList.get(i);	

			
			
			if(key.length() > 0){

				// ArrayList<String> valuesList = keyValuesInConcurrentMap.get(key);
				
				ArrayList<String> valuesList = finalKeyValuesInMap.get(key);	
				
				byte[] dataBytes = key.getBytes();
				// fos.write(dataBytes);
				bw.write(key);

				int dataBytesLength = key.length();
				String value = "";
				StringBuilder sbValue = new StringBuilder();
				
				for(String val : valuesList){
					val = complexDelimiter + val;
					sbValue.append(val);
				}
				value = sbValue.toString();
				dataBytes = value.getBytes();
				// fos.write(dataBytes);
				bw.write(value);
				dataBytesLength += value.length();
	
				String ip = InetAddress.getLocalHost().getHostAddress(); //ip of this machine
				LocationMeta location = new LocationMeta(offset, dataBytesLength, chunkId,ip);
				
				offset += dataBytesLength;
				

				keyAndFileLocationMapLocal.put(key, location);					

			}
		}
		// fos.close();
		bw.close();
/*		synchronized (fileChunkIdsList) {
			fileChunkIdsList.add(chunkId);			
		}*/

		long last = System.currentTimeMillis();
		System.out.println("---------------------------   END "+chunkId+"\t"+(last-first)/1000+"s");

		
		return keyAndFileLocationMapLocal;
	}
	
	
	
	
	public void writeKeyValuesToMultipleFileAndCreateTable() throws Exception{
			
//		keyValuesInConcurrentMap = (ConcurrentHashMap<String, ArrayList<String>>) keyValuesInMap;
		
		final int keyListSize = keyList.size();
		final int keysChunkSize = (int) Math.ceil(keyListSize/(1.0*10));
		int chunkId = inputDataChunkId*1000;
		int processedCount = 0;
		
		final ArrayList<String> finalKeyList = keyList;
		final Map<String, ArrayList<String>> finalKeyValuesInMap = keyValuesInMap;
		
		List<FutureTask<HashMap<String, LocationMeta>>> taskList = new ArrayList<FutureTask<HashMap<String, LocationMeta>>>();
		
		for(int start = 0; start < keyListSize; ){
			
			final int curChunkId = chunkId;
			final int curStart = start;
					
			//future task with
			// Start thread for the first half of the numbers
	        FutureTask<HashMap<String, LocationMeta>> futureTask_1 = new FutureTask<HashMap<String, LocationMeta>>(new Callable<HashMap<String, LocationMeta>>() {
	            //@Override
	            public HashMap<String, LocationMeta> call() throws Exception {
	            	return writeToFile(curChunkId, curStart, curStart + keyListSize, finalKeyList, finalKeyValuesInMap);
		            }
		        });
		        taskList.add(futureTask_1);
		        executorService.execute(futureTask_1);
		        
		        start += keysChunkSize;
				chunkId++;
				processedCount++;
		}	
			

		List<HashMap<String, LocationMeta>> listOfMaps = new ArrayList<HashMap<String, LocationMeta>>();
		
	    // Wait until all results are available and combine them at the same time
	    for (int j = 0; j < processedCount; j++) {
	        FutureTask<HashMap<String, LocationMeta>> futureTask = taskList.get(j);	        
	        System.out.println("Completed creating file: "+j);
	        listOfMaps.add(futureTask.get());
	        
	    }
	    for(int i=0; i<processedCount; i++){
	    	fileChunkIdsList.add(inputDataChunkId*1000 + i);
	    	keyAndFileLocationMap.putAll(listOfMaps.get(i));
	    }
	}
}



class RunReducerClass /*implements Runnable*/{

	
	Object instance;
	String key;
	Method printit;
	ArrayList<LocationMeta> locations;
	ExecutorService executor;
	
    public RunReducerClass(ExecutorService executor, Object instance, Method printit, String key){
    	this.executor = executor;
    	this.instance = instance;
    	this.key = key;
    	this.printit = printit;
    }
    
	public void run() {
		try {
			
			locations = Slave.worker.keysAndMapperLocations.get(key);
			ArrayList<String> values = getValuesForKeyFromMaps();
			Object value = null;
			if(values.size() > 0)
				value = printit.invoke(instance,key,values, Slave.worker);	// can replace value with Slave.worker.keyValuesInReducer.get(key) 
			Slave.worker.completedReducerKeys.add(key);			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getValuesForKeyFromMaps() throws InterruptedException, ExecutionException, UnknownHostException{
		// for each key, get the values from list of mappers of that key
		// keysAndIps

		// ArrayList<LocationMeta> locations = keysAndMapperLocations.get(key);
		// reducer client to map server
		
		
		
//        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        List<FutureTask<ArrayList<String>>> taskList = new ArrayList<FutureTask<ArrayList<String>>>();
        final String localIp = InetAddress.getLocalHost().getHostAddress().toString().split(":")[0];
        
        final HashMap<String, ArrayList<LocationMeta>> locationsMap = new HashMap<String, ArrayList<LocationMeta>>();
        
        for(LocationMeta location : locations){
        	if(locationsMap.containsKey(location.getIp())){
        		ArrayList<LocationMeta> locationList = locationsMap.get(location.getIp());
        		locationList.add(location);
        		locationsMap.put(location.getIp(), locationList);
        	}else{
        		ArrayList<LocationMeta> locationList = new ArrayList<LocationMeta>();
        		locationList.add(location);
        		locationsMap.put(location.getIp(), locationList);
        	}
        }
        
        
		/*for(final LocationMeta location : locations){
			
			//future task with
			// Start thread for the first half of the numbers
	        FutureTask<ArrayList<String>> futureTask_1 = new FutureTask<ArrayList<String>>(new Callable<ArrayList<String>>() {
	            //@Override
	            public ArrayList<String> call() throws Exception {
	            	if(location.getIp().equals(localIp))
	            		return Slave.worker.valueForKey(key, location);
	            	else	
	            		return getValuesFromSingleRemoteMap(key, location);
	            }
	        });
	        taskList.add(futureTask_1);
	        executor.execute(futureTask_1);
		}
		
		int threadNum = locations.size();
		*/
 
        int threadNum = locationsMap.keySet().size();
        
        for(final String locationIp : locationsMap.keySet()){
        	//future task with
			// Start thread for the first half of the numbers
	        FutureTask<ArrayList<String>> futureTask_1 = new FutureTask<ArrayList<String>>(new Callable<ArrayList<String>>() {
	            //@Override
	            public ArrayList<String> call() throws Exception {
	            	if(locationIp.equals(localIp)){
						// uncomment this
	            		// System.out.println(" ****** LOCAL ****** "+locationsMap.get(locationIp));
	            		return Slave.worker.valueForKeyLocationSet(key, locationsMap.get(locationIp));
	            	}
	            	else{
						// uncomment this
	            		// System.out.println(" ****** REMOTE ****** "+locationsMap.get(locationIp)+"\t"+localIp+"\t"+locationIp);
	            		return getValuesFromSingleRemoteMapLocationSet(key, locationsMap.get(locationIp));
	            	}
	            }
	        });
	        taskList.add(futureTask_1);
	        executor.execute(futureTask_1);
        }
        
        ArrayList<String> valuesFromMappers = new ArrayList<String>();
        // Wait until all results are available and combine them at the same time
        for (int j = 0; j < threadNum; j++) {
            FutureTask<ArrayList<String>> futureTask = taskList.get(j);
            valuesFromMappers.addAll(futureTask.get());
        }
        if(valuesFromMappers.size() > 0)
        	Slave.worker.keyValuesInReducer.put(key, valuesFromMappers);
        
        return valuesFromMappers;
	}
	
	
	/*public ArrayList<String> getValuesFromLocalMap(String key, LocationMeta location) throws Exception{
		return Slave.worker.valueForKey(key, location);
	}*/
	
	
	public static ArrayList<String> getValuesFromSingleRemoteMapLocationSet(String key, ArrayList<LocationMeta> locations) throws Exception{
		
		KeyLocationsSet.Builder keyLocationsSet = KeyLocationsSet.newBuilder();
		keyLocationsSet.setKey(key);
		
		for(LocationMeta location : locations){
			Location.Builder locn = Location.newBuilder();
			locn.setChunk(location.getChunkId());
			locn.setIp(location.getIp());
			locn.setStart(location.getStart());
			locn.setLength(location.getLength());
			
			keyLocationsSet.addLocations(locn);
		}
		
		
		CommandsClient commandClient = new CommandsClient(locations.get(0).getIp(), RunConfig.slaveServerPort);
		commandClient.startConnection();
		
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("RETURN_VALUES_FOR_KEY_LOCATION_SET");
		command.addKeysAndLocationsSet(keyLocationsSet);
		//command.setKeysAndLocationsSet(keyLocationsSet);
		
		CommandResponse response = commandClient.sendCommand(command.build());
		// *************** closing connection
		commandClient.closeConnection();
		KeyValuesSet kvSet = response.getKeyValuesSet();
		
		int valCount = kvSet.getValuesCount();
		ArrayList<String> valueList = new ArrayList<String>();
		
		for(int i=0; i < valCount; i++){
			valueList.add(kvSet.getValues(i));
		}
		return valueList;
	}
	
	
	public static ArrayList<String> getValuesFromSingleRemoteMap(String key, LocationMeta location) throws Exception{
		// send command "RETURN_VALUES_FOR_KEY" and return result
		
		Location.Builder locn = Location.newBuilder();
		locn.setChunk(location.getChunkId());
		locn.setIp(location.getIp());
		locn.setStart(location.getStart());
		locn.setLength(location.getLength());
		
		KeyLocation.Builder keyLocation = KeyLocation.newBuilder();
		keyLocation.setKey(key);
		keyLocation.setLocation(locn.build());
		
		// CommandsClient commandClient = new CommandsClient("127.0.0.1", "8476"); // use location.getIp(); for ip
		CommandsClient commandClient = new CommandsClient(location.getIp(), RunConfig.slaveServerPort);
		commandClient.startConnection();
		
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("RETURN_VALUES_FOR_KEY");
		command.setKeyLocation(keyLocation);
		
		CommandResponse response = commandClient.sendCommand(command.build());
		// *************** closing connection
		commandClient.closeConnection();
		KeyValuesSet kvSet = response.getKeyValuesSet();
		
		int valCount = kvSet.getValuesCount();
		ArrayList<String> valueList = new ArrayList<String>();
		
		for(int i=0; i < valCount; i++){
			valueList.add(kvSet.getValues(i));
		}
		return valueList;
	}
}


/*WorkerProgram.javaOpen
Displaying WorkerProgram.java.*/