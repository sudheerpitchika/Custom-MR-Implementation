package processing;

import io.netty.commands.Slave;

public class StartReduceFunction implements Runnable{

	public StartReduceFunction(){
		
	}
	
	public void run(){
		Slave.worker.startReduceFunction();
		
		// send completion status once done
	}
}
