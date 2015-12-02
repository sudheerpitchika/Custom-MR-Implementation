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
			System.out.println("MAP DONE");
			Slave.worker.startCombiner();
			System.out.println("COMBINER DONE");
			Slave.worker.writeKeyValuesToFileAndCreateTable();
			System.out.println("CREATE TABLES DONE");
			Slave.worker.sendKeyAndLocationsToShuffler();
			System.out.println("KEY TO SHUFFLER DONE");
			
			CommandsClient client = new CommandsClient("127.0.0.1", "8475");
			client.startConnection();
			Command.Builder command = Command.newBuilder();
			command.setCommandId(1);
			command.setCommandString("MAP_COMPLETE");
			client.sendCommand(command.build());
			System.out.println("MAP COMPLETE DONE");
			Slave.worker.openAllFiles();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// send completion status once done
		// also send keysandlocations to shuffler		
	}
}
