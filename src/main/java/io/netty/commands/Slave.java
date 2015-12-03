package io.netty.commands;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.commands.CommandsProtocol.Command;
import config.RunConfig;
import endmodules.WorkerProgram;

public class Slave {
	public static WorkerProgram worker = new WorkerProgram();
	// public static ShufflerProgram shuffler = new ShufflerProgram();
	
	ExecutorService threadPool = Executors.newFixedThreadPool(200);
	
	public static void main(String[] args) throws Exception{
		
		CommandListenerThread listenerThread = new CommandListenerThread();
		Thread t = new Thread(listenerThread);
		t.start();
		
		//CommandsClient commandClient = new CommandsClient("127.0.0.1", "8475");
		CommandsClient commandClient = new CommandsClient(RunConfig.masterServerIp, RunConfig.masterServerPort);
		commandClient.startConnection();
		Command.Builder command = Command.newBuilder();
		command.setCommandId(1);
		command.setCommandString("CONNECT");
		commandClient.sendCommand(command.build());
		// *************** closing connection
		commandClient.closeConnection();		
		
		
	}
}

class CommandListenerThread implements Runnable{

	public void run() {
		//CommandsServer commandsListener = new CommandsServer("8476");
		CommandsServer commandsListener = new CommandsServer(RunConfig.slaveServerPort);
		try {
			commandsListener.startListening();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}