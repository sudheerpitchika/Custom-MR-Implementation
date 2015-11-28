package processing;

import io.netty.commands.Slave;

public class StartReduceFunction implements Runnable{

	public StartReduceFunction(){
		
	}
	
	public void run(){
		try {
			Slave.worker.startReduceFunction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send completion status once done
	}
}
