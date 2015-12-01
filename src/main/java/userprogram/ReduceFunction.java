package userprogram;

import java.io.IOException;
import java.util.ArrayList;

import endmodules.WorkerProgram;

public class ReduceFunction implements Reducer {

	public void reduce(String key, ArrayList<String> values, WorkerProgram wp) {
		int count = 0;
		
		for(String val : values){
			count += Integer.parseInt(val);
		}
		
		try {
			wp.write(key, ""+count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
