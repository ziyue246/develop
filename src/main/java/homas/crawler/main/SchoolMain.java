package homas.crawler.main;

import homas.crawler.download.SchoolDownload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziyue on 2017/3/31 0031.
 */
public class SchoolMain {

    public static void main(String []args){
        //System.out.println(new Date().getTime());
        SchoolDownload sd = new SchoolDownload();
        List<String> kw = new ArrayList<>();
        kw.add("New York Times");
        sd.getSchoolList(kw);
    }
}
