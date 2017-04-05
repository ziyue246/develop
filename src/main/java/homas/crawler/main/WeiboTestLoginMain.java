package homas.crawler.main;

import homas.crawler.bean.WeiboLogin;
import homas.crawler.http.HtmlInfo;
import homas.crawler.http.SimpleHttp;
import homas.crawler.http.sina.SinaHttpProcess;
import homas.crawler.system.FileOperation;
import homas.crawler.system.SystemCommon;
import homas.crawler.util.DomTree;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class WeiboTestLoginMain {
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("\\n");
		
//		Scanner sc = new Scanner(System.in);
//		String door = sc.next();
//		door = sc.next();
//		//door = door.replace("\n", "");
//		System.out.print(door);
//		//sc.close();
//		
//		sc = new Scanner(System.in);
//		door = sc.next();
//		door = sc.next();
//		//door = door.replace("\n", "");
//		System.out.print(door);
//		sc.close();
		testLogin();
	}
	public static void test(String[] args) throws Exception {
		SinaHttpProcess sinaHttp = new SinaHttpProcess();
		sinaHttp.setUserName("1354805597rzy@sina.com");
		sinaHttp.setPassWord("649859709");
		HtmlInfo html = new HtmlInfo();
		SimpleHttp http = new SimpleHttp();
		html.setCookie(sinaHttp.getCookie());
		html.setUa("Opera/9.27 (Windows NT 5.2; U; zh-cn)");
		String url = "http://s.weibo.com/weibo/<keyword>?topnav=1&wvr=6&b=1";
		url = url.replace("<keyword>", SystemCommon.urlEncode("男生为什么要比女生高","utf-8"));
		html.setOrignUrl(url);
		http.simpleGet(html);
		
		String content = html.getContent(); 
		
		
		content = DomTree.decodeUnicode(content);
		content = DomTree.weiboSearchJsHtml(content);
		System.out.println(content);
		System.out.println();
		
		Node node = DomTree.getNode(content, "utf-8");
		
		//<div class="feed_content wbcon">
		//<p class="comment_txt" 
		String xpath = "//DIV[@class='feed_content wbcon']//P[@class='comment_txt']";
		NodeList nodeList = DomTree.commonList(xpath, node);
		for (int i = 0; i < nodeList.getLength(); i++) {
			String line = nodeList.item(i).getTextContent().trim();
			System.out.println(i+" : "+line);
		}
		//testLogin();
		//weiboHostMonitor();
	}
	
	
	public static void testLogin() throws Exception {
		
		String []weiboaccounts = FileOperation.read("config/weibo").split("\n");
		ArrayList<WeiboLogin> weibologinList = new ArrayList<WeiboLogin>();
		
		for (String account : weiboaccounts) {
			if(account.startsWith("##"))continue;
			
			WeiboLogin weiboLogin = new WeiboLogin();
			System.out.println(account);
			weiboLogin.setAccount(account.split("##")[0]);
			weiboLogin.setPasswd(account.split("##")[1]);
			weibologinList.add(weiboLogin);
		}
		SinaHttpProcess sinaHttp = new SinaHttpProcess();

		//System.out.println(sinaHttp.getUserAgent());
		for (WeiboLogin weiboLogin : weibologinList) {
			sinaHttp.setUserName(weiboLogin.getAccount());
			sinaHttp.setPassWord(weiboLogin.getPasswd());
			sinaHttp.login();
			sinaHttp.setLoginCookie(sinaHttp.getLoginCookie());	
		}

		String []keywords = FileOperation.read("config/weibo").split("\n");
		
		String []userAgents = FileOperation.read("config/userAgent").split("\n");
		
		while(true){
			for (WeiboLogin weibo : weibologinList) {
				String keyword  = keywords[(int)(Math.random()*(keywords.length-1))];
				HtmlInfo html = new HtmlInfo();
				SimpleHttp http = new SimpleHttp();
				html.setUa(userAgents[(int)(Math.random()*(userAgents.length-1))]);
				html.setCookie(weibo.getCookie());
				String url = "http://s.weibo.com/weibo/<keyword>?topnav=1&wvr=6&b=1";
				url = url.replace("<keyword>", SystemCommon.urlEncode(keyword,"utf-8"));
				html.setOrignUrl(url);
				//SystemCommon.httpAgent="221.204.246.116:3128";
				//html.setAgent(true);
				http.simpleGet(html);
				if(html.getContent().contains("验证码")){
					SystemCommon.printLog(weibo.getAccount()+"##"+weibo.getPasswd()+"  验证码问题");
					//return;
					continue;
				}
				String content = html.getContent(); 
				
				content = DomTree.decodeUnicode(content);
				content = DomTree.weiboSearchJsHtml(content);
				Node node = DomTree.getNode(content, "utf-8");
				
				String xpath = "//DIV[@class='feed_content wbcon']//P[@class='comment_txt']";
				NodeList nodeList = DomTree.commonList(xpath, node);
				if(nodeList.getLength()>3){
					SystemCommon.printLog(weibo.getAccount()+"##"+weibo.getPasswd()+"  正常");
				}else{
					SystemCommon.printLog(weibo.getAccount()+"##"+weibo.getPasswd()+"  出错");
				}
				for (int i = 0; i < nodeList.getLength(); i++) {
					String line = nodeList.item(i).getTextContent().trim();
					SystemCommon.printLog(i+" : "+line);
				}
				SystemCommon.sleeps(30);
			}
		}
	}
}
