package endmodules;

public class LocationMeta{
	
	private int start, length, chunkId;
	private String ip;
	
	public LocationMeta(int start, int length, int chunkId){
		this.start = start;
		this.length = length;
		this.chunkId = chunkId;
	}
	
	public LocationMeta(int start, int length, int chunkId, String ip){
		this.start = start;
		this.length = length;
		this.chunkId = chunkId;
		this.ip = ip;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getChunkId() {
		return chunkId;
	}

	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
		
}