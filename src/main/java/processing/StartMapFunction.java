package processing;

import io.netty.commands.CommandsClient;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.Slave;

public class StartMapFunction implements Runnable{

	public StartMapFunction(){
		
	}
	
	public void run(){
		try {
			Slave.worker.startMapFunction();

			Slave.worker.startCombiner();

			Slave.worker.writeKeyValuesToFileAndCreateTable();

			Slave.worker.sendKeyAndLocationsToShuffler();

			Slave.worker.openAllFiles();
			
			CommandsClient client = new CommandsClient("127.0.0.1", "8475");
			client.startConnection();
			Command.Builder command = Command.newBuilder();
			command.setCommandId(1);
			command.setCommandString("MAP_COMPLETE");
			client.sendCommand(command.build());
			
			System.out.println("***** MAP COMPLETE **** ");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// send completion status once done
		// also send keysandlocations to shuffler		
	}
}
