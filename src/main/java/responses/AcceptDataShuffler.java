package responses;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.KeyLocation;
import io.netty.commands.CommandsProtocol.Location;
import io.netty.commands.Master;

import java.util.List;

public class AcceptDataShuffler implements Runnable{
	
	ChannelHandlerContext ctx;
	Command command;
	
	public AcceptDataShuffler(ChannelHandlerContext ctx, Command command){
		this.ctx = ctx;
		this.command = command;
	}
	
	public void run(){
		List<KeyLocation> keyAndLocationList =  command.getKeyLocationsMapList();
		
		for(KeyLocation kl : keyAndLocationList){
			String key = kl.getKey();
			Location location = kl.getLocation();
			// Slave.shuffler.receiveKeyAndLocationFromMapper(key, location);
			Master.shuffler.receiveKeyAndLocationFromMapper(key, location);
		}
	}	
}
