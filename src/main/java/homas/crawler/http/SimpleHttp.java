package homas.crawler.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import homas.crawler.system.SystemCommon;

/**
 *
 * @author rzy 2016/12/19
 *
 */
public class SimpleHttp {

	private HashMap<String, String> cookieMap = new HashMap<String, String>();
	
	public String updateCookieMap(Header[] heads) {
		for (Header head : heads) {
			String lineCookie = head.getValue().split(";")[0];
			String cookieKey = lineCookie.split("=")[0].trim();
			String cookieValue = lineCookie.replace(cookieKey, "").trim();	
			if(!cookieValue.equals("=")){
				cookieMap.put(cookieKey, cookieValue);
			}
		}
		String cookie = "";
		for (String key : cookieMap.keySet()) {
			if(cookie==""){
				cookie = key + cookieMap.get(key);
			}else{
				cookie += "; "+key + cookieMap.get(key);
			}
		}
		return cookie+"; kc_cnki_net_uid=c8bf8056-57a5-248b-35a1-946320f8a00e";
	}

	public void getCookie(HtmlInfo html) {
		HttpClient client = HttpClients.createDefault();
		//html.setOrignUrl(loginUrl);
		
		//html.setOrignUrl("http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
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
			//SystemCommon.printHeaders(response.getAllHeaders());
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
		
		HttpClient client = HttpClients.createDefault();

		HttpGet get = new HttpGet(html.getOrignUrl());
		get.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		html.setContent(null);
		get.setHeader("User-Agent", html.getUa());
		get.setHeader("Connection", "keep-alive");
		if(html.getCookie()!=null){
			get.setHeader("Cookie", html.getCookie());
		}
		if(html.getReferUrl()!=null){
			get.setHeader("Referer",html.getReferUrl());
		}
		if(html.getAgent()){
        	
        	String hostname = SystemCommon.httpAgent.split(":")[0];
        	int port = Integer.parseInt(SystemCommon.httpAgent.split(":")[1]);
        	HttpHost proxy = new HttpHost(hostname, port);
        	client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
      
        }
		html.setOrignUrl(html.getOrignUrl());
		HttpResponse response = null;
		try {
			
			response = client.execute(get);
			
			//SystemCommon.printLog("获取Cookie get status:" + response.getStatusLine());
			if (response.containsHeader("Set-Cookie")) {
				html.setCookie(updateCookieMap(response.getHeaders("Set-Cookie")));
			}
			if (response.containsHeader("Location")){
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
				result = EntityUtils.toString(httpEntity,html.getEncode());
			} catch (IOException e) {
				e.printStackTrace();
			}
			html.setContent(result);
		}
	}
	public String simplePost(HtmlInfo html, String mark) {
        HttpClient client = HttpClients.createDefault();

        HttpPost post = new HttpPost(html.getRealUrl());
        
        ArrayList<String> params = new ArrayList<>();
        if (mark.equals("verify")) {
            post.setHeader("Referer",html.getReferUrl());

  
            SystemCommon.printLog(html.getCookie());
        } 

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (String line : params) {
        	String key = line.split("##")[0];
        	String value = line.split("##")[1];
        	list.add(new BasicNameValuePair(key, value));
		}
        if(html.getUa()!=null)
        	post.setHeader("User-Agent", html.getUa());
        if(html.getCookie()!=null)
        	post.setHeader("Cookie", html.getCookie());
        if(html.getReferUrl()!=null)
        	post.setHeader("Referer", html.getReferUrl());
        
        try {
            post.setEntity(new UrlEncodedFormEntity(list));
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = client.execute(post);
            SystemCommon.printLog("Post status : "+response.getStatusLine());

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            post.abort();
        }
        String geturl = null;
        try {
            geturl = response.getFirstHeader("Location").getValue();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (response.containsHeader("Set-Cookie")) {
            String cookie = "";
            for (Header co : response.getHeaders("Set-Cookie")) {
                cookie += co.getValue().split(";")[0] + ";";
            }
            
            html.setCookie(cookie);
        }
        if (geturl == null)
            return null;
        html.setRealUrl(geturl);

        return geturl;
    }

}
