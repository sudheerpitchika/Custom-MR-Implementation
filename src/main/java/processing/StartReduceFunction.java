package processing;

import io.netty.commands.CommandsClient;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.Slave;

public class StartReduceFunction implements Runnable{

	public StartReduceFunction(){
		
	}
	
	public void run(){
		try {
			
			Slave.worker.startReduceFunction();
			
			Command.Builder command = Command.newBuilder();
			command.setCommandId(1);
			command.setCommandString("REDUCE_COMPLETE");
			
			CommandsClient commandClient  = new CommandsClient("127.0.0.1", "8475");
			commandClient.startConnection();
			commandClient.sendCommand(command.build());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
