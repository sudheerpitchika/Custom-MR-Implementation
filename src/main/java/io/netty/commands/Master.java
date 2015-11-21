package io.netty.commands;

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
	        
		commandClient.sendCommand("START_TASK_TRACKER");
		Thread.sleep(4000);
		commandClient.sendCommand("START_SHUFFLE");
		
		Thread.sleep(6000);
		commandClient.sendCommand("SHUTDOWN");
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