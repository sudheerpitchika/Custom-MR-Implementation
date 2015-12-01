package endmodules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Master;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import responses.SendKeysAndLocationToReducers;

public class ShufflerProgram {

	Map<String, ArrayList<Location>> keysAndLocations;
	public int totalReducerCount=4;
	Map<String, String> keysReducerMap;
	
	public ShufflerProgram(){
		this.keysAndLocations = new HashMap<String, ArrayList<Location>>();
		// for key, ip mapping
		this. keysReducerMap = new HashMap<String, String>();
	}
	
	public void receiveKeyAndLocationFromMapper(String key, Location location){
		if(keysAndLocations.containsKey(key)){
			keysAndLocations.get(key).add(location);
		}
		else{
			ArrayList<Location> locationList = new ArrayList<Location>();
			locationList.add(location);
			keysAndLocations.put(key, locationList);
		}
	}
	
	public void sendKeysAndLocationsToReducers() throws InterruptedException{
		
		int reducers=0;
		
		Set<String> keySet = keysAndLocations.keySet();
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(keySet);
		Collections.sort(keyList);
		int keysCount = keyList.size();
		
		int keysCountToEachReducer = keysCount/totalReducerCount;
		
		for(int i = 0; i < totalReducerCount; i++){
			
			ChannelHandlerContext ctx = Master.availableClients.take();
			SocketAddress sa = ctx.channel().remoteAddress();
			
			int start = i*keysCountToEachReducer;
			
			keysReducerMap.put("ip: "+sa, ""+start);	// change it to key, ip table
			SendKeysAndLocationToReducers toReducers = new SendKeysAndLocationToReducers(ctx, keyList, keysAndLocations, start, keysCountToEachReducer);
			Thread t = new Thread(toReducers);
			t.start();
			
		}
		System.out.println("Shuffler: sent data to all reducers");
	}
}
