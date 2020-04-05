package com.aluohe.citycode.servince.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.aluohe.citycode.entity.BaseName;
import com.aluohe.citycode.servince.CityServince;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aluohe
 * @className CityServinceImpl
 * @projectName city-code
 * @date 2020/4/5 22:39
 * @description
 * @modified_by
 * @version:
 */
@Service
public class CityServinceImpl implements CityServince {
    private static String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/index.html";

    private static List<BaseName> provinces = new ArrayList<>();
    private static List<BaseName> citys = new ArrayList<>();
    private static List<BaseName> countys = new ArrayList<>();
    private static List<BaseName> towns = new ArrayList<>();
    private static List<BaseName> villages = new ArrayList<>();

    static String[] ss = {"citytr", "countytr", "towntr", "villagetr"};

    @Override
    public void servince() {

        try {
            Connection connect = Jsoup.connect(baseUrl).timeout(50000);

//        Document document = connect.get();

            Elements provincetr = connect.get().select("[class=provincetr]");

            for (Element element : provincetr) {

                Elements as = element.getElementsByTag("a");

                for (Element a : as) {
                    //省

                    String province = a.text();//北京市
                    String href = a.attr("href");//11.html

                    String code = handleProCode(href);
                    provinces.add(new BaseName()
                            .setCode(code)
                            .setName(province)
                            .setParent("0")
                    );

//                    if (province.equals("北京市")) {

                        //市
                        handleCode(baseUrl, href, code, 0);
//                    }


                }
            }

            for (BaseName province : provinces) {
                System.out.println(province);
            }
            for (BaseName city : citys) {
                System.out.println(city);
            }

            for (BaseName county : countys) {
                System.out.println(county);
            }

            for (BaseName town : towns) {
                System.out.println(town);
            }
            for (BaseName village : villages) {
                System.out.println(village);
            }

            String base="/opt/";

            excel(provinces, base+"provinces.xlsx");
            excel(citys, base+"citys.xlsx");
            excel(countys, base+"countys.xlsx");
            excel(towns, base+"towns.xlsx");
            excel(villages, base+"villages.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void excel(List<BaseName> list,String path) {
        ArrayList<BaseName> rows = CollUtil.newArrayList(list);

        ExcelWriter writer = ExcelUtil.getWriter(path);
// 合并单元格后的标题行，使用默认标题样式
//        writer.merge(4, "一班成绩单");
// 一次性写出内容，使用默认样式
        writer.write(rows);
// 关闭writer，释放内存
        writer.close();
    }

    private static void handleCode(String baseUrl, String href, String code, Integer count) throws IOException {
        String cityHref = getHref(baseUrl, href);

        Document document = null;
        while (document == null) {
            try {
                Connection cityConn = Jsoup.connect(cityHref).timeout(50000);

                document = cityConn.get();
            } catch (Exception e) {
                System.out.println(cityHref);
                e.printStackTrace();
            }
        }
        System.out.println(cityHref + "  " + count);
        Elements select = document.select("[class=(enam)]".replaceAll("\\(\\w+\\)", ss[count]));

        if (count == 3) {
            for (Element element : select) {
                Elements td = element.getElementsByTag("td");
                String vaCode = td.get(0).text();
                String vaMsg = td.get(2).text();

                if (count == 3) {

                    villages.add(new BaseName().setName(vaMsg).setCode(vaCode).setParent(code));
                }
            }

            return;
        }
        int counts = ++count;

        for (Element cityEle : select) {
            count = counts;
            Elements a1 = cityEle.getElementsByTag("a");
            if (a1 != null && a1.size() > 0) {

                String cityCode = a1.get(0).text();
                String cityMsg = a1.get(1).text();

                if (count == 0) {

                    citys.add(new BaseName().setName(cityMsg).setCode(cityCode).setParent(code));
                }
                if (count == 1) {

                    countys.add(new BaseName().setName(cityMsg).setCode(cityCode).setParent(code));
                }
                if (count == 2) {

                    towns.add(new BaseName().setName(cityMsg).setCode(cityCode).setParent(code));
                }


                String attr = a1.get(0).attr("href");

                handleCode(cityHref, attr, cityCode, count++);

//            String countHref = getHref(cityHref, attr);


            }
        }
    }

//    @NotNull
    private static String getHref(String baseUrl, String href) {
        return baseUrl.replaceAll("(http://.+/).+\\.html", "$1" + href);
    }

    private static String handleProCode(String href) {
        return String.format("%s0000000000", href.replaceAll("(\\d+).html", "$1"));
    }

}
