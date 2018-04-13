package quan.serverThreadEnd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ServerThreadStart implements Runnable{
	private Socket server;
	private List<Socket> servers;
	private int threadId; 

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			OutputStream outServer = server.getOutputStream();
			InputStream inServer = server.getInputStream();
			Scanner in = new Scanner(inServer,"UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(outServer,"UTF-8"),true);
			System.out.println("有一个客户端进行了连接");
			out.println("已连接服务器(quit退出)"+"你的Id为"+threadId);
			this.allSendIn(server);
			while(true) {
				String stringSer = in.nextLine();//会出现异常
				ServerProtocol information = new ServerProtocol(stringSer);
				switch(information.menu()) {
				case 1:
					//群发
					try {
						this.allSendMessage(server,information);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 2:
					//单发：
					this.sendMessage(in, out,information);
					break;
				case 3:
					//文件
					break;
				case 4:
					//退出
					this.allSendOut(server);
					server.close();
					//return;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//单发消息
	private void sendMessage(Scanner in,PrintWriter out,ServerProtocol information) throws UnsupportedEncodingException, IOException {
		int i = information.getId();
		if(i>=servers.size()) {
			out.println("ID不存在");
			return;
		}else {
			Socket s = servers.get(i);
			if(s.isClosed()) {
				out.println("已经退出了！");
				return;
			}else {
				PrintWriter outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
				outS.println(threadId+":"+information.getString());
			}
			return;
		}
	}
	
	//群发消息
	private void allSendMessage(Socket server,ServerProtocol information) throws Exception {
		//String stringSer = in.nextLine();
        for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
        	if(s.isClosed()) {
        		continue;
        	}else {
        		if(s != server) {
    				PrintWriter outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
    				outS.println(threadId+"发送"+information.getString());
    			}
        	}
        }
        return;
    }
	
	//广播进入
	private void allSendIn(Socket server) throws Exception {
        for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
			if(s.isClosed()) {
				continue;
			}else{
				if(s != server) {
					PrintWriter outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
					outS.println(threadId+"加入");
				}
			}
        }
        return;
	}
		
	//广播退出
	private void allSendOut(Socket server) throws Exception {
		for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
	        Socket s = servers.get(i);
			if(s.isClosed()) {
				continue;
			}else {
				if(s != server) {
					PrintWriter outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
					outS.println(threadId+"退出");
				}
			}
        }
		return;
	}
		
	//构造器将servers数组,线程id，server传入线程类
	public ServerThreadStart(Socket server,List servers,int threadId) {
		this.server = server;
		this.servers = servers;
		this.threadId = threadId;
	}
}