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
package io.netty.heartbeats;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {

    // Stateful properties
    private volatile Channel channel;
    private final BlockingQueue<String> answer = new LinkedBlockingQueue<String>();

    public HeartBeatClientHandler() {
        super(false);
    }

    public String sendCommand(int commandId, String commandString){
    	Command.Builder builder = Command.newBuilder();
    	
    	builder.setCommandId(commandId);
    	builder.setCommandString(commandString);

    	ChannelPromise promise = channel.newPromise();
    	promise.addListener(new GenericFutureListener<Future<? super Void>>() {

			public void operationComplete(Future<? super Void> arg0)
					throws Exception {
				// System.out.println("operatoin completed");
			}
		});

    	System.out.println("Sending to Server: "+commandString);
    	ChannelFuture responseFuture = channel.writeAndFlush(builder.build(),promise);

    	
    	// this can be removed for heart beats
    	String returnResult = "OK:Client";
    	
    	if(commandString.equals("SHUTDOWN")){
    		while(true)
    			if(responseFuture.isDone())
    				return returnResult;
    	}
    	    	
    	boolean interrupted = false;    	
        for (;;) {
            try {
            	returnResult = answer.take();
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
	protected void channelRead0(ChannelHandlerContext ctx, String result) throws Exception {
		System.out.println("Received from server: "+result);
		answer.add(result);		
	}
}
