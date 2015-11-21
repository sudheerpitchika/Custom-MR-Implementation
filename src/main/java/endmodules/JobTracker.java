package endmodules;

import java.net.Socket;
import java.util.Map;

public class JobTracker {
	Map<String, Socket> listOfNodes;
	Map<String, Boolean> statusOfNodes;
	Shuffler shuffler;
	
	public void autodiscovery(){
		// find the list of connected nodes
	}
	
	public void startWork(){
		// start the map process - issue commands to worker machines
		// start one thread each to listen from workers - thread listen till it completes the work

		// for each client, send start work command
		
		// 1. start Map - command to client, once all the maps are done then
		// 2. start Shuffle - (create instance of Shuffle and start work) once keys are distributed to reducers, then
		// 3. start Reduce - command to client
	}
}
