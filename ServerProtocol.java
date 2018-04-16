package quan.serverThreadEnd;

import java.util.StringJoiner;

public class ServerProtocol {
	private String string;
	//字符串前四位为指令字符串
	public int menu() {
		int i = 0;
		String s = string.substring(0,4);
		switch(s) {
		case "*al*"://群发menu为1
			i = 1;
			break;
		case "*11*"://单发为2+名字+"**"+消息
			i = 2;
			break;
		case "*ff*"://文件为3
			i = 3;
			break;
		case "*sp*"://显示当在线人数为4
			i = 4;
			break;
		case "*sn*"://显示当前上线的id为5
			i = 5;
			break;
		case "*ou*"://退出为6
			i = 6;
			break;
		case "*na*"://起名字
			i = 7;
			break;
		default:
			return 0;
		}
		return i;
	}
	
	//返回消息内容字符串
	public String getString() {
		String s = string.substring(4);
		return s;
	}
	
//	//返回id
//	public int getId() {
//		String s = string.substring(4,8);
//		return Integer.parseInt(s);
//	}
	
	public ServerProtocol(String string) {
		String str = string.trim();//减去字符串前后空格
		this.string = str;
	}

}
