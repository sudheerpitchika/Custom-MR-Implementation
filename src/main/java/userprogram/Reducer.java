package userprogram;

import java.util.ArrayList;

import endmodules.WorkerProgram;

public interface Reducer {
	public void reduce(String key, ArrayList<String> values, WorkerProgram wp);
}
