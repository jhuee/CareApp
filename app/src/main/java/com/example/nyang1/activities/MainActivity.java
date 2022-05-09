package com.example.nyang1.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import com.example.nyang1.R;
import com.example.nyang1.RetroCallback;
import com.example.nyang1.api.ApiClient;
import com.example.nyang1.api.ApiInterface;
import com.example.nyang1.category_search.CategoryResult;
import com.example.nyang1.category_search.Document;
import com.example.nyang1.shop.Shopping;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener {

    private static final String LOG_TAG = "MainActivity";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    //xml
    private ImageButton shop;
    //인터페이스
    private ApiInterface apiInterface;

    private double mCurrentX, mCurrentY;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private MapPoint mapPoint;
    MapPoint currentMapPoint;


    ArrayList<Document> hospitalList = new ArrayList<>(); //HP8
    ArrayList<Document> pharmacyList = new ArrayList<>(); //PM9
    MapPOIItem customMarker = new MapPOIItem();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shop = findViewById(R.id.hpBtn);
        shop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Shopping.class);
                startActivity(intent);

            }
        });
        initView();
    }


    private void sendImplicitBroadcast(Context ctxt, Intent i)

    {



    }


    private void initView() {
        //맵 실행
        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        mapView.setMapViewEventListener(this); //맵 리스너, 현재 위치 업데이트

        mapView.setCurrentLocationEventListener(this);

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); //트래킹 모드 헤딩 없음 , 현재 위치에 따라 따라다님

        //맵 리스너
        mapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mapView.setPOIItemEventListener(this);
        mapView.setOpenAPIKeyAuthenticationResultListener(this);



    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        //이 좌표로 지도 중심 이동
        mapView.setMapCenterPoint(currentMapPoint, true);
        //전역변수로 현재 좌표 저장
        mCurrentX = mapPointGeo.latitude;
        mCurrentY = mapPointGeo.longitude;
        Log.d(LOG_TAG, "현재위치 => " + mCurrentX + "  " + mCurrentY);

        requestHospitalLocal(mCurrentX, mCurrentY);
    }
    private ApiInterface entityKeyword;

    /* 생성자를 통해 레트로핏 객체를 가져옴 */
    public void requestHospitalLocal(double x, double y) {
        entityKeyword = ApiClient.getInstance().getApiClient().create(ApiInterface.class);


            //YOUR_RSET_API_KEY 에 REST_API_KEY를 넣어줘야함.
            Call<CategoryResult> call = entityKeyword.getSearchHospital("KakaoAK af09bdf5053ceef61e79c7731727e109", "동물병원", "HP8", x+"", y+"", 2000);
            call.enqueue(new Callback<CategoryResult>() {
                @Override
                public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                    if (response.isSuccessful()) {
                        Log.d("Success", "데이터 받아오기 성공");
                        hospitalList.addAll(response.body().getDocuments());
                    final String result = response.body().toString();
                    Log.d("retrofit", result);
                    } else {
                        Log.e("Fail", "서버 통신 실패1");
                    }
                }

                @Override
                public void onFailure(Call<CategoryResult> call, Throwable t) {
                    Log.e("Fail", "서버 통신 실패");
                    Log.e("Fail", t.getMessage());
                }
            });
        }

//    private void requestHospitalLocal(double x, double y) {
//        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//        Call<CategoryResult> call = apiInterface.getSearchHospital(getString(R.string.restapi_key),"동물병원" ,"HP8", x + "", y + "", 2000); //반경 2km로
//        call.enqueue(new Callback<CategoryResult>() {
//            @Override
//            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
//                if(response.isSuccessful()){
//                    hospitalList.addAll(response.body().getDocuments());
//                    final String result = response.body().toString();
//                    Log.d("retrofit", result);
//
//                }
//            }


//            @Override
//            public void onFailure(Call<CategoryResult> call, Throwable t) {
//                Log.d("retrofit", "통신 실패"+x+y);
//                Log.d("retrofit", t.getMessage());
//
//
//            }
//        });
//
//    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }
}