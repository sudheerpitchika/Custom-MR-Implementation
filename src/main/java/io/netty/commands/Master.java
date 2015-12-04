package io.netty.commands;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.heartbeats.HeartBeatServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import config.RunConfig;
import endmodules.ShufflerProgram;


public class Master {
	
	public static final ArrayList<ChannelHandlerContext> connectedClients = new ArrayList<ChannelHandlerContext>(); 
	public static final BlockingQueue<ChannelHandlerContext> availableClients = new LinkedBlockingQueue<ChannelHandlerContext>();
	
	static int numberOfClients = 0;
	static int completedMapsCount = 0;
	static int completedReducersCount = 0;
	
	public static ShufflerProgram shuffler = new ShufflerProgram();
	static JobTracker jobTracker;
	static ShuffleServerThread shuffleServer;
	
	public static void main(String[] args) throws Exception{
		
		//CommandServerThread commandsServer = new CommandServerThread("8475");
		CommandServerThread commandsServer = new CommandServerThread(RunConfig.masterServerPort);
		Thread servThread = new Thread(commandsServer);
		servThread.start();
		
 Thread.sleep(2000);

		//HeartBeatServerThread myRunnable = new HeartBeatServerThread("8478");
		HeartBeatServerThread myRunnable = new HeartBeatServerThread(RunConfig.heartBeatServerPort);
	     Thread t = new Thread(myRunnable);
	     t.start();

// Thread.sleep(2000);
 
     	//shuffleServer = new ShuffleServerThread("8477");
	     shuffleServer = new ShuffleServerThread(RunConfig.shuffleServerPort);
		Thread shuffleThread = new Thread(shuffleServer);
		shuffleThread.start();
     

// Thread.sleep(2000);
			
		jobTracker = new JobTracker();
		Thread jobTrackerThread = new Thread(jobTracker);
		jobTrackerThread.start();

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
	CommandsServer masterListener;

	public CommandServerThread(String port){
		this.masterListener = new CommandsServer(port);
	}
	
	public void run() {	
		try {
			masterListener.startListening();
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
	int numberOfMappers=0;
	
	public JobTracker(){
		
	}
	
	public void run(){
		
		RandomAccessFile raf = null;
	    long fileLength = 1;
		try {
			
			raf = new RandomAccessFile("inputdata.txt", "r");
			fileLength = raf.length();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
	System.out.println("File length "+fileLength);
		int chunkSize = RunConfig.chunkSize;
		int offset = 0;
//		chunkSize = 1024 * 1;
		chunksCount = (int) Math.ceil(fileLength/(chunkSize*1.0));
//		chunksCount=1;
		
		System.out.println("Chunks Count "+chunksCount);
		numberOfMappers = chunksCount;
		
		try {
			int chunkId = 0;
			while(chunksCount > 0){
				ChannelHandlerContext ctx = Master.availableClients.take();
				SocketAddress sa = ctx.channel().remoteAddress();
				String s = sa.toString();

				
				System.out.println("SENDING TO MAP: "+s+"\t"+chunkId+"\t"+offset+"\t"+chunkSize);
				SendData sendDataClient = new SendData (raf, chunkId, offset, chunkSize, ctx);
				Thread t = new Thread(sendDataClient);
				t.start();
				
				offset += chunkSize;
				chunkId++;
				chunksCount--;
			}			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int getChunksCount(){
		return chunksCount;
	}
	
	public int getNumberOfMappers(){
		return numberOfMappers;
	}
	
	public void stopAllHeartBeats() throws Exception{
		
		for(ChannelHandlerContext ctx : Master.connectedClients){
			String remoteAddress = ctx.channel().remoteAddress().toString();
			CommandsClient cmdClient = new CommandsClient(remoteAddress.split(":")[0].substring(1), RunConfig.slaveServerPort);
			cmdClient.startConnection();
			
			Command.Builder command = Command.newBuilder();
			command.setCommandId(1);
			command.setCommandString("SHUTDOWN");
			cmdClient.sendCommandAsync(command.build());
			cmdClient.closeConnection();
    	}
		
	}
} 

class SendData implements Runnable{

	/*BufferedInputStream bis;*/
	RandomAccessFile raf;
	int chunkId;
	int offset;
	int length;
	ChannelHandlerContext ctx;
	
	public SendData (RandomAccessFile raf, int chunkId, int offset, int length, ChannelHandlerContext ctx){
		
		this.raf = raf;
		this.chunkId = chunkId;
		this.offset = offset;
		this.length = length;
		this.ctx = ctx;
		
		/*String fileName = "inputdata.txt";
		File jInFile = new File(fileName);
		
		try {
			bis = new BufferedInputStream(new FileInputStream(jInFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}*/
	}
	
	public void run() {
		/*byte[] byteData = new byte[length+10];
		try {
			System.out.println("Trying to read from "+offset+" to "+(offset+length-1));
			bis.read(byteData, offset, length-1);
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		
        byte[] byteData = new byte[(int) length];

		try {
			raf.seek(offset);
	        raf.read(byteData);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        String stringData = new String(byteData);
		Command.Builder cmd = Command.newBuilder();
		cmd.setCommandId(1);
		cmd.setCommandString("ACCEPT_DATA");
		cmd.setInputChunk(stringData);
		cmd.setInputChunkId(chunkId);
		
		String remoteAddress =  ctx.channel().remoteAddress().toString();
		// CommandsClient cc = new CommandsClient("127.0.0.1", "8476");
		CommandsClient cc = new CommandsClient(remoteAddress.split(":")[0].substring(1), RunConfig.slaveServerPort);
		
		
		try {
			cc.startConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cc.sendCommand(cmd.build());
		
		cmd.setCommandString("START_MAP");
		cc.sendCommand(cmd.build());
		// *************** closing connection
		cc.closeConnection();
	}
} 