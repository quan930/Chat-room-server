package quan.serverThreadEnd;

public class ServerProtocol {
	private String string;
	//字符串前四位为功能键
	public int menu() {
		int i = 0;
		String s = string.substring(0,4);
		switch(s) {
		case "*al*":
			i = 1;//群发menu为1
			break;
		case "*11*":
			i = 2;//单发
			break;
		case "*ff*":
			i = 3;//文件
			break;
		case "*ou*":
			i = 4;
			break;//退出
		default:
			return 0;
		}
		return i;
	}
	
	//返回消息内容字符串
	public String getString() {
		String s = string.substring(8);
		return s;
	}
	
	//返回id
	public int getId() {
		String s = string.substring(4,8);
		return Integer.parseInt(s);
	}
	
	public ServerProtocol(String string) {
		this.string = string;
	}
}
