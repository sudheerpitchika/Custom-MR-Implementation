package endmodules;

import java.util.Map;

public class NameNode {
	Map<String, Split> splitMeta;
	
	
	public void splitData(){
		// split data and fills splitMeta hash map
	}
	
	public void transferData(){
		//transfer data to workers
	}
	
	public void transferJars(){
		//transfer jars to all clients
	}
}

class Split{
	String ip;
	int start, end, id;
}