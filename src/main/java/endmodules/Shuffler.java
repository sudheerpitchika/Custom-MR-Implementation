package endmodules;

import java.util.ArrayList;
import java.util.Map;

public class Shuffler {
	Map<String, ArrayList<String>> keyIpMap; //table of keys and list of machines who has that key
	Map<String, Split> keysToReducerSplit; //table of ip, start,end values in the above map (howmany keys assigned to each reducer)
	
	public void getKeys(){
		//get all keys from each mapper
	}
	
	public void shuffle(){
		//call get keys, sort the keys, fill keyToReducerSplit table
	}
	
	public void sendKeyIpSetsToReducer(){
		//sends list<key, list<ip>> to reducer based on values from keyToReducerSplit table
	}
}
