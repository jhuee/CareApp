package com.example.nyang1.shop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NaverShopping {

    //결과 값을 받기 위해서 return
    public static String main(String apiURL,String query,int start,String sort) {
        //static: 클래스 생성없이 메서드 사용가능
        String clientId = "6liYmXCsasxia3bBYbWt";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "lYqJFVHhvw";//애플리케이션 클라이언트 시크릿값";
        try {
            String text = URLEncoder.encode(query, "UTF-8");
            apiURL += "query="+ text; // json 결과
            //String apiURL = "https://openapi.naver.com/v1/search/shop.xml?query="+ text; // xml 결과
            apiURL += "&display=10";
            apiURL += "&start=" + start;
            apiURL += "&sort=" + sort;


            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return e.toString();
        }
    }
}