package processing;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyLocationsSet;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Slave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import endmodules.LocationMeta;

public class AcceptingKeyAndLocationsAtReducer implements Runnable{
	
	HashMap<String, ArrayList<LocationMeta>> klMap = new HashMap<String, ArrayList<LocationMeta>>();
	ChannelHandlerContext ctx;
	Command command;
	
	public AcceptingKeyAndLocationsAtReducer(ChannelHandlerContext ctx, Command command){
		this.ctx = ctx;
		this.command = command;
	}
	
	public void run(){

		List<KeyLocationsSet> keyLocationsSet = command.getKeysAndLocationsSetList();
		System.out.println("Reducer received "+keyLocationsSet.size()+" elements");
		for(KeyLocationsSet keyLocnSet : keyLocationsSet){
			String key = keyLocnSet.getKey();
			List<Location> locations = keyLocnSet.getLocationsList();
			ArrayList<LocationMeta> locationMetaList = new ArrayList<LocationMeta>();
			
			for(Location location : locations){
				int start = location.getStart();
				int length = location.getLength();
				int chunkId = location.getChunk();
				String ip = location.getIp();
				
				LocationMeta locationMeta = new LocationMeta(start, length, chunkId, ip);
				
				locationMetaList.add(locationMeta);
			}
			klMap.put(key, locationMetaList);
		}

		Slave.worker.acceptKeyAndMapperLocationSetFromShuffler(klMap);
		
		sendResponse();
	}
	
	public void sendResponse(){
	
		CommandResponse.Builder cmdResp = CommandResponse.newBuilder();
        cmdResp.setForCommandId(command.getCommandId());
        cmdResp.setForCommandString(command.getCommandString());
        cmdResp.setResponseText("OK "+command.getCommandString());
        this.ctx.writeAndFlush(cmdResp.build());
        System.out.println("after responded for command");
	}
}
