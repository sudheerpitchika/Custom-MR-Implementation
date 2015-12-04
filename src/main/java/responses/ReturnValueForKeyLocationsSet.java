package responses;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyLocationsSet;
import io.netty.commands.CommandsProtocol.KeyValuesSet;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Slave;
import endmodules.LocationMeta;

public class ReturnValueForKeyLocationsSet{
	
	ChannelHandlerContext ctx;
	Command command;
	
	public ReturnValueForKeyLocationsSet(ChannelHandlerContext ctx, Command command){
		this.ctx = ctx;
		this.command = command;
	}
	
	public Void run(){
    	
		ArrayList<LocationMeta> locationMetaList = new ArrayList<LocationMeta>();
		
		List<KeyLocationsSet> keyLocationsSet = command.getKeysAndLocationsSetList();
		KeyLocationsSet keyLocnSet = keyLocationsSet.get(0);

		String key = keyLocnSet.getKey();
		List<Location> locations = keyLocnSet.getLocationsList();
		
		for(Location location : locations){
			int start = location.getStart();
			int length = location.getLength();
			int chunkId = location.getChunk();
			String ip = location.getIp();
			
			LocationMeta locationMeta = new LocationMeta(start, length, chunkId, ip);
			locationMetaList.add(locationMeta);
		}


		ArrayList<String> valuesForKey = null;
    	try {
    		valuesForKey = Slave.worker.valueForKeyLocationSet(key, locationMetaList);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	CommandResponse.Builder cmdResponse = CommandResponse.newBuilder();
    	cmdResponse.setForCommandId(command.getCommandId());
    	cmdResponse.setForCommandString(command.getCommandString());
    	cmdResponse.setResponseText("OK "+command.getCommandString());
    	
    	KeyValuesSet.Builder keyValueSet = KeyValuesSet.newBuilder();
    	keyValueSet.setKey(key);
    	for(String value : valuesForKey){
    		keyValueSet.addValues(value);
    	}
    	
    	cmdResponse.setKeyValuesSet(keyValueSet.build());
    	ctx.writeAndFlush(cmdResponse.build());
		return null;
	}
}
