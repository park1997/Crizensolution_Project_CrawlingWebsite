package hello.hellospring.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
public class CrawlingController {
    public String number;
    public JSONObject information;
    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("msg", "input number.");
        return "index";
    }
    //1525
    @RequestMapping(value="/", method = RequestMethod.POST)
    public String send(@RequestParam("id1")String  number, Model model) throws InterruptedException {
        information = Crawling(number);
        model.addAttribute("msg", information);
        model.addAttribute("id2", number);
        return "index";
    }
    @GetMapping("/collecting")
    public JSONObject showCollected(Model model) throws InterruptedException{
        model.addAttribute("msg", information);
        return information;
    }



    // 크롤링 메소드 -> return 으로 json 객체 받음
    public JSONObject Crawling(String number) throws InterruptedException {

        int interval =1000;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36");
        options.addArguments("disable-gpu");
        options.addArguments("--disable-gpu");
        options.addArguments("lang=ko_KR");
        options.addArguments("window-size=1920x1080"); // 이거 안해주면 headless 때문에 안되고 useragent 넣어줘도 안됨



        ChromeDriver driver = new ChromeDriver(options);
        // 정체 정보가 들어갈 Json
        JSONObject info = new JSONObject();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String url = "https://new.land.naver.com/complexes/"+number;
//        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36").get();
//        Document doc = Jsoup.connect(url).get();

        // Chrome 열기
        driver.get(url);


        // 현재 페이지의 소스코드 가져오기
        Document doc = Jsoup.parse(driver.getPageSource());

        // 단지명 : 해당 아파트
        Elements complex_titles = doc.select("div.complex_title h3.title");
        info.put("단지명",complex_titles.get(0).text());

        // 단지 종류 : 아파트 , 오피스텔 ..
        Elements complex_type = doc.select("span.label--category");
        info.put("단지종류", complex_type.get(0).text());


        String complex_info = "";
        // 혹시나 발생할 버튼클릭에 있어서의 예외처리
        try{
            // "단지 정보" 라는 글자 저장
            complex_info = driver.findElementByClassName("complex_link").getText();
            // 단지 정보 클릭!
            driver.findElementByClassName("complex_link").click();
            // 혹시 모를 로딩으로 인해 1초 쉬어줌
            Thread.sleep(interval);
        }catch(Exception e){
            e.printStackTrace();
        }

        // 현재 페이지의 소스코드 가져오기(페이지 소스 업데이트)
        doc = Jsoup.parse(driver.getPageSource());

        // Json 구조로 만들기위해 여러 인자들을 받을 리스트를 생성
        JSONObject detail_info = new JSONObject();

        // temp
        ArrayList<String> key_temp = new ArrayList<>();
        ArrayList<String> value_temp = new ArrayList<>();


        // 단지 정보 테이블 소스코드
        Elements complex_infos = doc.select("div.detail_box--complex table.info_table_wrap tr.info_table_item");
//        System.out.println(complex_infos.size());

        // for loop 을 이용하여 단지 정보 추출
        for(Element detail_complex_info : complex_infos){
            // 단지 정보 key(세대수, 저/최고층, 사용승인일, 총주차대수, 용적률, 건폐율, ...)
            for(Element detail : detail_complex_info.select("th.table_th")){
                key_temp.add(detail.text().replace("\\",""));
//                System.out.println(detail.text());
            }
            // 단지 정보 value
            for(Element detail : detail_complex_info.select("td.table_td")){
                value_temp.add(detail.text());
//                System.out.println(detail.text());
            }
        }

        for(int i =0; i<key_temp.size();i++){
            detail_info.put(key_temp.get(i), value_temp.get(i));
        }

        info.put(complex_info,detail_info);

        // 나중에 다시 쓰기위해 초기화 함(Json)
        detail_info=null;
        // 해당면적 매물 : [매매 1, 전세2 ..] 와 같이 표현하기 위한 ArrayList 공간
        ArrayList<String> item_detail_info = new ArrayList<>();


        // "단지 내 면적별 정보" 테이블
        Elements size_infos = doc.select("h5.heading_text");
        // 위 테이블에서 뽑아낸 "단지 내 면적별 정보" String
        String width_info_name = doc.select("div.detail_box--floor_plan div.heading h5.heading_text").text().strip().replaceAll(" ","");



        Elements width_info = doc.select("div.detail_box--floor_plan a.detail_sorting_tab");
        JSONObject small_info = new JSONObject();

        // 더보기 탭 클릭하
        try{
            driver.findElementByClassName("btn_moretab").click();
        }catch(Exception e){
//            e.printStackTrace();
        }


        for (int num = 0; num < width_info.size(); num++) {
            JSONObject obj_temp = new JSONObject();
            // click 을 위한 xpath 설정
            String xpath = String.format("//*[@id=\"tab%d\"]", num);
            // tab을 클릭함
            driver.findElementByXPath(xpath).click();

            // 탭 클릭때마다 0.1초 쉼
            // 0.1초만 쉬어도 돌아가는것을 확인했음!
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 현재 페이지의 소스코드 가져오기(페이지 소스 업데이트)
            doc = Jsoup.parse(driver.getPageSource());


            // 공급/전용, 방수/욕실수, 해다연적 세대수.. 등 에 대한 정보 table
            Elements size_infos_table = doc.select("div.detail_box--floor_plan table.info_table_wrap tr.info_table_item");
            // rowspan = 2 인 경우를 구별하기위한 boolean
            boolean checking_boolean = true;

            String title_for_rowspan = null;

            // 단지내 면적별 정보를 담기 위한 JSOUP
            /*
            77m:{xx:xx,
                yy:yy,
                ...
                zz:zz}
             */
            JSONObject obj_detail = new JSONObject();

            // 단지정보 -> 단지 내 면적별 정보 전체 크롤링 하기위한 for loop
            for(Element details_table : size_infos_table){
                // 데이터가 잠깐 담길 지역변수
                String title = null;
                String detail = null;
                JSONArray temp_array = new JSONArray();
                // 여기서 오류가 발생할 확률이 있으므로 예외처리로 오류가 발생하면 정보 못가져오게 !
                // 매물마다 있는 정보가 있고 없는 정보가 있기때문에 오류가 발생 할 수도(?) 있음
                try{
                        // 공급/ 전용, 방수/욕실 수, 해당면적 세대수, 현관구조, 공시가격
                    if (details_table.select("th.table_th").size() != 0 && details_table.select("td.table_td").size() != 0 && (checking_boolean) && (details_table.select("th[rowspan=2]").size() == 0)) {
                        title = details_table.select("th.table_th").text().replaceAll(" ","");
                        if (details_table.select("strong").size() != 0) {
                            detail = details_table.select("strong").text();
                        }else{
                            detail = details_table.select("td.table_td").text();
                        }
                        obj_detail.put(title, detail);
                        // 해당면적 매물, 관리비, 보유세
                    }else if (details_table.select("th[rowspan=2]").size() != 0 && checking_boolean && details_table.select("a").size() != 0) {
                        // 소 분류 이름
                        title = details_table.select("th[rowspan=2]").text().replaceAll(" ","");
                        // rowspan으로 인해 다음 태그에서 title 값을 못가져오므로 다른 변수에 타이틀 값 저장
                        title_for_rowspan = title;
                        Elements detail_infos = details_table.select("a.data");
                        String detail_info_name = null;
                        String detail_info_num = null;
                        for(Element elem : detail_infos){
                            detail_info_name = elem.text();
                            detail_info_num = elem.text();
                            temp_array.add(detail_info_name+" : "+detail_info_num);
                        }
                        obj_detail.put(title, temp_array);
                        checking_boolean = false;
                        // rowspan = 2 다음태그, 해당면적 매물, 관리비, 보유세
                    }else if (!(checking_boolean) && (details_table.select("ul").size() != 0)){
                        Elements detail_infos = details_table.select("li.info_list_item");
                        for(Element elem : detail_infos){
                            detail = elem.text().strip();
                            temp_array.add(detail);
                        }
                        obj_detail.put(title_for_rowspan, temp_array);
                        checking_boolean = true;
                        title_for_rowspan = null;
                        // 보유세 지역 크롤링 ( a 태그가 없는 것이 특징 !)
                    } else if (checking_boolean && details_table.select("th.table_th").size() != 0 && details_table.select("th[rowspan=2]").size() != 0 && details_table.select("a").size() == 0) {
                        title = details_table.select("th.table_th").text().replaceAll(" ","");
                        title_for_rowspan = title;
                        detail = details_table.select("strong").text();
                        temp_array.add(detail);
                        checking_boolean = false;
                        obj_detail.put(title, temp_array);
                    }
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
            // Jsoup 형태로 저장
            small_info.put(width_info.get(num).text(), obj_detail);
        }
        // Jsoup 형태로 저장
        info.put(width_info_name,small_info);


        // 로딩으로 인해 오류 방지를 위한 interval
        Thread.sleep(interval);

        // "시세/실거래가"
        String actual_transaction = doc.select("button.complex_link").get(1).text();

        // 시세/실거래가 클릭
        driver.findElementByXPath("//*[@id=\"summaryInfo\"]/div[2]/div[2]/button[2]").click();

        // 현재 페이지의 소스코드 가져오기(페이지 소스 업데이트)
        doc = Jsoup.parse(driver.getPageSource());

        // 시세/실거래가 면적 정보들
        width_info = doc.select("div#detailContents2 div.detail_sorting_tabs div.detail_sorting_inner div.detail_sorting_tablist span.detail_sorting_width a span");

        // "매매", "전세", "월세"
        Elements selling_type = doc.select("div.detail_box--chart div.detail_sorting_tabs--underbar a");


        JSONObject width_type_obj = new JSONObject();

        try {
            for (int num = 0; num < width_info.size(); num++){
                JSONObject selling_type_obj = new JSONObject();

                // 시세/실거래가 면적 for loop를 통해 클릭
                driver.findElementByLinkText(width_info.get(num).text()).click();
                // 페이지 로딩으로 인하여 0.4초간 쉬어줌
                Thread.sleep(400);
                // 현재 페이지의 소스코드 가져오기(페이지 소스 업데이트)
                doc = Jsoup.parse(driver.getPageSource());

                for (int i = 0; i < selling_type.size()-1; i++) {

                    String id = String.format("marketPriceTab%d", i+1);

                    driver.findElementById(id).click();

                    // 페이지 로딩으로 인하여 0.4초 쉬어줌
                    Thread.sleep(400);

                    // 현재 페이지의 소스코드 가져오기(페이지 소스 업데이트)
                    doc = Jsoup.parse(driver.getPageSource());

                    // actual price detail
                    JSONObject act_pri_details = new JSONObject();
                    // 값이 있는 경우
                    if (doc.select("div.detail_asking_price").size() != 0) {
                        // 상한가, 하한가의 정보가 들은 테이블 소스
                        Elements min_max_table = doc.select("div.detail_table_cell");

                        // "하한가"
                        String min_name = min_max_table.get(0).select("span").text();
                        // 하한가 가격
                        String min_price = min_max_table.get(0).select("strong").text();

                        // "하한가"
                        String max_name = min_max_table.get(1).select("span").text();
                        // 하한가 가격
                        String max_price = min_max_table.get(1).select("strong").text();

                        // 매매가 대비 전세가
                        String rent_fee_name = min_max_table.get(2).select("span").text();
                        // 매매가 대비 전세가 퍼센트(%)
                        String rent_fee_price = min_max_table.get(2).select("strong").text();

                    /*
                    {"하한가":"xx원",
                     "상한가":"xx원",
                     "매매가 대비 전세가":"xx%"}
                     */
                        act_pri_details.put(min_name, min_price);
                        act_pri_details.put(max_name, max_price);
                        act_pri_details.put(rent_fee_name, rent_fee_price);

                    /*
                    {"매매":
                        {"하한가":"xx원",
                        "상한가":"xx원",
                        "매매가 대비 전세가":"xx%"}
                        }
                     */
                        selling_type_obj.put(selling_type.get(i).text(), act_pri_details);

                        // 값이 없는 경우
                    }else {
                        selling_type_obj.put(selling_type.get(i).text(), "해당 기간 내 시세 및 실거래가 정보가 없습니다");
                    }

                    /*
                    {"76m":
                        {"매매":
                            {"하한가":"xx원",
                            "상한가":"xx원",
                            "매매가 대비 전세가":"xx%"}
                            }
                     */
                    width_type_obj.put(width_info.get(num).text(), selling_type_obj);
                }
            }
            /*
            {"시세/실거래가":
                {"76m":
                    {"매매":
                        {"하한가":"xx원",
                        "상한가":"xx원",
                        "매매가 대비 전세가":"xx%"}
                        }
                    }
             */
            info.put(actual_transaction, width_type_obj);

        }catch (Exception e){
            e.printStackTrace();
        }


        // 동일 매물 묶기
        driver.findElementByClassName("address_filter").click();
        Thread.sleep(interval);
        // 매물 크롤링
        // 현재 페이지의 소스코드 가져오기(페이지 소스 업데이트)
        doc = Jsoup.parse(driver.getPageSource());

        boolean isEnd = true;
        js.executeScript("var prevListCount = document.querySelectorAll('#articleListArea .item').length;\n" +
                "var isFirst = true;\n" +
                "window.scrollToBottom = function(){\n" +
                "    // scroll 이동\n" +
                "    var scrollWrp = document.querySelector('.item_list.item_list--article');\n" +
                "    scrollWrp.scrollTop = scrollWrp.scrollHeight - scrollWrp.clientHeight;\n" +
                "\n" +
                "    var nextListCount = document.querySelectorAll('#articleListArea .item').length;\n" +
                "\n" +
                "    // 더이상 호출할 리스트가 없음\n" +
                "    if(!isFirst && prevListCount == nextListCount){\n" +
                "        console.log('finished! item count : ', nextListCount)\n" +
                "        return nextListCount\n" +
                "    }\n" +
                "    else{\n" +
                "        console.log('next')\n" +
                "        prevListCount = nextListCount;\n" +
                "    }\n" +
                "\n" +
                "    isFirst = false\n" +
                "}");

        while(isEnd) {
            Object result = js.executeScript("return window.scrollToBottom()");
            if (result != null) {
//                System.out.println("total count : " + result);
                isEnd = false;
            }
            Thread.sleep(400);
        }

        // soup 업데이트 !
        doc = Jsoup.parse(driver.getPageSource());
        Elements for_sale_names = doc.select("div.item_list.item_list--article div span.text");
        Elements for_sale_prices = doc.select("div.item_list.item_list--article div div.price_line");
        Elements for_sale_types = doc.select("div.item_list.item_list--article p.line strong.type");
        Elements for_sale_specs = doc.select("div.item_list.item_list--article div.info_area");
        JSONArray for_sale_arr = new JSONArray();

        for (int i=0; i<for_sale_names.size();i++){
            JSONObject for_sale_details = new JSONObject();
            for_sale_details.put("매물명", for_sale_names.get(i).text());
            for_sale_details.put("가격", for_sale_prices.get(i).text());
            for_sale_details.put("종류", for_sale_types.get(i).text());

            // 면적, 층 , 방향만 있는 span.spec 만 들고오기 위한 필터링 과정!
            Element element = for_sale_specs.get(i).select("span.spec").get(0);
            // 면적, 층, 방향이 들어갈 String Array
            String[] specs = element.text().split(",");

            for_sale_details.put("면적", specs[0].strip());
            for_sale_details.put("층", specs[1].strip());
            for_sale_details.put("방향", specs[2].strip());

            for_sale_arr.add(for_sale_details);
        }
        // 매물 : [{매물 명: xxx, 가격 : xxx, 종류: xxx, 스펙 : xxx},
        //        {매물 명: xxx, 가격 : xxx, 종류: xxx, 스펙 : xxx},
        //        ...]
        info.put("매물", for_sale_arr);

        // 결과 출력
//        System.out.println(info);

        driver.quit();

        return info;
    }
}
