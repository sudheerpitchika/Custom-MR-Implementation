package responses;

import io.netty.heartbeats.HeartBeatClient;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import config.RunConfig;

public class HeartBeats implements Runnable {
	static ScheduledThreadPoolExecutor exec;
	HeartBeatClient heartBeatClientClient;
	static int i=0;
	public HeartBeats(String ip, String port) throws Exception{
		heartBeatClientClient = new HeartBeatClient(ip, port);
		heartBeatClientClient.startConnection();
	}
	
    public void run() {
        // code in the other thread, can reference "var" variable
    	try {
    		exec = new ScheduledThreadPoolExecutor(1);
        	exec.scheduleAtFixedRate(new Runnable() {
        	           public void run() {
        	        	   // send heartbeat to master
        	        	   i++;
        	        	   System.out.println(""+i);
        	        	   heartBeatClientClient.sendCommand("HEART_BEAT");
        	           }
        	       }, 0, RunConfig.heartbeatIntervalInSec, TimeUnit.SECONDS); // execute every 3 seconds
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	public void stopSendingHeartBeats(){ 
		heartBeatClientClient.closeConnection();
		exec.shutdown();
	}
}
