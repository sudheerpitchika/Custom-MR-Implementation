package responses;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.Slave;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.CommandsProtocol.KeyLocation;
import io.netty.commands.CommandsProtocol.Location;

import java.util.Map;
import java.util.Set;

import endmodules.LocationMeta;
import endmodules.WorkerProgram;

public class ReturnKeysAndLocations implements Runnable {
	
	private WorkerProgram worker;
	private ChannelHandlerContext ctx;
	CommandResponse.Builder cmdResponse;
	
	public ReturnKeysAndLocations(ChannelHandlerContext ctx, CommandResponse.Builder cmdResponse){
		this.worker = Slave.worker;
		this.ctx = ctx;
		this.cmdResponse = cmdResponse;
	}
	
	public void run(){
		
		Map<String, LocationMeta> keyAndFileLocationMap = worker.getKeyAndLocations();
		Set<String> keySet =  keyAndFileLocationMap.keySet();
		
		for(String key : keySet){
			LocationMeta location = keyAndFileLocationMap.get(key);
			KeyLocation.Builder keyLocnBuilder = KeyLocation.newBuilder();
			
			Location.Builder locnBuilder = Location.newBuilder();
			locnBuilder.setChunk(location.getChunkId());
			locnBuilder.setIp("");
			locnBuilder.setStart(location.getStart());
			locnBuilder.setLength(location.getLength());
			
			keyLocnBuilder.setKey(key);
			keyLocnBuilder.setLocation(locnBuilder.build());
			
			KeyLocation kl = keyLocnBuilder.build();
//			cmdResponse.addKeyLocationTable(kl);
		}
		
		ctx.writeAndFlush(cmdResponse);
	}
}

