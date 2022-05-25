package com.example.nyang1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nyang1.R;
import com.example.nyang1.category_search.Document;
import com.example.nyang1.utils.IntentKey;

public class DetailView extends AppCompatActivity {

    TextView placeNameText;
    TextView addressText;
    TextView roadAddressName;
    TextView urlText;
    TextView phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        placeNameText = findViewById(R.id.placeName);
        addressText = findViewById(R.id.addressName);
        roadAddressName = findViewById(R.id.roadAddressName);
        urlText = findViewById(R.id.urlText);
        phoneText = findViewById(R.id.phone);
        processIntent();
    }
    private void processIntent(){
        Intent processIntent = getIntent();
        Document document = processIntent.getParcelableExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA);
        placeNameText.setText(document.getPlaceName());
        addressText.setText(document.getAddressName());
        roadAddressName.setText(document.getRoadAddressName());
        urlText.setText(document.getPlaceUrl());
        phoneText.setText(document.getPhone());
    }

}
