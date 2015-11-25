package io.netty.commands;

import endmodules.ShufflerProgram;
import endmodules.WorkerProgram;

public class Slave {
	public static WorkerProgram worker = new WorkerProgram();
	public static ShufflerProgram shuffler = new ShufflerProgram();
	
	public static void main(String[] args) throws Exception{
		
		CommandsServer commandsListener = new CommandsServer("8475");
		commandsListener.startListening();
	}
}