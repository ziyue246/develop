package homas.crawler.download;

import homas.crawler.bean.SchoolData;
import homas.crawler.extractor.SchoolExtract;
import homas.crawler.http.HtmlInfo;
import homas.crawler.http.sub.HttpSchool;
import homas.crawler.service.mysql.ConnectSqlSchool_data;
import homas.crawler.system.SystemCommon;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SchoolDownload {

	public void getSchoolList(List<String> kw) {

		HtmlInfo html = new HtmlInfo();
		HttpSchool http = new HttpSchool();
		SchoolExtract schoolExtract = new SchoolExtract();
		// String keyword = "Publications Access Articles";// Access Articles
		for (String keyword : kw) {
			try {
				SystemCommon.printLog("Collecting Keyword: " + keyword);

				String 	url  ="http://schools.nyc.gov/schoolsearch/services/SchoolRpc.ashx?rpc";
				html.setOrignUrl(url);
				html.setEncode("UTF-8");

				int start = 0;
				int total = 0;
				int idCount=0;
                do{
                    JSONObject params = new JSONObject();
                    params.put("id", idCount++);
                    params.put("method", "schoolSearch");
                    JSONObject param3 = new JSONObject();
                    param3.put("mode", "search");
                    param3.put("search", "search");
                    param3.put("action", "search");
                    param3.put("search", keyword);
                    param3.put("borough", "");
                    param3.put("grade", "");
                    param3.put("filters", null);
                    param3.put("sort", 0);
                    param3.put("start", start);
                    param3.put("count", 10);

                    params.put("params", param3);
                    //http.httpUrlConnection(html,params);
                    http.doPost(html, params);
                    String content = html.getContent();

                    System.out.println(content);
                    if(total==0) {
                        if (!JSONObject.fromObject(content).containsKey("result"))
                            break;
                        //JSONArray jarray = JSONObject.fromObject(content).getJSONArray("result");
                        JSONObject jobj = JSONObject.fromObject(content).getJSONObject("result");
                        total = jobj.getInt("total");
                        System.out.println(total);
                    }
                    List<SchoolData> list = new ArrayList<>();

                    schoolExtract.JsonParserList(content, list,keyword);

                    for (SchoolData sdata:list) {
                        sdata.print();

                        ConnectSqlSchool_data.insertMessage(sdata);
                    }
                    start += 10;
                    SystemCommon.sleepsSecond(30);
                }while(start<total);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
