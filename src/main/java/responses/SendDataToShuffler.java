package responses;

import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.KeyLocation;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.CommandsClient;
import io.netty.commands.Slave;

import java.util.Map;
import java.util.Set;

import endmodules.LocationMeta;

public class SendDataToShuffler implements Runnable{
	
	CommandsClient commandClient;
	
	public SendDataToShuffler() throws Exception{
		//shuffler ip, port
		commandClient = new CommandsClient("127.0.0.1", "8115");
		commandClient.startConnection();
	}
	
	public void run(){
		
		Map<String, LocationMeta> keyAndFileLocationMap = Slave.worker.getKeyAndLocations();
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
			//cmdResponse.addKeyLocationTable(kl);
		}
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("ACCEPT_DATA_SHUFFLER");
		// send the above data also
		
		commandClient.sendCommand("");
	}
}
