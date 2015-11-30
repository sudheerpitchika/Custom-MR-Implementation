package userprogram;

import endmodules.WorkerProgram;

public interface Mapper {
	public void map(String chunkId, String inputText, WorkerProgram wp);
}
