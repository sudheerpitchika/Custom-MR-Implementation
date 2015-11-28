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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
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
        cmdResp.setResponseText("OK /"+cmdString);
        ctx.write(cmdResp.build());
        
        
        if(cmdString.equals("SHUTDOWN")){
        	
        	if(heartBeatClient != null)
        		heartBeatClient.stopSendingHeartBeats();
        
        	// Close the current channel
        	ctx.channel().close();
        	// Then close the parent channel (the one attached to the bind)
        	ctx.channel().parent().close();
        }
        
        else if(cmdString.equals("START_DATA_NODE")){
        	
        }
        
        else if(cmdString.equals("ACCEPT_DATA")){
        	
        }
        
        else if(cmdString.equals("ACCEPT_JAR")){
        	
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
        	
        	StartMapFunction startMap = new StartMapFunction();
        	Thread t = new Thread(startMap);
        	t.start();
        	
        }
        
        else if(cmdString.equals("START_REDUCE")){
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
        	AcceptingKeyAndLocations acceptKeyLocn = new AcceptingKeyAndLocations(ctx, command);
        	Thread t = new Thread(acceptKeyLocn);
        	t.start();
        }
        
        else if(cmdString.equals("RETURN_VALUES_FOR_KEY")){
        	ReturnValueForKey retValForKey = new ReturnValueForKey(ctx, command);
        	Thread t = new Thread(retValForKey);
        	t.start();
        }
        
        // Command to receive at shuffler
        else if(cmdString.equals("ACCEPT_DATA_SHUFFLER")){
        	AcceptDataShuffler shuffleData = new AcceptDataShuffler(ctx, command);
        	Thread t = new Thread(shuffleData);
        	t.start();
        }
	}
}
