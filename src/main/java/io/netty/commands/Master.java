package io.netty.commands;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.heartbeats.HeartBeatServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Master {
	
//	static final ChannelGroup connectedClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	static ArrayList<ChannelHandlerContext> connectedClients = new ArrayList<ChannelHandlerContext>(); 
	static final BlockingQueue<ChannelHandlerContext> availableClients = new LinkedBlockingQueue<ChannelHandlerContext>();
	static int numberOfClients = 0;
	
	public static void main(String[] args) throws Exception{
		
		
		CommandServerThread commandsServer = new CommandServerThread("8475");
		Thread servThread = new Thread(commandsServer);
		servThread.start();
		
		ShuffleServerThread shuffleServer = new ShuffleServerThread("8477");
		Thread shuffleThread = new Thread(shuffleServer);
		shuffleThread.start();
		
		JobTracker jobTracker = new JobTracker();
		Thread jobTrackerThread = new Thread(jobTracker);
		jobTrackerThread.start();
		
		
	/*	
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
	*/
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


class CommandServerThread implements Runnable{
	CommandsServer commandsListener;

	public CommandServerThread(String port){
		this.commandsListener = new CommandsServer(port);
	}
	
	public void run() {	
		try {
			commandsListener.startListening();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}

class ShuffleServerThread implements Runnable{

	CommandsServer shuffleListener;
	public ShuffleServerThread(String port){
		this.shuffleListener = new CommandsServer(port);
	}
	
	public void run() {
		try {
			shuffleListener.startListening();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class JobTracker implements Runnable{
	int chunksCount = 10;
	public JobTracker(){
		
	}
	public void run(){
		
		int length = 4194304;
		int offset = 0;
		length = 4096;
        
		try {
			
			while(chunksCount>0){
				ChannelHandlerContext ctx = Master.availableClients.take();
				SocketAddress sa = ctx.channel().remoteAddress();
				SendData sendDataClient = new SendData (offset, length, ctx);
				Thread t = new Thread(sendDataClient);
				t.start();
				
				offset += length;
				chunksCount--;
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
} 

class SendData implements Runnable{

	BufferedInputStream bis;
	int offset;
	int length;
	ChannelHandlerContext ctx;
	
	public SendData (int offset, int length, ChannelHandlerContext ctx){
		
		this.offset = offset;
		this.length = length;
		this.ctx = ctx;
		
		File jInFile = new File("inputdata.txt");
        try {
			bis = new BufferedInputStream(new FileInputStream(jInFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        
	}
	
	public void run() {
		byte[] byteData = new byte[length];
		try {
			bis.read(byteData, offset, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String stringData = new String(byteData);
		Command.Builder cmd = Command.newBuilder();
		cmd.setCommandId(1);
		cmd.setCommandString("ACCEPT_DATA");
		cmd.setInputChunk(stringData);
		
		// ctx.channel().remoteAddress();
		CommandsClient cc = new CommandsClient("127.0.0.1", "8476");
		try {
			cc.startConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cc.sendCommand(cmd.build());
		
		cmd.setCommandString("START_MAP");
		cc.sendCommand(cmd.build());
	}
} 