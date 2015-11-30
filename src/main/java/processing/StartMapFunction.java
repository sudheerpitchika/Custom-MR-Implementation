package processing;

import io.netty.commands.Slave;

public class StartMapFunction implements Runnable{

	public StartMapFunction(){
		
	}
	
	public void run(){
		try {
			Slave.worker.startMapFunction();
			Slave.worker.startCombiner();
			Slave.worker.writeKeyValuesToFileAndCreateTable();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		// send completion status once done
		// also send keysandlocations to shuffler		
	}
}
