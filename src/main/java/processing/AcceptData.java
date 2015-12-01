package processing;

import io.netty.channel.ChannelHandlerContext;
import io.netty.commands.CommandsProtocol.Command;
import io.netty.commands.CommandsProtocol.CommandResponse;
import io.netty.commands.Slave;


public class AcceptData implements Runnable {
	ChannelHandlerContext ctx;
	Command command;
	
	public AcceptData (ChannelHandlerContext ctx, Command command){
		this.command = command;
		this.ctx = ctx;
	}

	public void run() {
		String inputDataString = command.getInputChunk();
		Slave.worker.acceptData(command.getInputChunkId(), inputDataString);
		CommandResponse.Builder cmdResponse = CommandResponse.newBuilder();
		cmdResponse.setForCommandId(command.getCommandId());
		cmdResponse.setForCommandString(command.getCommandString());
		cmdResponse.setResponseText("OK "+command.getCommandString());
		ctx.writeAndFlush(cmdResponse.build());
		System.out.println("*************");
	}
}
