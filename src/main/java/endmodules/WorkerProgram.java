package endmodules;

import io.netty.commands.CommandsClient;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyLocation;
import io.netty.commands.CommandsProtocol.KeyLocationsSet;
import io.netty.commands.CommandsProtocol.KeyValuesSet;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Slave;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
	ArrayList<Integer> inputChunkIdsList;
	HashMap<Integer,RandomAccessFile> fileStreams;
//	HashMap<Integer, Long> fileSizes;
	String inputData;
	FileOutputStream reduceOs;
	static int reducersProcessed = 0;
	BlockingQueue<String> completedReducerKeys = new LinkedBlockingQueue<String>();
	int inputChunkId;

	public WorkerProgram(){

		complexDelimiter = "#";
		fileStreams = new HashMap<Integer,RandomAccessFile>();
//		fileSizes = new HashMap<Integer,Long>();
		inputChunkIdsList = new ArrayList<Integer>();
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
		this.inputChunkId = inputChunkId;
		byte[] buffer = inputDataString.getBytes();
		
		inputChunkIdsList.add(inputChunkId);
		
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
		for(int chunkId : inputChunkIdsList){
			String fileName = RunConfig.tempFilesDirectory + "/" + "tempFile-" + chunkId + ".txt";
			RandomAccessFile raf = new RandomAccessFile(fileName, "r");
			fileStreams.put(chunkId,raf);
			System.out.println(chunkId+"  Opening File: "+fileName);
//			fileSizes.put(chunkId, file.length());
		}
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
//		System.out.println("Data String: "+dataString);
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
	//		System.out.println("Data String: "+dataString);
			String[] splits = dataString.split(complexDelimiter);
			
			ArrayList<String> values = new ArrayList<String> ( Arrays.asList(splits));
			values.remove(0);
			valuesList.addAll(values);
		}
		
		
		System.out.println("*** Returning values for key: "+key+"\t size: "+valuesList.size());
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
		
		if(keyValuesInMap.containsKey(key)){
			ArrayList<String> values = keyValuesInMap.get(key);
			values.add(value);
			keyValuesInMap.put(key, values);
		}else{
			ArrayList<String> values = new ArrayList<String>();
			values.add(value);
			keyValuesInMap.put(key, values);
		}
//		System.out.println("Size: "+keyValuesInMap.size());
	}
	

	public void write(String key, String value) throws IOException{
		String outVal = key+"\t"+value;
		reduceOs.write(outVal.getBytes());
		reduceOs.write("\n".getBytes());
	}
	
	//call this once map() is completed
	public void writeKeyValuesToFileAndCreateTable() throws Exception{
		
		createDirectoryIfNotExists(RunConfig.tempFilesDirectory);
		String fileName = RunConfig.tempFilesDirectory+"/"+"tempFile-"+inputChunkId+".txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		
		int start = 0;
		
		for(String key : keyList){
			if(key.trim().length() > 0){
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
				LocationMeta location = new LocationMeta(start, dataBytesLength, inputChunkId,ip);
				
				start =  start + dataBytesLength;
				
				keyAndFileLocationMap.put(key, location);
	//			System.out.println(key+"\t"+location.toString());
			}
		}		
		fos.close();
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
			Object value = printit.invoke(instance,key,values, Slave.worker);	// can replace value with Slave.worker.keyValuesInReducer.get(key) 
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
	            		System.out.println(" ****** LOCAL ****** "+locationsMap.get(locationIp));
	            		return Slave.worker.valueForKeyLocationSet(key, locationsMap.get(locationIp));
	            	}
	            	else{
	            		System.out.println(" ****** REMOTE ****** "+locationsMap.get(locationIp)+"\t"+localIp+"\t"+locationIp);
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


