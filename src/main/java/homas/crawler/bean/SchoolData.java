package homas.crawler.bean;

import homas.crawler.system.SystemCommon;

public class SchoolData extends CommonData{

	private String name;
	private String district;
	private String locationCode;
	private String optCode;
	private String primaryAddressLine;
	private String stateCode;
	private String zip;
	private String accessibilityCode;
	private String accessibilityDescription;
	private String profile;
	private String boroughNumber;
	private String boroughName;
	private String phoneNumber;
	private String size;
	private String grades;
	private String distance;
	private String rank;
	private String isChildhoodCenter;
	private String displayName;
	private String dayLength;
	private String managedBy;
	private String seats;
	private String prekType;
	private String primaryBuildingCode;

	public String getOptCode() {
		return optCode;
	}

	public void setOptCode(String optCode) {
		this.optCode = optCode;
	}

	public String getName() {
		return name;
	}

	public String getDistrict() {
		return district;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public String getPrimaryAddressLine() {
		return primaryAddressLine;
	}

	public String getStateCode() {
		return stateCode;
	}

	public String getZip() {
		return zip;
	}

	public String getAccessibilityCode() {
		return accessibilityCode;
	}

	public String getAccessibilityDescription() {
		return accessibilityDescription;
	}

	public String getProfile() {
		return profile;
	}

	public String getBoroughNumber() {
		return boroughNumber;
	}

	public String getBoroughName() {
		return boroughName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getSize() {
		return size;
	}

	public String getGrades() {
		return grades;
	}

	public String getDistance() {
		return distance;
	}

	public String getRank() {
		return rank;
	}

	public String getIsChildhoodCenter() {
		return isChildhoodCenter;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDayLength() {
		return dayLength;
	}

	public String getManagedBy() {
		return managedBy;
	}

	public String getSeats() {
		return seats;
	}

	public String getPrekType() {
		return prekType;
	}

	public String getPrimaryBuildingCode() {
		return primaryBuildingCode;
	}



	public void setName(String name) {
		this.name = name;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public void setPrimaryAddressLine(String primaryAddressLine) {
		this.primaryAddressLine = primaryAddressLine;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public void setAccessibilityCode(String accessibilityCode) {
		this.accessibilityCode = accessibilityCode;
	}

	public void setAccessibilityDescription(String accessibilityDescription) {
		this.accessibilityDescription = accessibilityDescription;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public void setBoroughNumber(String boroughNumber) {
		this.boroughNumber = boroughNumber;
	}

	public void setBoroughName(String boroughName) {
		this.boroughName = boroughName;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setGrades(String grades) {
		this.grades = grades;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public void setIsChildhoodCenter(String isChildhoodCenter) {
		this.isChildhoodCenter = isChildhoodCenter;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDayLength(String dayLength) {
		this.dayLength = dayLength;
	}

	public void setManagedBy(String managedBy) {
		this.managedBy = managedBy;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

	public void setPrekType(String prekType) {
		this.prekType = prekType;
	}

	public void setPrimaryBuildingCode(String primaryBuildingCode) {
		this.primaryBuildingCode = primaryBuildingCode;
	}
	public void print(){

		System.out.println();
		System.out.println();
		SystemCommon.printLog("一个学校信息：");
		SystemCommon.printLog("name                     : " + this.getName());
		SystemCommon.printLog("district                 : " + this.getDistrict());
		SystemCommon.printLog("locationCode             : " + this.getLocationCode());
		SystemCommon.printLog("searchKeyword            : " + this.getSearchKeyword());
		SystemCommon.printLog("md5                      : " + this.getMd5());
		SystemCommon.printLog("optCode                  : " + this.getOptCode());
		SystemCommon.printLog("primaryAddressLine       : " + this.getPrimaryAddressLine());
		SystemCommon.printLog("stateCode                : " + this.getStateCode());
		SystemCommon.printLog("zip                      : " + this.getZip());
		SystemCommon.printLog("accessibilityCode        : " + this.getAccessibilityCode());
		SystemCommon.printLog("accessibilityDescription : " + this.getAccessibilityDescription());
		SystemCommon.printLog("profile                  : " + this.getProfile());
		SystemCommon.printLog("boroughNumber            : " + this.getBoroughNumber());
		SystemCommon.printLog("boroughName              : " + this.getBoroughName());
		SystemCommon.printLog("phoneNumber              : " + this.getPhoneNumber());
		SystemCommon.printLog("size                     : " + this.getSize());
		SystemCommon.printLog("grades                   : " + this.getGrades());
		SystemCommon.printLog("distance                 : " + this.getDistrict());
		SystemCommon.printLog("rank                     : " + this.getRank());
		SystemCommon.printLog("isChildhoodCenter        : " + this.getIsChildhoodCenter());
		SystemCommon.printLog("displayName              : " + this.getDisplayName());
		SystemCommon.printLog("dayLength                : " + this.getDayLength());
		SystemCommon.printLog("managedBy                : " + this.getManagedBy());
		SystemCommon.printLog("seats                    : " + this.getSeats());
		SystemCommon.printLog("prekType                 : " + this.getPrekType());
		SystemCommon.printLog("primaryBuildingCode      : " + this.getPrimaryBuildingCode());
		System.out.println();
		System.out.println();
	}
	
}