package io.netty.commands;

public class App {

	public static void main(String[] args) throws Exception {
		Slave slave = new Slave();
		Master master = new Master();
		//set in future task
		slave.main(null);
		master.main(null);
	}

}
