package quan.serverThreadEnd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerAnd {
	public static void main(String[] args) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(8889);
			System.out.println("Server Started!");
			List<Socket>servers = new ArrayList<Socket>();
			int i = 0;
			while(true) {
				Socket server = serverSocket.accept();
				servers.add(server);
				System.out.println("线程"+i);
				Runnable r = new ServerThreadStart(server,servers,i);
				Thread t = new Thread(r);
				t.start();
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
