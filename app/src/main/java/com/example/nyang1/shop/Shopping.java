package com.example.nyang1.shop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.nyang1.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Shopping extends Activity {
    Bitmap bitmap_tmp;
    EditText edit;
    XmlPullParser xpp;
    String key = "055be09b2c4d1b783f77fdc342061441";
    int pageNum;
    private ListView listview;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);

        edit = findViewById(R.id.edit);

        adapter = new ListViewAdapter();

        listview = findViewById(R.id.listview);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getXmlData(pageNum = 1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
                adapter.clear();
                break;
            case R.id.nextPageButton:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getXmlData(++pageNum);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
                adapter.clear();
                break;
            case R.id.prePageButton:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (pageNum > 1) {
                            getXmlData(--pageNum);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }).start();
                adapter.clear();
                break;
        }
    }


    void getXmlData(int pageResult) {
        String str = edit.getText().toString();
        String search_word = URLEncoder.encode(str);
        String sortCd = "CP";
        String apiCode = "ProductSearch";
        int index = 1;

        String queryUrl = "https://openapi.11st.co.kr/openapi/OpenApiService.tmall?key="
                + key
                + "&apiCode="
                + apiCode
                + "&sortCd="
                + sortCd
                + "&pageNum="
                + pageResult
                + "&keyword="
                + search_word;


        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            System.out.println(queryUrl);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "EUC-KR"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();
            ListViewItem item = new ListViewItem();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//테그 이름 얻어오기
                        if (tag.equals("Products")) ;
                        else if (tag.equals("ProductName")) {
                            xpp.next();
                            item.setTitle(xpp.getText());
                        } else if (tag.equals("ProductImage")) {
                            xpp.next();
                            try {
                                URL url_img = new URL("https://" + xpp.getText().substring(7));
                                URLConnection conn = url_img.openConnection();
                                conn.connect();
                                int nSize = conn.getContentLength();
                                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
                                bitmap_tmp = BitmapFactory.decodeStream(bis);
                                bis.close();
                                adapter.addItem(index + ". " + item.getTitle(), bitmap_tmp, item.getContent() + "원", item.getRating(), "리뷰 : " + item.getReviewCount());
                                index++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (tag.equals("ProductPrice")) {
                            xpp.next();
                            item.setContent(xpp.getText());
                        } else if (tag.equals("Rating")) {
                            xpp.next();
                            item.setRating(xpp.getText());
                        } else if (tag.equals("ReviewCount")) {
                            xpp.next();
                            item.setReviewCount(xpp.getText());
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
        }
    }
}