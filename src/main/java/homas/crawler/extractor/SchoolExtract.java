package homas.crawler.extractor;

import homas.crawler.bean.SchoolData;
import homas.crawler.util.MD5Util;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

public class SchoolExtract {


	public void JsonParserList(String content, List<SchoolData> list,String keyword) {
		if (!JSONObject.fromObject(content).containsKey("result")) {
			System.out.println("result");
			return;
		}
		JSONObject job = JSONObject.fromObject(content).getJSONObject("result");
		if(!job.containsKey("items")){
			System.out.println("items");
			return ;
		}
		JSONArray jarray =   job.getJSONArray("items");

		for (Object obj : jarray) {
			JSONObject subjobj = (JSONObject) obj;

			System.out.println(subjobj.getString("name"));
			if (!subjobj.containsKey("name")) {
				continue;
			}
			SchoolData schoolData = new SchoolData();
			list.add(schoolData);

            schoolData.setSearchKeyword(keyword);
			parserName(subjobj, schoolData);
			parserDistrict(subjobj, schoolData);
			parserLocationCode(subjobj, schoolData);
			parserOptCode(subjobj, schoolData);
			parserAccessibilityCode(subjobj, schoolData);
			parserAccessibilityDescription(subjobj, schoolData);
			parserProfile(subjobj, schoolData);
			parserBoroughNumber(subjobj, schoolData);
			parserBoroughName(subjobj, schoolData);
			parserPhoneNumber(subjobj, schoolData);
			parserSize(subjobj, schoolData);
			parserGrades(subjobj, schoolData);
			parserDistance(subjobj, schoolData);
			parserRank(subjobj, schoolData);
			parserDisplayName(subjobj, schoolData);
			parserIsChildhoodCenter(subjobj, schoolData);
			parserDayLength(subjobj, schoolData);
			parserManagedBy(subjobj, schoolData);
			parserPrimaryBuildingCode(subjobj, schoolData);
			parserPrimaryAddressLine(subjobj, schoolData);
			parserStateCode(subjobj, schoolData);
			parserPrekType(subjobj, schoolData);
			parserZip(subjobj, schoolData);
			parserSeats(subjobj, schoolData);
			parserMd5(subjobj, schoolData);
		}
	}


	public void parserMd5(JSONObject jobj, SchoolData sdata) {
		sdata.setMd5(MD5Util.MD5(sdata.getSearchKeyword()+sdata.getName()+sdata.getLocationCode()));
	}

	public void parserName(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("name")){
			sdata.setName(jobj.getString("name"));
		}
	}
	public void parserDistrict(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("district")){
			sdata.setDistrict(jobj.getString("district"));
		}
	}
	public void parserLocationCode(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("locationCode")){
			sdata.setLocationCode(jobj.getString("locationCode"));
		}
	}
	public void parserOptCode(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("optCode")){

			sdata.setOptCode(jobj.getString("optCode"));
		}
	}public void parserPrimaryAddressLine(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("primaryAddressLine")){
			sdata.setName(jobj.getString("primaryAddressLine"));
		}
	}
	public void parserSeats(JSONObject jobj, SchoolData sdata){
		if(jobj.containsKey("seats")){
			sdata.setSeats(jobj.getString("seats"));
		}
	}
	public void parserPrekType(JSONObject jobj, SchoolData sdata){
		if(jobj.containsKey("prekType")){
			sdata.setPrekType(jobj.getString("prekType"));
		}
	}

	public void parserStateCode(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("stateCode")){
			sdata.setStateCode(jobj.getString("stateCode"));
		}
	}
	public void parserZip(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("zip")){
			sdata.setZip(jobj.getString("zip"));
		}
	}
	public void parserAccessibilityCode(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("accessibilityCode")){
			sdata.setAccessibilityCode(jobj.getString("accessibilityCode"));
		}
	}
	public void parserAccessibilityDescription(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("accessibilityDescription")){
			sdata.setAccessibilityDescription(jobj.getString("accessibilityDescription"));
		}
	}
	public void parserProfile(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("profile")){
			sdata.setProfile(jobj.getString("profile"));
		}
	}
	public void parserBoroughNumber(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("boroughNumber")){
			sdata.setBoroughNumber(jobj.getString("boroughNumber"));
		}
	}
	public void parserBoroughName(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("boroughName")){
			sdata.setBoroughName(jobj.getString("boroughName"));
		}
	}
	public void parserPhoneNumber(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("phoneNumber")){
			sdata.setPhoneNumber(jobj.getString("phoneNumber"));
		}
	}
	public void parserSize(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("size")){
			sdata.setSize(jobj.getString("size"));
		}
	}
	public void parserGrades(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("grades")){
			sdata.setGrades(jobj.getString("grades"));
		}
	}
	public void parserDistance(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("distance")){
			sdata.setDistance(jobj.getString("distance"));
		}
	}
	public void parserRank(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("rank")){
			sdata.setRank(jobj.getString("rank"));
		}
	}
	public void parserDisplayName(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("displayName")){
			sdata.setDisplayName(jobj.getString("displayName"));
		}
	}
	public void parserIsChildhoodCenter(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("isChildhoodCenter")){
			sdata.setIsChildhoodCenter(jobj.getString("isChildhoodCenter"));
		}
	}
	public void parserDayLength(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("dayLength")){
			sdata.setDayLength(jobj.getString("dayLength"));
		}
	}
	public void parserManagedBy(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("managedBy")){
			sdata.setManagedBy(jobj.getString("managedBy"));
		}
	}
	public void parserPrimaryBuildingCode(JSONObject jobj, SchoolData sdata) {
		if(jobj.containsKey("primaryBuildingCode")){
			sdata.setPrimaryBuildingCode(jobj.getString("primaryBuildingCode"));
		}
	}

}
