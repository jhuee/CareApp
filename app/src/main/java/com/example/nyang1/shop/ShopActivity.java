package com.example.nyang1.shop;

import android.app.Activity;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyang1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    //데이터를 생성하기 위해서
    ArrayList<ShopVO> array;
    String apiURL="https://openapi.naver.com/v1/search/shop.json?";
    String query="사료";
    String this_query="사료";
    int start=1;
    String sort = "sim";
    String spn_sort = "sim";
    Button f1, f2, f3, f4;
    ImageButton search;

    RecyclerView list;
    ShopAdapter adapter;

    String[] items = {"관련도순", "날짜순", "가격높은순", "가격낮은순"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_main);




        list=findViewById(R.id.list);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        list.setLayoutManager(manager);

        //생성
        array=new ArrayList<ShopVO>();

        //생성
        new ShopThread().execute();

        //생성

        Spinner spn = (Spinner) findViewById(R.id.sort);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 스피너에 어댑터 설정
        spn.setAdapter(adapter);

        //사료
        f1 = findViewById(R.id.f1);
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = 1;
                array.clear();
                query = "사료";
                sort = spn_sort;
                getQuery(query);
                new ShopThread().execute();
            }
        });

        //간식
        f2 = findViewById(R.id.f2);
        f2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                start = 1;
                array.clear();
                query = "애완 간식";
                sort = spn_sort;
                getQuery(query);
                new ShopThread().execute();
            }
        });
        f3 = findViewById(R.id.f3);
        f3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                start = 1;
                array.clear();
                query = "애완 용품";
                sort = spn_sort;
                getQuery(query);
                new ShopThread().execute();
            }
        });

        f4 = findViewById(R.id.f4);
        f4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                start = 1;
                array.clear();
                query = "애완 목욕";
                sort = spn_sort;
                getQuery(query);
                new ShopThread().execute();
            }
        });

        final EditText edtsearch = findViewById(R.id.edtsearch);
        edtsearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == EditorInfo.IME_ACTION_DONE ) {
                    start = 1;
                    array.clear();
                    query = edtsearch.getText().toString();
                    sort = spn_sort;
                    getQuery(query);
//                if(keyCode == KeyEvent.KEYCODE_ENTER) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(edtsearch.getWindowToken(), 0);    //hide keyboard

                    new ShopThread().execute();
//                    return false;
//                }

                    return true;

                }
                return false;
            }
        });
        edtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE ) {
                    start = 1;
                    array.clear();
                    query = edtsearch.getText().toString();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtsearch.getWindowToken(), 0);    //hide keyboard
                    getQuery(query);
                    sort = spn_sort;
                    new ShopThread().execute();


                    return true;

                }
                return true;
            }

        });


        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if(items[i].equals("관련도순")) {
                   start = 1;
                   array.clear();
                   query = this_query;
                   spn_sort = "sim";
                   getSort(spn_sort);
                   new ShopThread().execute();
               } else if(items[i].equals("날짜순")) {
                   start = 1;
                   array.clear();
                   query = this_query;
                   spn_sort = "date";
                   getSort(spn_sort);
                   new ShopThread().execute();
               } else if(items[i].equals("가격높은순")) {
                   start = 1;
                   array.clear();
                   query = this_query;
                   spn_sort = "dsc";
                   getSort(spn_sort);
                   new ShopThread().execute();
               } else if(items[i].equals("가격낮은순")){
                   start = 1;
                   array.clear();
                   query = this_query;
                   spn_sort = "asc";
                   getSort(spn_sort);
                   new ShopThread().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        FloatingActionButton btnmore=findViewById(R.id.btnmore);
        btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start += 10;
                new ShopThread().execute();
            }
        });
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                start = 1;
                array.clear();
                query = edtsearch.getText().toString();
                new ShopThread().execute();
            }
        });


    }

    private String getSort(String spn_sort) {
        this.sort = spn_sort;
        return sort;
    }

    private String getQuery(String this_query) {
        this.query = this_query;
        return query;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    //BackThread 생성
    class ShopThread extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            return NaverShopping.main(apiURL,query,start,sort);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("결과.........."+s);
            try {
                JSONArray jArray=new JSONObject(s).getJSONArray("items");
                for(int i=0; i<jArray.length(); i++){
                    JSONObject obj=jArray.getJSONObject(i);
                    ShopVO vo = new ShopVO();
                    vo.setImage(obj.getString("image"));
                    vo.setLPrice(obj.getInt("lprice"));
                    vo.setLink(obj.getString("link"));
                    vo.setTitle(obj.getString("title"));
                    vo.setMallName(obj.getString("mallName"));
                    array.add(vo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter=new ShopAdapter(array,ShopActivity.this);
            list.setAdapter(adapter);
            list.scrollToPosition(start);

        }
    }
}