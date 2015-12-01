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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.protobuf.ByteString;

public class CommandsClientHandler extends SimpleChannelInboundHandler<CommandResponse> {

    // Stateful properties
    private volatile Channel channel;
    private final BlockingQueue<CommandResponse> responseFromServer = new LinkedBlockingQueue<CommandResponse>();

    public CommandsClientHandler() {
        super(false);
    }

    
    public String sendCommand(Command command){

    	ChannelPromise promise = channel.newPromise();
    	promise.addListener(new GenericFutureListener<Future<? super Void>>() {

			public void operationComplete(Future<? super Void> arg0)
					throws Exception {
				//System.out.println("operatoin completed");
			}
		});

    	ChannelFuture responseFuture = channel.writeAndFlush(command,promise);
    	
    	String returnResult = "OK:Client";
    	
    	if(command.getCommandString().equals("SHUTDOWN")){
    		while(true)
    			if(responseFuture.isDone())
    				return returnResult;
    	}
    	    	
    	boolean interrupted = false;    	
        for (;;) {
            try {
            	returnResult = responseFromServer.take().getResponseText();
                break;
            } catch (InterruptedException ignore) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return returnResult;
    }
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CommandResponse result) throws Exception {
		
		if(result.getForCommandString().equals("CONNECT")){
			
			ByteString bs = result.getJarData();
			byte[] buffer = bs.toByteArray();
			File jOutFile = new File("MFReceived.jar");
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jOutFile));
	        bos.write(buffer, 0, buffer.length);
	        bos.flush();
	        bos.close();
	        System.out.println("Creating Jar" +buffer.length);
		}
		
		System.out.println("got from server: "+result.getResponseText());
		responseFromServer.add(result);
	}
}
