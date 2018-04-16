package quan.serverThreadEnd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
//创建流关闭？
//关闭套接字的同时io流关闭？
public class ServerThreadStart implements Runnable{
	private Socket server;
	private List<Socket> servers;
	private int threadId;
	private PrintStream log;
	private List<String>names;
	//线程逻辑
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			OutputStream outServer = server.getOutputStream();
			InputStream inServer = server.getInputStream();
			Scanner in = new Scanner(inServer,"UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(outServer,"UTF-8"),true);
			log.print(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date())+"有一个客户端进行了连接\t\t");
			System.out.print(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date())+"有一个客户端进行了连接\t\t");
			out.println("已连接服务器(quit退出)"+"你的Id为"+threadId);
			this.allSendIn();//广播进入
			log.println("在线人数:"+this.showPerson());
			System.out.println("在线人数:"+this.showPerson());
			boolean isOnline= true;//上下线
			while(isOnline) {
				String stringSer = in.nextLine();
				//判断字符串长度有客户端以后可以省略
				if(stringSer.length()<4) {
					continue;
				}
				ServerProtocol information = new ServerProtocol(stringSer);
				switch(information.menu()) {
				case 1:
					//群发
					try {
						this.allSendMessage(information);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace(log);
						e.printStackTrace();
					}
					break;
				case 2:
					//单发：
					//判断字符串长度有客户端以后可省略
//					if(stringSer.length()<8) {
//						break;
//					}else {
						this.sendMessage(out,information);
						break;
//					}
				case 3:
					//文件
					break;
				case 4:
					//显示在线人数
					out.println("在线人数:"+this.showPerson());
					break;
				case 5:
					//显示当前上线的name
					this.showPersonName(out);
					break;
				case 6:
					//退出
					this.allSendOut();
					server.close();
					in.close();
					out.close();
					isOnline = false;
					log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date())+"\n"+threadId+"退出了连接");
					break;
				case 7:
					//命名
					this.named(information, out);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {//抛出异常
			// TODO Auto-generated catch block
			log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
			e.printStackTrace(log);
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
			e1.printStackTrace(log);
			e1.printStackTrace();
		}
	}
	
	//单发消息不可给本人发消息
//	private void sendMessage(PrintWriter out,ServerProtocol information){
//		int i = information.getId();
//		if(i>=servers.size()) {//判断发送目标是否存在
//			out.println("ID不存在");
//			return;
//		}else {
//			Socket s = servers.get(i);
//			if(s.isClosed()) {//判断套接字是否关闭
//				out.println("已经退出了！");
//				return;
//			}else {
//				if(s == server) {//判断是否是自己
//					out.println("不可以给给自己发消息error!");
//					return;
//				}else {
//					PrintWriter outS;//创建输出流
//					try {
//						outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
//						outS.println(threadId+":"+information.getString());
//						//outS.close();//关闭流
//					} catch (UnsupportedEncodingException e) {//抛出异常
//						// TODO Auto-generated catch block
//						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
//						e.printStackTrace(log);
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
//						e.printStackTrace(log);
//						e.printStackTrace();
//					}
//				}
//			}
//			return;
//		}
//	}
	
	//按名字单发
	private void sendMessage(PrintWriter out,ServerProtocol information){
		String string = information.getString();
		int m = string.indexOf("**");
		String name = string.substring(0, m);
		String newStr = string.substring(m+2);
		int num = 0;
		for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
        	if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
        		continue;
        	}else {
        		if(names.get(i).equals(name)) {
        			num = i;//获得对象ID
        			if(s == server) {//判断是否是自己
    					out.println("不可以给给自己发消息error!");
    					return;
    				}else {
    					PrintWriter outS;//创建输出流
    					try {
    						outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
    						outS.println(names.get(threadId)+":"+newStr);
    						//outS.close();//关闭流
    					} catch (UnsupportedEncodingException e) {//抛出异常
    						// TODO Auto-generated catch block
    						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
    						e.printStackTrace(log);
    						e.printStackTrace();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
    						e.printStackTrace(log);
    						e.printStackTrace();
    					}
    				}
				}else {
					continue;
				}
        	}
        }
	}
	
	//群发消息
	private void allSendMessage(ServerProtocol information){
        for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
        	if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
        		continue;
        	}else {
        		if(s != server) {//判断是否是自己
    				PrintWriter outS;//创建输出流
					try {
						outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
						outS.println(names.get(threadId)+"发送"+information.getString());
						//outS.close();//关闭流
					} catch (UnsupportedEncodingException e) {//抛出异常
						// TODO Auto-generated catch block
						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
						e.printStackTrace(log);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
						e.printStackTrace(log);
						e.printStackTrace();
					}
    			}
        	}
        }
        return;
    }
	
	//广播进入
	private void allSendIn(){
        for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
			if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
				continue;
			}else{
				if(s != server) {//判断是否是自己
					PrintWriter outS;//创建输出流
					try {
						outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
						outS.println(threadId+"加入");
						//outS.close();//关闭流
					} catch (UnsupportedEncodingException e) {//抛出异常
						// TODO Auto-generated catch block
						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
						e.printStackTrace(log);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
						e.printStackTrace(log);
						e.printStackTrace();
					}
				}
			}
        }
        return;
	}
	
	//返回上线人数
	private int showPerson(){
        int sum = 0;
		for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
			if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
				continue;
			}else{
				sum = sum+1;
			}
        }
        return sum;
	}
	
	//显示上线的name
	private void showPersonName(PrintWriter out){
        for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
        	if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
        		continue;
        	}else {
        		out.println("Name:"+names.get(i));
        	}
        }
        return;
    }
	
	//广播退出
	private void allSendOut(){
		for	(int i = 0; i < servers.size(); i++) {// 遍历所有的线程
	        Socket s = servers.get(i);
			if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
				continue;
			}else {
				if(s != server) {//判断是否自己
					PrintWriter outS;//创建输出流
					try {
						outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
						outS.println(names.get(threadId)+"退出");
						//outS.close();//关闭流 ？？？？？？！！！！！！！
					} catch (UnsupportedEncodingException e) {//抛出异常
						// TODO Auto-generated catch block
						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
						e.printStackTrace(log);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.println(new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分ss秒").format(new Date()));
						e.printStackTrace(log);
						e.printStackTrace();
					}
				}
			}
        }
		return;
	}
	
	//命名	
	private void named(ServerProtocol information,PrintWriter out) {
		String name = information.getString();
		for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
        	if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
        		continue;
        	}else {
        		if(names.get(i).equals(name)) {
        			out.println("名字重复");
					return;
				}else {
					continue;
				}
        	}
        }
		names.set(threadId, name);
		return;
	}
	
	//返回该名字的ID
	private int nameID(ServerProtocol information,PrintWriter out) {
		String string = information.getString();
		int m = string.indexOf("**");
		String name = string.substring(0, m);
		String newStr = string.substring(m+2);
		int num = 0;
		for (int i = 0; i < servers.size(); i++) {// 遍历所有的线程
        	Socket s = servers.get(i);
        	if(s.isClosed()) {//判断套接字是否关闭，如关闭结束本次循环
        		continue;
        	}else {
        		if(names.get(i).equals(name)) {
        			num = i;
				}else {
					continue;
				}
        	}
        }
		return num;
	}
	
	//构造器将servers数组,线程id，server传入线程类
	public ServerThreadStart(Socket server,List servers,int threadId,PrintStream log,List names) {
		this.server = server;
		this.servers = servers;
		this.threadId = threadId;
		this.log = log;
		this.names = names;
	}

}