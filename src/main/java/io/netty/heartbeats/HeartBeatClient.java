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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Sends a list of continent/city pairs to a {@link WorldClockServer} to
 * get the local times of the specified cities.
 */
public class HeartBeatClient {

    boolean SSL = System.getProperty("ssl") != null;
    static String HOST = System.getProperty("host", "127.0.0.1");
    static int PORT = Integer.parseInt(System.getProperty("port", "9898"));
    HeartBeatClientHandler handler;
    Channel ch;
    EventLoopGroup group;
    
    public HeartBeatClient(String ip, String port){
    	SSL = System.getProperty("ssl") != null;
    	HOST = System.getProperty("host", ip);
    	PORT = Integer.parseInt(System.getProperty("port", port));
    }
    
    public void startConnection() throws Exception{
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new HeartBeatClientInitializer(sslCtx));

            // Make a new connection.
            ch = b.connect(HOST, PORT).sync().channel();

            // Get the handler instance to initiate the request.
            handler = ch.pipeline().get(HeartBeatClientHandler.class);

        } finally {
           
        }
    }
    
    /*public void sendCommand(Command command){
        String result = handler.sendCommand(command);
//        System.out.println("Returned: "+result);
    }*/
    
    public void sendCommandAsync(Command command){
    	handler.sendCommandAsync(command);
    }
    
    public void closeConnection(){
    	try{
    		// Close the connection.
    		ch.close();
    	}finally{
    		 group.shutdownGracefully();
    	}
    }
}
