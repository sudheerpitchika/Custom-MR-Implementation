package endmodules;

import io.netty.commands.CommandsClient;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyLocation;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Slave;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class WorkerProgram {
	TaskTracker tt;
	DataNode dn;
	
	final static String complexDelimiter="#$%@@%$#";
	
	//used to send values to reducers
	Map<String, LocationMeta> keyAndFileLocationMap; //stores keys and location(offset) in file, and let of the value for key
	Map<String, ArrayList<LocationMeta>> keysAndMapperLocations;
	Map<String, ArrayList<String>> keyValuesInMap;
	Map<String, ArrayList<String>> keyValuesInReducer;
	ArrayList<String> keyList;
	ArrayList<DataInputStream> fileStreams;
	String inputData;
	FileOutputStream reduceOs;
	
	public WorkerProgram(){
		keyValuesInMap = new HashMap<String, ArrayList<String>>();
		keyValuesInReducer = new HashMap<String, ArrayList<String>>();
		keyAndFileLocationMap = new HashMap<String, LocationMeta>();
		fileStreams = new ArrayList<DataInputStream>();
		keysAndMapperLocations = new HashMap<String, ArrayList<LocationMeta>>();
		inputData = null;
	}
	
	public void acceptData(String inputDataString){
			inputData = inputDataString;
			byte[] buffer = inputDataString.getBytes();
			File jOutFile = new File("receivedData.txt");
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
		int chunkId = 0;
		// for loop here
		String fileName = "tempFile"+chunkId+".txt";
		File file = new File(fileName);
		DataInputStream in = new DataInputStream(new FileInputStream(file));		
		fileStreams.add(in);
	}
	
	
	//start a thread and listen for master program

	public void startProgram(String type){ //run map or reducer task
		//
		
	}
	
	public void startMapFunction() throws Exception{
		// run map class in jar
		
		
		URL url = new URL("file:MFReceived.jar"); 
        URLClassLoader loader = new URLClassLoader (new URL[] {url});
        Class<?> cl = Class.forName ("userprogram.MapFunction", true, loader);
        Method printit = cl.getMethod("map",String.class, String.class, WorkerProgram.class );
        Constructor<?> ctor = cl.getConstructor(); //One has to pass arguments if constructor takes input arguments.
        Object instance = ctor.newInstance();
        Object value = printit.invoke(instance,"chunk-1",inputData, Slave.worker);
        loader.close ();
        System.out.println("Map completed, ele count "+keyValuesInMap.size());
	}

	public void startReduceFunction() throws Exception{
		
		// open output file to write data into, write(key, value) function
		String fileName = "output.txt";
		reduceOs = new FileOutputStream(fileName);
		
		// run reduce class in jar
		// use keyValuesInReducer
		
		
		

	}

	public void receiveData(String inputData){
		this.inputData = inputData;
	}
	
	public void startCombiner() throws Exception{
		//sort values in the map
		Set<String> keySet = keyValuesInMap.keySet();
		keyList = new ArrayList<String>();
		keyList.addAll(keySet);
		Collections.sort(keyList);
	}

	
	public void returnKeyAndLocationsToShuffler() throws Exception{
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
			location.setStart(location.getStart());
			location.setLength(locationMeta.getLength());
			
			keyLocation.setKey(key);
			keyLocation.setLocation(location);
			
			command.addKeyLocationsMap(keyLocation);
		}
		
		CommandsClient shuffleClient = new CommandsClient("127.0.0.1", "8477");
		shuffleClient.startConnection();
		shuffleClient.sendCommand(command.build());
	}
	
	public Map<String, LocationMeta> getKeyAndLocations(){
		// sends the key set available at the worker when the shuffeler requests
		// send keys and their locations too (offset, length, chunk id)
				
		// RETURN_KEYS_AND_LOCATIONS
		
		return keyAndFileLocationMap;
	}
	
	public ArrayList<String> valueForKey(String key, LocationMeta location) throws Exception{
		//get the value from the file(for corresponding chunk) //requested by reducer
		
		DataInputStream in = fileStreams.get(location.getChunkId());
		byte[] dataBytes=null;
		in.read(dataBytes, location.getStart(), location.getLength());
		String dataString = dataBytes.toString();
		String[] splits = dataString.split(complexDelimiter);
		// String returnKey = splits[0];
		
		ArrayList<String> values = new ArrayList<String> ( Arrays.asList(splits));
		values.remove(values.size());
		values.remove(0);

		return values;
	}

	public void acceptKeyAndMapperLocationSetFromShuffler( Map<String, ArrayList<LocationMeta>> map){
		//reducer server
		//read from buffer and add to keysAndMapperLocations
		this.keysAndMapperLocations = map;		
	}
	
	public Map<String, ArrayList<String>> getValuesForKeyFromMaps(final String key) throws InterruptedException, ExecutionException{
		// for each key, get the values from list of mappers of that key
		// keysAndIps

		ArrayList<LocationMeta> locations = keysAndMapperLocations.get(key);
		// reducer client to map server
		
		int threadNum = locations.size();
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        List<FutureTask<ArrayList<String>>> taskList = new ArrayList<FutureTask<ArrayList<String>>>();
        
		for(final LocationMeta location : locations){
			
			//future task with
			// Start thread for the first half of the numbers
	        FutureTask<ArrayList<String>> futureTask_1 = new FutureTask<ArrayList<String>>(new Callable<ArrayList<String>>() {
	            //@Override
	            public ArrayList<String> call() throws Exception {
	                return WorkerProgram.getValuesFromSingleMap(key, location);
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
        executor.shutdown();
        keyValuesInReducer.put(key, valuesFromMappers);
        
        return keyValuesInReducer;
	}
	
	
	public static ArrayList<String> getValuesFromSingleMap(String key, LocationMeta location) throws Exception{
		// send command "RETURN_VALUES_FOR_KEY" and return result
		
		CommandsClient commandClient = new CommandsClient("127.0.0.1", "8475");
// 		CommandsClient commandClient = new CommandsClient(location.getIp(), "8475");
		commandClient.startConnection();
		
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("RETURN_VALUES_FOR_KEY");
		commandClient.sendCommand(command.build());
		return null;
	}
	
	public void emit(String key, String value){
		//keyValuesMap
		
		System.out.println("In Emit Function "+key+"\t"+value);
		
		if(keyValuesInMap.containsKey(key)){
			ArrayList<String> values = keyValuesInMap.get(key);
			values.add(value);
			keyValuesInMap.put(key, values);
		}else{
			ArrayList<String> values = new ArrayList<String>();
			values.add(value);
			keyValuesInMap.put(key, values);
		}
		System.out.println("Size: "+keyValuesInMap.size());
	}
	
	
	public void write(String key, String value) throws IOException{
		String outVal = key+"\t"+value;
		reduceOs.write(outVal.getBytes());
		reduceOs.write("\n".getBytes());
	}
	
	//call this once map() is completed
	public void writeKeyValuesToFileAndCreateTable() throws Exception{
		int chunkId = 0;
		String fileName = "tempFile"+chunkId+".txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		
		int start = 0;
		
		for(String key : keyList){
			ArrayList<String> values = keyValuesInMap.get(key);
			
			byte[] dataBytes = key.getBytes();
			fos.write(dataBytes);
			
			int dataBytesLength = key.length();

			for(String val : values){
				val = complexDelimiter + val;
				dataBytes = val.getBytes();
				fos.write(dataBytes);
				dataBytesLength += val.length();
			}
			String ip = ""; //ip of this machine
			LocationMeta location = new LocationMeta(start, dataBytesLength, chunkId,ip);
			start += dataBytesLength;
			keyAndFileLocationMap.put(key, location);
		}		
		fos.close();
	}
}

