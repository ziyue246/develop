package homas.crawler.extractor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import homas.crawler.bean.IEEEinfo;
import homas.crawler.http.HtmlInfo;
import homas.crawler.http.sub.Httpieee;
import homas.crawler.system.SystemCommon;
import homas.crawler.util.MD5Util;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class IeeeExtract{

	
	public void getAbstractPageContent(HtmlInfo html, Httpieee http, IEEEinfo ieeeInfo) {
		String url = ieeeInfo.getUrl();
		html.setRealUrl(url);
		http.simpleGet(html);
		String content = html.getContent();
		content = extractMetadata(content);
		content = content.split("metadata=")[1].replace(";", "");
		JSONObject obj = (JSONObject) JSONObject.fromObject(content);
		parserKeyword(obj, ieeeInfo);
		parserPubtime(obj, ieeeInfo);
	}

	/**
	 * pubtime
	 * 
	 * @param html
	 * @param http
	 * @param ieeeInfo
	 */
	public void parserReferNum(HtmlInfo html, Httpieee http, IEEEinfo ieeeInfo) {
		// http://ieeexplore.ieee.org/rest/document/5392890/references
		String url = "http://ieeexplore.ieee.org/rest/document" + ieeeInfo.getUrl().split("document")[1] + "references";
		html.setRealUrl(url);
		http.simpleGet(html);
		String content = html.getContent();
		JSONObject obj = (JSONObject) JSONObject.fromObject(content);
		if (obj.containsKey("references")) {
			int referNum = obj.getJSONArray("references").size();
			ieeeInfo.setReferNum(referNum);
		}

	}

	public void parserKeyword(JSONObject obj, IEEEinfo ieeeInfo) {
		if (!obj.containsKey("keywords")) {
			SystemCommon.printLog("not contain keyword");
			return;
		}
		JSONObject kobj = (JSONObject) obj.getJSONArray("keywords").get(0);
		JSONArray jarray = kobj.getJSONArray("kwd");
		for (Object object : jarray) {
			if (ieeeInfo.getKeywords() == null) {
				ieeeInfo.setKeywords((String) object);
			} else {
				ieeeInfo.setKeywords(ieeeInfo.getKeywords() + ";" + (String) object);
			}
		}
	}

	public void parserPubtime(JSONObject obj, IEEEinfo ieeeInfo) {
		String time = null;
		if (obj.containsKey("conferenceDate")) {
			time = obj.getString("conferenceDate");
			if (time.contains("\",\"")) {
				time = obj.getJSONArray("conferenceDate").get(0).toString();
			}
		} else if (obj.containsKey("chronOrPublicationDate")) {
			time = obj.getString("chronOrPublicationDate");

		} else if (obj.containsKey("chronDate")) {
			time = obj.getString("chronDate");

		}
		if (time != null) {
			ieeeInfo.setPubtime(pubDateFormat(time));
		}
	}

	public String extractMetadata(String content) {
		// window.recordKeysMapping['3'] = '485337821';
		String pattern = "global\\.document.metadata=.+;";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			sb.append(m.group());
		}
		return sb.toString();
	}

	public void JsonParserOnePageList(String content, List<IEEEinfo> list) {
		if (!JSONObject.fromObject(content).containsKey("records"))
			return;
		JSONArray jarray = JSONObject.fromObject(content).getJSONArray("records");

		for (Object obj : jarray) {
			JSONObject jobj = (JSONObject) obj;
			if (!jobj.containsKey("authors")) {
				continue;
			}
			IEEEinfo ieee = new IEEEinfo();
			list.add(ieee);
			parserAuthor(jobj, ieee);
			parserBrief(jobj, ieee);
			parserCategory(jobj, ieee);
			parserUrl(jobj, ieee);
			parserCiteNum(jobj, ieee);
			parserDoi(jobj, ieee);
			parserTitle(jobj, ieee);
			parserMd5(jobj, ieee);
		}
	}

	public void parserAuthor(JSONObject jobj, IEEEinfo ieee) {
		JSONArray jauthors = jobj.getJSONArray("authors");
		for (Object author : jauthors) {
			JSONObject jid = (JSONObject) author;
			if (ieee.getAuthors() == null) {
				ieee.setAuthors(jid.getString("normalizedName"));
			} else {
				ieee.setAuthors(ieee.getAuthors() + "; " + jid.getString("normalizedName"));
			}
		}
		for (Object id : jauthors) {
			JSONObject jauthor = (JSONObject) id;
			if (ieee.getAuthorsids() == null) {
				ieee.setAuthorsids(jauthor.getString("id"));
			} else {
				ieee.setAuthorsids(ieee.getAuthorsids() + "; " + jauthor.getString("id"));
			}
		}
	}

	public void parserUrl(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("documentLink")) {
			ieee.setUrl("http://ieeexplore.ieee.org" + jobj.getString("documentLink"));
		} else if (jobj.containsKey("articleNumber")) {
			ieee.setUrl("http://ieeexplore.ieee.org/document/" + jobj.getString("articleNumber") + "/");
		} else {
			return;
		}
		ieee.setReferUrl(ieee.getUrl() + "references?ctx=references");
		ieee.setCiteUrl(ieee.getUrl() + "citations?ctx=citations");
	}

	public void parserTitle(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("title"))
			ieee.setTitle(jobj.getString("title"));
		
	}

	public void parserDoi(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("doi")) {
			ieee.setDoi(jobj.getString("doi"));
		}
	}

	public void parserJournal(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("publicationTitle")) {
			ieee.setJournal(jobj.getString("publicationTitle"));
		} 
	}

	public void parserBrief(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("abstract")) {
			ieee.setBrief(jobj.getString("abstract"));
		}
	}

	public void parserCategory(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("articleContentType")) {
			ieee.setCategory(jobj.getString("articleContentType"));
		} 
	}

	public void parserCiteNum(JSONObject jobj, IEEEinfo ieee) {
		if (jobj.containsKey("citationCount"))  {
			String num = jobj.getString("citationCount");
			ieee.setCiteNum(Integer.parseInt(num));
		} 
	}

	public void parserMd5(JSONObject jobj, IEEEinfo ieee) {
		String md5 = MD5Util.MD5(ieee.getUrl() + ieee.getAuthors() + ieee.getDoi());
		
		
		
		ieee.setMd5(md5);
	}

	public Date pubDateFormat(String time) {
		String day = time.split(" ")[0].trim();
		String month = time.split(" ")[1].trim();
		String year = time.split(" ")[time.split(" ").length - 1].trim();
		if (day.contains("-")) {
			day = day.split("-")[0];
		}
		if (month.contains(".")) {
			month = month.replace(".", "");
		}
		if (month.contains("-")) {
			month = month.split("-")[0].trim();
		}
		month = SystemCommon.getMonth(month) + "";
		time = year + "-" + month + "-" + day;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return dateFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
