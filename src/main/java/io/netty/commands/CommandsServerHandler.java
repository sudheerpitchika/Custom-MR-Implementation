/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.commands;

import endmodules.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.heartbeats.HeartBeatClient;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandsServerHandler extends SimpleChannelInboundHandler<Command> {

	HeartBeats heartBeatClient=null;
   
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        command.getCommandId();
        String cmdString = command.getCommandString();
        System.out.println("Command Received: "+ cmdString + "\t from "+ctx.channel().remoteAddress());
        ctx.write("OK /"+cmdString);
        
        if(cmdString.equals("SHUTDOWN")){
        	if(heartBeatClient != null)
        		heartBeatClient.stopSendingHeartBeats();
        	
        	// Close the current channel
        	ctx.channel().close();
        	// Then close the parent channel (the one attached to the bind)
        	ctx.channel().parent().close();
        }
        else if(cmdString.equals("START_TASK_TRACKER")){
        	// send heart beats to master repeatedly
        	heartBeatClient = new HeartBeats("127.0.0.1", "9898");
   	     	Thread t = new Thread(heartBeatClient);
   	     	t.start();
        }
        else if(cmdString.equals("START_MAP")){
        	// new thread for map work
        	//at the end of map process send the completion status
        	
        }
        else if(cmdString.equals("START_REDUCE")){
        	// new thread for reduce work        	
        	//at the end of reduce process send the completion status
        	
        }
        else if (cmdString.equals("RETURN_KEYS")){
        	
        }
	}
}


class HeartBeats implements Runnable {
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
        	       }, 1, 1, TimeUnit.SECONDS); // execute every 60 seconds
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	public void stopSendingHeartBeats(){
		heartBeatClientClient.closeConnection();
		exec.shutdown();
	}
}