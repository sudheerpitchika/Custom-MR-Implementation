package processing;

import io.netty.commands.Slave;

public class StartMapFunction implements Runnable{

	public StartMapFunction(){
		
	}
	
	public void run(){
		Slave.worker.startMapFunction();
		
		// send completion status once done
		// also send keysandlocations to shuffler		
	}
}
