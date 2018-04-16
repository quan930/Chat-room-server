package quan.serverThreadEnd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerAnd {
	public static void main(String[] args) throws IOException {
		//日志
		File file =new File("log.txt");
		if(!file.exists()) {
			file.createNewFile();
		}
		PrintStream log = new PrintStream(file);
		ServerSocket serverSocket;
		serverSocket = new ServerSocket(6666);
		log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date())+"\n"+"Server Started!");
		List<Socket>servers = new ArrayList<Socket>();
		List<String>names = new ArrayList<String>();
		int i = 0;
		while(true) {
			Socket server = serverSocket.accept();
			servers.add(server);
			names.add("****");
			log.println("线程"+i);
			Runnable r = new ServerThreadStart(server,servers,i,log,names);
			Thread t = new Thread(r);
			t.start();
			i++;
		}
	}
}
