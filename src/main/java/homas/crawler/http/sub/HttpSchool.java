package homas.crawler.http.sub;

import homas.crawler.http.HtmlInfo;
import homas.crawler.system.SystemCommon;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rzy 2016/12/19
 *
 */
public class HttpSchool {




	private HashMap<String, String> cookieMap = new HashMap<String, String>();

	private String patentCode;
	private String validCode;

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}

	public HashMap<String, String> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(HashMap<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}

	public String getPatentCode() {
		return patentCode;
	}

	public void setPatentCode(String patentCode) {
		this.patentCode = patentCode;
	}

	public String updateCookieMap(Header[] heads) {
		for (Header head : heads) {
			String lineCookie = head.getValue().split(";")[0];
			String cookieKey = lineCookie.split("=")[0].trim();
			String cookieValue = lineCookie.replace(cookieKey, "").trim();
			if (!cookieValue.equals("=")) {
				cookieMap.put(cookieKey, cookieValue);
			}
		}
		String cookie = "";
		for (String key : cookieMap.keySet()) {
			if (cookie == "") {
				cookie = key + cookieMap.get(key);
			} else {
				cookie += "; " + key + cookieMap.get(key);
			}
		}
		return cookie;
	}

	public void getCookie(HtmlInfo html) {
		HttpClient client = HttpClients.createDefault();
		// html.setOrignUrl(loginUrl);

		// html.setOrignUrl("http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.setHeader("User-Agent", html.getUa());
		get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		get.setHeader("Host", "acad.cnki.net");
		get.setHeader("Proxy-Connection", "keep-alive");
		get.setHeader("Upgrade-Insecure-Requests", "1");

		HttpResponse response = null;
		try {
			response = client.execute(get);
			SystemCommon.printLog("获取Cookie get status:" + response.getStatusLine());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (response.containsHeader("Set-Cookie")) {
			html.setCookie(updateCookieMap(response.getHeaders("Set-Cookie")));
		}
		if (response.containsHeader("Location")) {
			try {
				String locationUrl = response.getFirstHeader("Location").getValue();
				html.setOrignUrl(locationUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void simpleGet(HtmlInfo html) {
	
		//System.out.println(html.getOrignUrl());
		
		html.setContent(null);
		CloseableHttpClient client = HttpClients.createDefault();
		
		
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		html.setContent(null);
		get.setHeader("User-Agent", html.getUa());
		get.setHeader("Connection", "keep-alive");
		if (html.getCookie() != null) {
			get.setHeader("Cookie", html.getCookie());
		}
		if (html.getReferUrl() != null) {
			get.setHeader("Referer", html.getReferUrl());
		}
		html.setOrignUrl(html.getOrignUrl());
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);
			if (response.containsHeader("Set-Cookie")) {
				html.setCookie(updateCookieMap(response.getHeaders("Set-Cookie")));
			}
			if (response.containsHeader("Location")) {
				String realUrl = response.getFirstHeader("Location").getValue();
				html.setRealUrl(realUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (response != null && response.getStatusLine().getStatusCode() == 200) {
			HttpEntity httpEntity = response.getEntity();
			String result = null;
			try {
				result = EntityUtils.toString(httpEntity, html.getEncode());
			} catch (IOException e) {
				e.printStackTrace();
			}
			html.setContent(result);
		}
		try {
			response.close();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public int downGet(HtmlInfo html, String filename) {
		//System.out.println(html.getOrignUrl());
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.setHeader("User-Agent", html.getUa());
		get.setHeader("Connection", "keep-alive");
		if (html.getCookie() != null) {
			get.setHeader("Cookie", html.getCookie());
		}
		if (html.getReferUrl() != null) {
			get.setHeader("Referer", html.getReferUrl());
		}
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);
			if (response.containsHeader("Set-Cookie")) {
				html.setCookie(updateCookieMap(response.getHeaders("Set-Cookie")));
			}
			if (response.containsHeader("Location")) {
				String realUrl = response.getFirstHeader("Location").getValue();
				html.setRealUrl(realUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(response.getStatusLine().getStatusCode()!=200)
			SystemCommon.printLog(response.getStatusLine().toString());
		
		if (response != null && response.getStatusLine().getStatusCode() == 200) 
		{
			HttpEntity httpEntity = response.getEntity();

			InputStream inputStream=null;
			OutputStream outputStream=null;
			try {
				inputStream = httpEntity.getContent();
				outputStream = new FileOutputStream(filename);
				int byteCount = 0;
				byte[] bytes = new byte[1024];
				while ((byteCount = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, byteCount);
				}
				return 0;
			} catch (UnsupportedOperationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					if(inputStream!=null)
					inputStream.close();
					if(outputStream!=null)
					outputStream.close();
					response.close();
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				response.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	
	
	
	public void httpUrlConnectiondown(HtmlInfo html, String filename) {
		//System.out.println(html.getOrignUrl());
		
		html.setContent(null);
		try {
			String pathUrl = html.getOrignUrl();
			// 建立连接
			URL url = new URL(pathUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			// //设置连接属性
			httpConn.setDoOutput(true);// 使用 URL 连接进行输出
			httpConn.setDoInput(true);// 使用 URL 连接进行输入
			httpConn.setUseCaches(true);// 忽略缓存
			httpConn.setRequestMethod("GET");// 设置URL请求方法
			httpConn.setRequestProperty("User-Agent", html.getUa());
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpConn.setRequestProperty("Charset", html.getEncode());
			httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			

			if (html.getCookie() != null)
				httpConn.setRequestProperty("Cookie", html.getCookie());
			if (html.getReferUrl() != null)
				httpConn.setRequestProperty("Referer", html.getReferUrl());

		
			// 获得响应状态
			int responseCode = httpConn.getResponseCode();

			if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
															// 当正确响应时处理数据
				String readLine;
				BufferedReader responseReader;
				OutputStream outputStream = new FileOutputStream(filename);
				// 处理响应流，必须与服务器响应流输出的编码一致
				responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), html.getEncode()));
				while ((readLine = responseReader.readLine()) != null) {
					outputStream.write(readLine.getBytes());
					
				}
				outputStream.close();
				responseReader.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	POST http://schools.nyc.gov/schoolsearch/services/SchoolRpc.ashx?rpc HTTP/1.1
//	Host: schools.nyc.gov
//	Connection: keep-alive
//	Content-Length: 188
//	Origin: http://schools.nyc.gov
//	X-Requested-With: XMLHttpRequest
//	User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36
//	Content-Type: application/json; charset=UTF-8
//	Accept: */*
//Referer: http://schools.nyc.gov/schoolsearch/
//Accept-Encoding: gzip, deflate
//Accept-Language: zh-CN,zh;q=0.8
//Cookie: ASP.NET_SessionId=nteqhqzejjxfs0oxa1ounbz2; __utmt=1; _hjIncludedInSample=1; __utma=150944820.652943394.1490864258.1490864258.1490864258.1; __utmb=150944820.2.10.1490864258; __utmc=150944820; __utmz=150944820.1490864258.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)
//
//{"id":0,"method":"schoolSearch","params":{"mode":"search","action":"search","search":"New York Times","borough":"","grade":"","filters":null,"sort":0,"start":0,"count":10},"jsonrpc":"2.0"}




	public void httpUrlConnection(HtmlInfo html, ArrayList<String> params) {

		//System.out.println(html.getOrignUrl());
		
		html.setContent(null);
		try {
			String pathUrl = html.getOrignUrl();
			// 建立连接
			URL url = new URL(pathUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			// //设置连接属性
			httpConn.setDoOutput(true);// 使用 URL 连接进行输出
			httpConn.setDoInput(true);// 使用 URL 连接进行输入
			httpConn.setUseCaches(true);// 忽略缓存
			httpConn.setRequestMethod("POST");// 设置URL请求方法
			httpConn.setRequestProperty("User-Agent", html.getUa());
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpConn.setRequestProperty("Accept", "*/*");
			httpConn.setRequestProperty("Charset", html.getEncode());
			if (html.getCookie() != null)
				httpConn.setRequestProperty("Cookie", html.getCookie());
			if (html.getReferUrl() != null)
				httpConn.setRequestProperty("Referer", html.getReferUrl());

			StringBuffer form = new StringBuffer();
			for (String line : params) {
				String[] items = line.split("##");
				if (form.length() > 0) {
					form.append("&");
				}
				if (items.length == 1) {
					form.append(items[0]).append("=");
				} else if (items.length == 2) {
					form.append(items[0]).append("=").
					append(URLEncoder.encode(items[1], html.getEncode()));
				}
			}
//			if(mark.equals("pdf")) {
//				form.delete(0, form.length());
//				form.append("PatentNo=200480007192.3&Name=%25e8%25bd%25ae%25e8%2583%258e%25e7%2594%25a8%25e4%25bc%25a0%25e6%2584%259f%25e5%2599%25a8%25e8%25a3%2585%25e7%25bd%25ae&PatentType=fmzl&PageNumFM=13&UrlFM=%2Ffm%2F2006%2F2216%2F200480007192%2F000001.tif&PageNumSD=14&UrlSD=%2FSD%2F2008%2F20081203%2F200480007192.3%2F000001.tif&PublicationDate=20060419&ReadyType=&FulltextType=sqgk&Common=1");		
//			}

			byte[] bypes = form != null ? form.toString().getBytes() : "".getBytes();
			httpConn.setRequestProperty("Content-length", "" + bypes.length);
			// 建立输出流，并写入数据
			OutputStream outputStream = httpConn.getOutputStream();
			outputStream.write(bypes);
			outputStream.close();
			// 获得响应状态
			int responseCode = httpConn.getResponseCode();

			if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
															// 当正确响应时处理数据
				StringBuffer sb = new StringBuffer();
				String readLine;
				BufferedReader responseReader;
				// 处理响应流，必须与服务器响应流输出的编码一致
				responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), html.getEncode()));
				while ((readLine = responseReader.readLine()) != null) {

					sb.append(readLine).append("\n");
				}
				responseReader.close();
				html.setContent(sb.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void doPost(HtmlInfo html, JSONObject json){
		String url = html.getOrignUrl();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");//发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = client.execute(post);
			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(res.getEntity());// 返回json格式：
				//response = JSONObject.fromObject(result);
				html.setContent(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//return result;
	}


}
