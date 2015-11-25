package responses;

import java.util.ArrayList;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyValuesSet;
import io.netty.commands.Slave;
import endmodules.LocationMeta;

public class ReturnValueForKey implements Runnable{
	
	ChannelHandlerContext ctx;
	Command command;
	
	public ReturnValueForKey(ChannelHandlerContext ctx, Command command){
		this.ctx = ctx;
		this.command = command;
	}
	
	public void run(){
    	
		String key = command.getKeyLocation().getKey();
		int chunkId = command.getKeyLocation().getLocation().getChunk();
		int start = command.getKeyLocation().getLocation().getStart();
		int length = command.getKeyLocation().getLocation().getLength();
		String ip = command.getKeyLocation().getLocation().getIp(); //not required though
		
		LocationMeta location = new LocationMeta(start, length, chunkId);
    	
		ArrayList<String> valuesForKey = null;
    	try {
    		valuesForKey = Slave.worker.valueForKey(key, location);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	CommandResponse.Builder cmdResponse = CommandResponse.newBuilder();
    	cmdResponse.setForCommandId(command.getCommandId());
    	cmdResponse.setForCommandString(command.getCommandString());
    	
    	KeyValuesSet.Builder keyValueSet = KeyValuesSet.newBuilder();
    	keyValueSet.setKey(key);
    	for(String value : valuesForKey){
    		keyValueSet.addValues(value);
    	}
    	
    	cmdResponse.setKeyValuesSet(keyValueSet.build());
    	ctx.write(cmdResponse.build());
	}
}
