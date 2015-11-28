package io.netty.commands;

import io.netty.commands.CommandsProtocol.Command;
import io.netty.heartbeats.HeartBeatServer;


public class Master {
	public static void main(String[] args) throws Exception{
		CommandsClient commandClient = new CommandsClient("127.0.0.1", "8475");
		commandClient.startConnection();
		
		//start listening from the workers now
		// only for the first time - if it needs to be only one for all slaves
		
		Thread.sleep(4000);
		
		HeartBeatServerThread myRunnable = new HeartBeatServerThread("9898");
	     Thread t = new Thread(myRunnable);
	     t.start();
	        
	    Command.Builder command = Command.newBuilder();
	    command.setCommandId(1);
	    command.setCommandString("START_TASK_TRACKER");
		commandClient.sendCommand(command.build());
		Thread.sleep(4000);
	    command.setCommandString("START_SHUFFLE");
	    commandClient.sendCommand(command.build());
		
		Thread.sleep(6000);
	    command.setCommandString("SHUTDOWN");		
	    commandClient.sendCommand(command.build());
		commandClient.closeConnection();
		
		//stop heart beat server
	}
}


class HeartBeatServerThread implements Runnable {
	HeartBeatServer heartBeatServer;
	
    public HeartBeatServerThread(String port) {
    	heartBeatServer = new HeartBeatServer(port);
    }

    public void run() {
    	try {
    		heartBeatServer.startListening();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void stopHeartBeatServer(){
    	
    }
}