package responses;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsClient;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.KeyLocationsSet;
import io.netty.commands.CommandsProtocol.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendKeysAndLocationToReducers implements Runnable {

	ChannelHandlerContext ctx;
	List<String> keyList;
	Map<String, ArrayList<Location>> keysAndLocations;
	int start, count;
	public SendKeysAndLocationToReducers(ChannelHandlerContext ctx, List<String> keyList, Map<String, ArrayList<Location>> keysAndLocations, int start, int count){
		this.ctx = ctx;
		this.keyList = keyList;
		this.keysAndLocations = keysAndLocations;
		this.start = start;
		this.count = count;
	}
	
	public void run(){
		
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("ACCEPT_KEYS_AND_LOCATIONS");
		
		// KeyLocationsSet
		for( ; start < start+count && start < keyList.size(); start++){
			String key = keyList.get(start);
			ArrayList<Location> locations = keysAndLocations.get(key);
			KeyLocationsSet.Builder klSet = KeyLocationsSet.newBuilder();
			klSet.setKey(key);
			
			for(int i = 0; i < locations.size(); i++)
				klSet.addLocations(i, locations.get(i));
			
			command.addKeysAndLocationsSet(klSet);	
		}
		CommandsClient cmdClient = new CommandsClient("127.0.0.1", "8476");
		try {
			cmdClient.startConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cmdClient.sendCommand(command.build());
		
		// once completed, send START_REDUCE command to start reduce task
		command.setCommandString("START_REDUCE");
		cmdClient.sendCommand(command.build());
	}
}
