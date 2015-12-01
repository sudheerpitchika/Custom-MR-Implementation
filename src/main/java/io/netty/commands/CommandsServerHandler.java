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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.google.protobuf.ByteString;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import processing.AcceptData;
import processing.AcceptingKeyAndLocations;
import processing.StartMapFunction;
import processing.StartReduceFunction;
import responses.AcceptDataShuffler;
import responses.HeartBeats;
import responses.ReturnValueForKey;

public class CommandsServerHandler extends SimpleChannelInboundHandler<Command> {

	HeartBeats heartBeatClient = null;
   
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
        
        String cmdString = command.getCommandString();
        System.out.println("Command Received: "+ cmdString + "\t from "+ctx.channel().remoteAddress());
        
        // Temp response
        CommandResponse.Builder cmdResp = CommandResponse.newBuilder();
        cmdResp.setForCommandId(command.getCommandId());
        cmdResp.setForCommandString(command.getCommandString());
        cmdResp.setResponseText("OK "+command.getCommandString());
        
        
        if(cmdString.equals("CONNECT")){
        	
        	Master.numberOfClients++;
        	Master.connectedClients.add(ctx);
        	
        	Master.availableClients.add(ctx);
//        	System.out.println("New client connected: "+Master.numberOfClients+"\tSize: "+Master.connectedClients.size());
        	File jInFile = new File("MF.jar");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(jInFile));
            
            byte[] buffer = new byte[(int)jInFile.length()];
            bis.read(buffer);
            
        	cmdResp.setJarData(ByteString.copyFrom(buffer));
            ctx.write(cmdResp.build());
        }
       
        else if(cmdString.equals("MAP_COMPLETE")){
        	ctx.write(cmdResp.build());
        	
        	Master.completedMapsCount++;
        	Master.availableClients.add(ctx);
        	if(Master.jobTracker.getNumberOfMappers() == Master.completedMapsCount){
        		Master.shuffler.sendKeysAndLocationsToReducers();
        		System.out.println("Last mapper completed");
        	}
        	
        }
        
        
        if(cmdString.equals("SHUTDOWN")){
        	
        	ctx.write(cmdResp.build());
        	Master.numberOfClients--;
        	Master.connectedClients.remove(ctx);
        	
        	if(heartBeatClient != null)
        		heartBeatClient.stopSendingHeartBeats();
        
        	// Close the current channel
        	ctx.channel().close();
        	// Then close the parent channel (the one attached to the bind)
        	ctx.channel().parent().close();
        }
        
        else if(cmdString.equals("START_DATA_NODE")){
        	ctx.write(cmdResp.build());
        }
        
        else if(cmdString.equals("ACCEPT_DATA")){
        	
        	AcceptData acceptData = new AcceptData(ctx, command);
        	Thread t = new Thread(acceptData);
        	t.start();
        	
        }
        
/*        else if(cmdString.equals("ACCEPT_JAR")){
        	ctx.write(cmdResp.build());f
        }
*/        
        
        else if(cmdString.equals("START_TASK_TRACKER")){
        	// send heart beats to master repeatedly
        	ctx.write(cmdResp.build());
        	heartBeatClient = new HeartBeats("127.0.0.1", "9898");
   	     	Thread t = new Thread(heartBeatClient);
   	     	t.start();
   	     	
        }
        
        else if(cmdString.equals("START_MAP")){
        	// new thread for map work
        	//at the end of map process send the completion status
        	ctx.write(cmdResp.build());
        	StartMapFunction startMap = new StartMapFunction();
        	Thread t = new Thread(startMap);
        	t.start();
        	
        }
        
        else if(cmdString.equals("START_REDUCE")){
        	ctx.write(cmdResp.build());
        	//at the end of reduce process send the completion status
        	StartReduceFunction startRed = new StartReduceFunction();
        	Thread t = new Thread(startRed);
        	t.start();
        }

        // IMPLEMENTING THE BELOW FUNCTION IN THE OTHER WAY - Send data to Shuffle &  ACCEPT_DATA_SHUFFLER
        
/*        else if (cmdString.equals("RETURN_KEYS_AND_LOCATIONS")){
        	CommandResponse.Builder cmdResponse = CommandResponse.newBuilder();
        	ReturnKeysAndLocations retKeyLoc = new ReturnKeysAndLocations(ctx, cmdResponse);
        	Thread t = new Thread(retKeyLoc);
        	t.start();
        }*/

        else if(cmdString.equals("ACCEPT_KEYS_AND_LOCATIONS")){
        	ctx.write(cmdResp.build());
        	AcceptingKeyAndLocations acceptKeyLocn = new AcceptingKeyAndLocations(ctx, command);
        	Thread t = new Thread(acceptKeyLocn);
        	t.start();
        }
        
        else if(cmdString.equals("RETURN_VALUES_FOR_KEY")){
        	ctx.write(cmdResp.build());
        	ReturnValueForKey retValForKey = new ReturnValueForKey(ctx, command);
        	Thread t = new Thread(retValForKey);
        	t.start();
        }
        
        // Command to receive at shuffler
        else if(cmdString.equals("ACCEPT_DATA_SHUFFLER")){
        	ctx.write(cmdResp.build());
        	AcceptDataShuffler shuffleData = new AcceptDataShuffler(ctx, command);
        	Thread t = new Thread(shuffleData);
        	t.start();
        }
        
	}
}
