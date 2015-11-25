package userprogram;

import java.util.ArrayList;

public interface Reducer {
	public void reduce(String key, ArrayList<String> values);
}
