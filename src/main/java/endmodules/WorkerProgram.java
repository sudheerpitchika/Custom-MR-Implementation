package endmodules;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	Map<String, Location> keyLocationMap; //stores keys and location(offset) in file, and let of the value for key
	//used to send values to reducers
	
	Map<String, ArrayList<Location>> keysAndIps;
	Map<String, ArrayList<String>> keyValuesMap;
	ArrayList<String> keyList;
	ArrayList<DataInputStream> fileStreams;
	
	public WorkerProgram(){
		keyValuesMap = new HashMap<String, ArrayList<String>>();
		keyLocationMap = new HashMap<String, Location>();
		fileStreams = new ArrayList<DataInputStream>();
		keysAndIps = new HashMap<String, ArrayList<Location>>();
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
		
	}

	public void startCombiner() throws Exception{
		//sort values in the map
		Set<String> keySet = keyValuesMap.keySet();
		keyList = new ArrayList<String>();
		keyList.addAll(keySet);
		Collections.sort(keyList);
		this.writeKeyValuesFileAndCreateTable();
	}

	public Map<String, Location> returnKeyAndLocationsToShuffler(){
		// sends the key set available at the worker when the shuffeler requests
		// send keys and their locations too (offset, length, chunk id)
		
		// set protobuf objects and write to shuffle server
		
		// RETURN_KEYS_AND_LOCATIONS
		
		return keyLocationMap;
	}
	
	public ArrayList<String> valueForKey(String key, Location location) throws Exception{
		//get the value from the file(for corresponding chunk) 
		
		DataInputStream in = fileStreams.get(location.getChunkId());
		byte[] dataBytes=null;
		in.read(dataBytes, location.getStart(), location.getLength());
		String dataString = dataBytes.toString();
		String[] splits = dataString.split(complexDelimiter);
		String returnKey = splits[0];
		
		ArrayList<String> values = new ArrayList<String> ( Arrays.asList(splits));
		values.remove(values.size());
		values.remove(0);

		return null;
	}
	
	public void receiveKeyIpSetFromShuffler(){
		//reducer server
	}
	
	public void getValuesForKeyFromMaps(final String key) throws InterruptedException, ExecutionException{
		// for each key, get the values from list of mappers of that key
		// keysAndIps

		ArrayList<Location> locations = keysAndIps.get(key);
		// reducer client to map server
		
		int threadNum = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        List<FutureTask<ArrayList<String>>> taskList = new ArrayList<FutureTask<ArrayList<String>>>();
        
		for(final Location location : locations){
			
			//future task with
			// Start thread for the first half of the numbers
	        FutureTask<ArrayList<String>> futureTask_1 = new FutureTask<ArrayList<String>>(new Callable<ArrayList<String>>() {
	            //@Override
	            public ArrayList<String> call() {
	                return WorkerProgram.getValuesFromSingleMap(key, location);
	            }
	        });
	        taskList.add(futureTask_1);
	        executor.execute(futureTask_1);

		}
		
		ArrayList<String> finalValues = new ArrayList<String>();
		
		// Wait until all results are available and combine them at the same time
        for (int j = 0; j < threadNum; j++) {
            FutureTask<ArrayList<String>> futureTask = taskList.get(j);
            finalValues.addAll(futureTask.get());
        }
        executor.shutdown();
	}
	
	
	public static ArrayList<String> getValuesFromSingleMap(String key, Location location){
		// send command "VALUE_FOR_KEY"
		return null;
	}
	
	public void emit(String key, String value){
		//keyValuesMap
		if(keyValuesMap.containsKey(key)){
			ArrayList<String> values = keyValuesMap.get(key);
			values.add(value);
			keyValuesMap.put(key, values);
		}else{
			ArrayList<String> values = new ArrayList<String>();
			values.add(value);
			keyValuesMap.put(key, values);
		}
	}
	
	public void writeKeyValuesFileAndCreateTable() throws Exception{
		int chunkId = 0;
		String fileName = "tempFile"+chunkId+".txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		
		int start = 0;
		
		for(String key : keyList){
			ArrayList<String> values = keyValuesMap.get(key);
			String valueString = key + complexDelimiter;
			
			for(String val : values){
				valueString = valueString + val + complexDelimiter;
			}
			
			byte[] dataBytes = valueString.getBytes();
			int dataBytesLength = dataBytes.length;
			
			Location location = new Location(start, dataBytesLength, chunkId);
			start += dataBytesLength;
			keyLocationMap.put(key, location);
			
			fos.write(dataBytes);
		}		
		fos.close();
	}
}

class Location{
	private int start, length, chunkId;
	private String ip;
	
	public Location(int start, int length, int chunkId){
		this.start = start;
		this.length = length;
		this.chunkId = chunkId;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getChunkId() {
		return chunkId;
	}

	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
		
}