package io.netty.commands;

import io.netty.commands.CommandsProtocol.Command;
import endmodules.ShufflerProgram;
import endmodules.WorkerProgram;

public class Slave {
	public static WorkerProgram worker = new WorkerProgram();
	// public static ShufflerProgram shuffler = new ShufflerProgram();
	
	public static void main(String[] args) throws Exception{
		
		CommandListenerThread listenerThread = new CommandListenerThread();
		Thread t = new Thread(listenerThread);
		t.start();
		
		CommandsClient commandClient = new CommandsClient("127.0.0.1", "8475");
		commandClient.startConnection();
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("CONNECT");
		commandClient.sendCommand(command.build());
				
		
		
	}
}

class CommandListenerThread implements Runnable{

	public void run() {
		CommandsServer commandsListener = new CommandsServer("8476");
		try {
			commandsListener.startListening();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}