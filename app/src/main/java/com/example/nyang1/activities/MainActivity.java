package com.example.nyang1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nyang1.R;
import com.example.nyang1.api.ApiClient;
import com.example.nyang1.api.ApiInterface;
import com.example.nyang1.calendar.CalendarView;
import com.example.nyang1.category_search.CategoryResult;
import com.example.nyang1.category_search.Document;
import com.example.nyang1.utils.IntentKey;
import com.kakao.util.maps.helper.Utility;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

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
    private MapView mMapView;
    private ViewGroup mMapViewContainer;
    private MapPoint mapPoint;
    MapPoint currentMapPoint;



    ArrayList<Document> hospitalList = new ArrayList<>();
    ArrayList<Document> petShopList = new ArrayList<>();
    MapPOIItem customMarker = new MapPOIItem();

    public void key() {
        String keyhash = Utility.getKeyHash(this);
        Log.e("keyhash", keyhash);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hospitalList.clear();
        petShopList.clear();
        shop = findViewById(R.id.shopBtn);
        shop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalendarView.class);
                startActivity(intent);
            }
        });
        initView();

        key();
//        processIntent();
    }


    //인텐트처리
//    private void processIntent() {
//        Intent getIntent = getIntent();
//
//        hospitalList = getIntent.getParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA1);
//        petShopList = getIntent.getParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA2);
//
//    }




    private void initView() {
        //맵 실행
        mMapView = new MapView(this);
        ViewGroup mMapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mMapViewContainer.addView(mMapView);

        mMapView.setMapViewEventListener(this); //맵 리스너, 현재 위치 업데이트

        mMapView.setCurrentLocationEventListener(this);

        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); //트래킹 모드 헤딩 없음 , 현재 위치에 따라 따라다님

        //맵 리스너
        mMapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mMapView.setPOIItemEventListener(this);
        mMapView.setOpenAPIKeyAuthenticationResultListener(this);



    }




    @Override
    public void onCurrentLocationUpdate(MapView mMapView, MapPoint mapPoint, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
//        //이 좌표로 지도 중심 이동
//        mMapView.setMapCenterPoint(currentMapPoint, true);
        //전역변수로 현재 좌표 저장
        mCurrentX = mapPointGeo.latitude;
        mCurrentY = mapPointGeo.longitude;
        Log.d(LOG_TAG, "현재위치 => " + mCurrentX + "  " + mCurrentY);

        requestHospitalLocal(mCurrentX, mCurrentY);
        requestPharmacyLocal(mCurrentX, mCurrentY);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }


    private void requestHospitalLocal(double x, double y) {

            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<CategoryResult> call = apiInterface.getSearchHospital(getString(R.string.restapi_key), "동물병원", y + "", x + "", 10000); //반경 2km로
            call.enqueue(new Callback<CategoryResult>() {
                @Override
                public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                    Log.e("tag1", "확인");
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getDocuments() != null) {
                            hospitalList.addAll(response.body().getDocuments());
                        }

//                        final String result1 = response.raw().body().toString();
//                        Log.d("retrofit", result);
                        Log.e("retrofit", "성공");

                        int tagNum = 10;
                        for (Document document : hospitalList) {
                            MapPOIItem marker = new MapPOIItem();
                            marker.setItemName(document.getPlaceName());
                            marker.setTag(tagNum++);
                            double x = Double.parseDouble(document.getY());
                            double y = Double.parseDouble(document.getX());
                            Log.e("이거 왜이래", x + "   " + y);
                            //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                            marker.setMapPoint(mapPoint);
                            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                            marker.setCustomImageResourceId(R.drawable.hospital_marker); // 마커 이미지.
                            marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                            marker.setCustomImageAnchor(0.5f, 1.0f);
                            mMapView.addPOIItem(marker);
                        }
                    } else {
                        try {

                            assert response.errorBody() != null;
                            String str = response.errorBody().string();
                            Log.e("tag2", str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e("ta2", response.code() + " " + response.message() + response.errorBody().toString());


                    }
                }


                @Override
                public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                    Log.d("retrofit", "통신 실패" + x + y);
                    Log.d("retrofit", x + " " + y);
                    Log.d("retrofit", t.getMessage());
                    Log.d("retrofit", t.getCause().toString());
                    t.getCause();


                }

            });

        }


    public void requestPharmacyLocal(double x, double y) {
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<CategoryResult> call = apiInterface.getSearchPetShop(getString(R.string.restapi_key), "애견샵", y + "", x + "", 10000); //반경 2km로
            call.enqueue(new Callback<CategoryResult>() {
                @Override
                public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                    Log.e("tag1", "확인");
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        if (response.body().getDocuments() != null) {
                            petShopList.addAll(response.body().getDocuments());

                        }
//                        final String result = response.raw().body().toString();
//                        Log.d("retrofit", result);
                        Log.e("retrofit", "성공");

                        int tagNum = 10;
                        for (Document document : petShopList) {
                            MapPOIItem marker = new MapPOIItem();
                            marker.setItemName(document.getPlaceName());
                            marker.setTag(tagNum++);
                            double x = Double.parseDouble(document.getY());
                            double y = Double.parseDouble(document.getX());
                            Log.e("이거 왜이래", x + "   " + y);
                            //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                            marker.setMapPoint(mapPoint);
                            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                            marker.setCustomImageResourceId(R.drawable.petshop_marker2); // 마커 이미지.
                            marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                            marker.setCustomImageAnchor(0.5f, 1.0f);
                            mMapView.addPOIItem(marker);
                        }
                    } else {
                        try {

                            String str = response.errorBody().string();
                            Log.e("tag2", str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e("ta2", response.code() + " " + response.message() + response.errorBody().toString());


                    }
                }


                @Override
                public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                    Log.d("retrofit", "통신 실패" + x + y);
                    Log.d("retrofit", x + " " + y);
                    Log.d("retrofit", t.getMessage());
                    Log.d("retrofit", t.getCause().toString());
                    t.getCause();


                }

            });

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
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        double lat = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        double lng = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CategoryResult> call = apiInterface.getSearchPetShop(getString(R.string.restapi_key), mapPOIItem.getItemName(), String.valueOf(lat), String.valueOf(lng), 1);
        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse( Call<CategoryResult> call,  Response<CategoryResult> response) {
//                mLoaderLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, DetailView.class);
                    assert response.body() != null;
                    intent.putExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA, response.body().getDocuments().get(0));
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
//                FancyToast.makeText(getApplicationContext(), "해당장소에 대한 상세정보는 없습니다.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
//                mLoaderLayout.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, DetailView.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}