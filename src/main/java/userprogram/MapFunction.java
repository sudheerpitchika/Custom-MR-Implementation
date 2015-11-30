package userprogram;

import java.util.regex.Pattern;

import endmodules.WorkerProgram;

public class MapFunction extends WorkerProgram implements Mapper{

	public void map(String chunkId, String inputText, WorkerProgram wp) {
		
		final Pattern WORD_BOUNDARY = Pattern .compile("\\s*\\b\\s*");
		
		for ( String word  : WORD_BOUNDARY.split(inputText)) {
            if (word.isEmpty()) {
               continue;
            }
            wp.emit(word,"1");
         }

	}

}
