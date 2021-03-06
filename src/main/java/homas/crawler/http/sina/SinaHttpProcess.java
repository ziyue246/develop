package homas.crawler.http.sina;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

//import org.apache.commons.httpclient.HttpClient;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import homas.crawler.http.HtmlInfo;
import homas.crawler.system.SystemCommon;
import homas.crawler.util.EncoderUtil;
import homas.crawler.util.StringUtil;
import net.sf.json.JSONObject;




/**
 * 新浪微博
 */
public class SinaHttpProcess  {
	
	private String loginCookie;
	//private String userAgent = SystemCommon.User_Agent;
	//"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2859.0 Safari/537.36";
	private String userName = "13582688545";//"13582688545";
	private String passWord = "pp9999";//13582688545----pp9999
	private String redirectURL;
	
	
	public String getLoginCookie() {
		return loginCookie;
	}

	public void setLoginCookie(String loginCookie) {
		this.loginCookie = loginCookie;
	}

//	public String getUserAgent() {
//		return userAgent;
//	}
//
//	public void setUserAgent(String userAgent) {
//		this.userAgent = userAgent;
//	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public static String getCookie(){
		return null;
	}	
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	public String getRedirectURL() {
		return redirectURL;
	}
	
	public HttpResponse cookie(HtmlInfo html, String agent) {
		HttpClient hc = HttpClients.createDefault();  
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.addHeader("Content-Type", "application/x-www-form-urlencoded");
		//get.addHeader("User-Agent",userAgent);
		get.addHeader("Referer", "");
		try {
			HttpResponse response = hc.execute(get);
			loginCookie = getCookie(response);
			return response;
		} catch (Exception e) {
			return null;
		} finally {
			get.abort();
		}
	}
	
	private HttpClient httpClient(HtmlInfo html) {
		return HttpClients.createDefault();
	}

	public  String getCookie(HttpResponse response) {
		Header[] hs = response.getHeaders("Set-Cookie");
		StringBuilder sb = new StringBuilder();
		for(Header h : hs) {
			sb.append(h.getValue().split(";")[0] + "; ");
		}
		return sb.toString();
	}
	
	public synchronized boolean login() {

		HtmlInfo html = new HtmlInfo();
		html.setEncode("utf-8");
		html.setType("LOGIN");
		html.setSite("sina");
		
		SinaUserLoginEntity loginEntity = getEntity();
		String postLoginUrl = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.2)";
		String pwdString = loginEntity.getServertime() + "\t"
				+ loginEntity.getNonce() + "\n" + loginEntity.getPassword();
		
		html.setOrignUrl(postLoginUrl);
		String sp = null;
		try {
			sp = rsaCrypt(loginEntity.getPubkey(), "10001", pwdString);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("entry", "weibo"));
		nvps.add(new BasicNameValuePair("gateway", "1"));
		nvps.add(new BasicNameValuePair("from", ""));
		nvps.add(new BasicNameValuePair("savestate", "7"));
		nvps.add(new BasicNameValuePair("useticket", "1"));
		nvps.add(new BasicNameValuePair("pagerefer", ""));
		nvps.add(new BasicNameValuePair("pcid", loginEntity.getPcid()));
		if (loginEntity.getShowpin() != null
				&& loginEntity.getShowpin().equals("1")
				&& loginEntity.getPicNO() != null) {
			nvps.add(new BasicNameValuePair("door", loginEntity.getPicNO()));
		}
		nvps.add(new BasicNameValuePair("vsnf", "1"));
		nvps.add(new BasicNameValuePair("su", loginEntity.getUsernameBase64()));
		nvps.add(new BasicNameValuePair("service", "miniblog"));
		nvps.add(new BasicNameValuePair("servertime", loginEntity.getServertime()));
		nvps.add(new BasicNameValuePair("nonce", loginEntity.getNonce()));
		nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
		nvps.add(new BasicNameValuePair("rsakv", loginEntity.getRsakv()));
		nvps.add(new BasicNameValuePair("sp", sp));
		nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
		nvps.add(new BasicNameValuePair("prelt", "115"));
		nvps.add(new BasicNameValuePair("returntype", "META"));
		nvps.add(new BasicNameValuePair("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
		String entity = null;
		try {
			entity = new String(postRequest(html, nvps, getRedirectURL(), "UserAgent()"), "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//Systemconfig.dbService.updateUserOrder(loginEntity.getUsername());
		//System.out.println(entity);
		if (entity != null && entity.indexOf("retcode=0") > -1) {
			System.out.println(loginEntity.getUsername() + "\tlogin success.\r\n");
			
			String url = StringUtil.regMatcher(entity, "location.replace\\(['\"]", "['\"]").split("url=")[1].replace("%3A", ":").replace("%26", "&").replace("%3D", "=").replace("%2F", "/").replace("%3F", "?");
			html.setOrignUrl(url);
			html.setReferUrl(postLoginUrl);
			cookie(html, "UserAgent");
			html.setCookie(loginCookie);
			//System.out.println("cookie:"+loginCookie);
			//user.setCookie(cookie);
			return true;
		} else {
			System.out.println(loginEntity.getUsername() + "\tlogin fail.");
			return false;
		}
	}
	
	public byte[] postRequest(HtmlInfo html, List<NameValuePair> form_data, String refererURL, String agent){
		HttpPost postMethod = new HttpPost(html.getOrignUrl());
		postMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.addHeader("Host", StringUtil.getHost(html.getOrignUrl()));
		//postMethod.addHeader("User-Agent", userAgent);
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(form_data, html.getEncode()));
			
			
			Header[] header = postMethod.getAllHeaders();
			for (Header h : header) {
				//System.out.println(h.getName()+"\t:\t"+h.getValue());
			}
			HttpEntity he = postMethod.getEntity();
			HttpResponse response = httpClient(html).execute(postMethod);
			byte[] byteArray = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count = -1;
			InputStream responseBodyAsStream;
			byte[] buffer = new byte[1024];
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseBodyAsStream = response.getEntity().getContent();
				Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");  
		        if (contentEncodingHeader != null) {  
		            String contentEncoding = contentEncodingHeader.getValue();  
		            if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {  
		            	responseBodyAsStream = new GZIPInputStream(responseBodyAsStream);  
		            }  
		        }  
		        Header contentType = response.getFirstHeader("Content-Type");  
		        if (contentType != null) {
		            String type = contentType.getValue();  
		            if (type.contains("charset")) {  
		            	String[] temp = type.split("charset=");
		            	if(type.length()>1) {
		            		type = temp[1].trim();
		            		html.setEncode(type);
		            	}
		            }
		        }  
				while ((count = responseBodyAsStream.read(buffer, 0, buffer.length)) > -1) {
					baos.write(buffer, 0, count);
				}
				buffer = null;
				baos.close();
				responseBodyAsStream.close();
				byteArray = baos.toByteArray();
			}
			return byteArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try{
				postMethod.abort();
			}catch(Exception e){}		
		}
		return null;
	}
	public boolean verify() {
		String url = "http://weibo.com/guangxianliuyan?refer_flag=1001030101_&is_hot=1";
		HtmlInfo html = new HtmlInfo();
		html.setCookie(loginCookie);
		html.setSite("sina");
		html.setOrignUrl(url);
		html.setEncode("utf-8");
		html.setType("LOGIN");
		getContent(html);
		String str = html.getContent();
		if(str.indexOf("我的首页") > -1 ||  str.indexOf("我的微博") > -1 
				||  str.indexOf("光线传媒当家女主播") > -1) {
			return true;
		}
		return false;
	}
	
	private void getContent(HtmlInfo html) {
	
		HttpClient client =  HttpClients.createDefault();//new HttpClient();;	
		HttpGet getMethod = new HttpGet(html.getOrignUrl());// new GetMethod(html.getOrignUrl());
		
		//String User_Agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36 QIHU 360SE";
	    //((Object) getMethod.getParams()).setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		
		getMethod.addHeader("Cookie", loginCookie);
		getMethod.addHeader("Accept", "*/*");
		//getMethod.addHeader("Accept-Encoding", "deflate");
		getMethod.addHeader("Connection", "keep-alive");
		//getMethod.addHeader("User-Agent", userAgent);
		//getMethod.addHeader("Host","weibo.com");
		CloseableHttpResponse  response = null;
		try {
			response = (CloseableHttpResponse) client.execute(getMethod);
			InputStream instream = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(instream, "utf-8"));//
			StringBuffer sb = new StringBuffer();
			for (String line=br.readLine(); line!=null; line=br.readLine()) {
				sb.append(line+"\n");
			}
			html.setContent(sb.toString());
			response.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String rsaCrypt(String modeHex, String exponentHex, String messageg)
			throws IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);
		byte[] encryptedContentKey = enc.doFinal(messageg.getBytes("GB2312"));
		return new String(Hex.encodeHex(encryptedContentKey));
	}
	//得到登录属性
	private SinaUserLoginEntity getEntity() {
		SinaUserLoginEntity entity = new SinaUserLoginEntity();
		entity.setUsername(userName);//user.getName()
		entity.setPassword(passWord);
		String usernameBase64 = userName;
		if(usernameBase64.contains("@")) {
			usernameBase64 = usernameBase64.replaceFirst("@", "%40");
		}
		usernameBase64 = new String(EncoderUtil.getBase64Encode(usernameBase64.getBytes()));
		entity.setUsernameBase64(usernameBase64);
		String preloginurl = "http://login.sina.com.cn/sso/prelogin.php?entry=sso&callback=sinaSSOController.preloginCallBack&su="+ usernameBase64
				+ "&checkpin=1&rsakt=mod&client=ssologin.js(v1.4.5)&_=" + (new Date().getTime() / 1000);
		//System.out.println(preloginurl);
		HtmlInfo html = new HtmlInfo();
		html.setOrignUrl(preloginurl);
		html.setType("LOGIN");
		html.setSite("sina");
		html.setEncode("utf-8");
		html.setReferUrl(null);
		getContent(html);
		
		String getResp = html.getContent();
		int firstLeftBracket = getResp.indexOf("(");
		int lastRightBracket = getResp.lastIndexOf(")");
		String firstJson = getResp.substring(firstLeftBracket + 1,lastRightBracket);
		//System.out.println("firstJson:"+firstJson);
		JSONObject jsonInfo = JSONObject.fromObject(firstJson);
		entity.setNonce(jsonInfo.getString("nonce"));
		entity.setPcid(jsonInfo.getString("pcid"));
		entity.setPubkey(jsonInfo.getString("pubkey"));
		entity.setRetcode(jsonInfo.getString("retcode"));
		entity.setShowpin(jsonInfo.getString("showpin"));
		entity.setRsakv(jsonInfo.getString("rsakv"));
		entity.setServertime(jsonInfo.getString("servertime"));
		if((entity.getShowpin() != null && entity.getShowpin().equals("1"))){
			System.err.println("新浪微博登陆需要验证码，从validateImg文件夹查看验证码，将验证码写入相同名字的文本文件并复制到validateImg文件夹！");
			addPicDoor(entity);
		}
		return entity;
	}
	//需要验证码的情况，图片设置为文本读入验证码
	private void addPicDoor(SinaUserLoginEntity entity){
		String door = null;
		String validateImgPath = "validateImg";
		String picUrl = "http://login.sina.com.cn/cgi/pin.php?s=0&p=" + entity.getPcid() + "&r=" +  Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8);
		String picName = entity.getUsername();
		HtmlInfo html = new HtmlInfo();
		html.setOrignUrl(picUrl);
		html.setSite("sina");
		html.setType("LOGIN");
		html.setEncode("utf-8");
		downloadVPic(html, validateImgPath, picName);
		
		
		Scanner sc = new Scanner(System.in);
		door = sc.nextLine();
		door = door.trim();
		//sc.close();
		/*String txt = validateImgPath + File.separator + picName + ".txt";
		while(door == null){
			File file = new File(txt);
			try {
				if(file.exists()){
					door = StringUtil.getContent(txt).replace("\n", "");
					door = door.replace(" ", "");
					door = door.replace("\r", "");
					door = door.replace("\t", "");
				}else{
					Thread.sleep(1000);
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}*/
		entity.setPicNO(door);
	}
	//需要验证码，下载到本地
	private boolean downloadVPic(HtmlInfo html, String picDir,String fileName) {
		HttpGet getMethod = null;
		try {
			String url = EncoderUtil.encodeKeyWords(html.getOrignUrl(), html.getEncode());
			getMethod = new HttpGet(url);
			getMethod.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			//getMethod.addHeader("User-Agent",userAgent);
			fileName = picDir + File.separator + fileName + ".jpg";
			File picFile = new File(fileName);
			HttpResponse response = httpClient(html).execute(getMethod);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				// 得到网络资源的字节
				InputStream in = entity.getContent();
				BufferedImage img = ImageIO.read(in);
				in.close();
				if (img == null ) {
					return false;
				}
				if(!picFile.getParentFile().exists()){
					picFile.getParentFile().mkdirs();
				}
				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(1, 1), null);
				BufferedImage new_img = op.filter(img, null);
				FileOutputStream out = new FileOutputStream(picFile);
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(new_img);
				out.close();
				return true;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(getMethod != null)
				getMethod.abort();
		}
		return false;
	}	
}

