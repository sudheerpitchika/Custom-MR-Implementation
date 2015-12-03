package config;

public class RunConfig {
	public static final String masterServerIp = "10.18.192.21"; //"10.38.76.13";
	public static final String shuffleServerIp = "10.18.192.21"; //"10.38.76.13";
	public static final String heartBeatServerIp = "10.18.192.21"; //"10.38.76.13";
	
	public static final String masterServerPort = "8475";
	public static final String slaveServerPort = "8476";
	public static final String shuffleServerPort = "8477";
	public static final String heartBeatServerPort = "8478";
	
	public static final int chunkSize = 4194304;	//4 MB
	public static final int numberOfReducers = 2;
}