package config;

public class RunConfig {
	
	public static String masterServerIp = "192.168.1.16"; //"10.18.192.21"; //"10.38.76.13";
	public static String shuffleServerIp = "192.168.1.16"; //"10.18.192.21"; //"10.38.76.13";
	public static String heartBeatServerIp = "192.168.1.16"; //"10.18.192.21"; //"10.38.76.13";
	
	public static final String masterServerPort = "8475";
	public static final String slaveServerPort = "8476";
	public static final String shuffleServerPort = "8477";
	public static final String heartBeatServerPort = "8478";
	
	public static final int chunkSize = 1*1024*1024;// 103116800; //4194304;	//4 MB
	public static final int numberOfReducers = 4;
	
	public static final String jarFileDirectory = "jar";
	public static final String inputFilesDirectory = "input";
	public static final String tempFilesDirectory = "temp";
	public static final String outputFilesDirectory = "output";
	
	public static final int heartbeatIntervalInSec = 10;

	
}
