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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.commands.CommandsProtocol.Command;

public class HeartBeatServerHandler extends SimpleChannelInboundHandler<Command> {
   
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
		//replace Command with heartbeat proto

        String cmdString = command.getCommandString();
        
        //it only receives HEART_BEAT
        System.out.println(cmdString + "\t from "+ctx.channel().remoteAddress());
        // ctx.writeAndFlush("OK /"+cmdString);
        
        if(cmdString.equals("HEARTBEAT_SHUTDOWN")){
        	// Close the current channel
        	ctx.channel().close();
        	// Then close the parent channel (the one attached to the bind)
        	ctx.channel().parent().close();
        }
	}
}

