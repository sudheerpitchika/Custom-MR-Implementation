package io.netty.commands;

public class Slave {
	public static void main(String[] args) throws Exception{
		CommandsServer commandsListener = new CommandsServer("8475");
		commandsListener.startListening();
	}
}