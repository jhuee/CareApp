package com.example.nyang1.shop;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nyang1.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {


    private ImageView iconImageView;
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView ratingTextView;
    private TextView reviewCountTextView;
    private final ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();


    public ListViewAdapter() { }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        titleTextView = convertView.findViewById(R.id.title);
        iconImageView = convertView.findViewById(R.id.icon);
        contentTextView = convertView.findViewById(R.id.content);
        reviewCountTextView = convertView.findViewById(R.id.reviewCount);
        ListViewItem listViewItem = listViewItemList.get(position);

        titleTextView.setText(listViewItem.getTitle());
        iconImageView.setImageBitmap(listViewItem.getIcon());
        contentTextView.setText(listViewItem.getContent());
        reviewCountTextView.setText(listViewItem.getReviewCount());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(String title, Bitmap icon, String content, String rating, String reviewCount) {
        ListViewItem item = new ListViewItem();

        item.setTitle(title);
        item.setIcon(icon);
        item.setContent(content);
        item.setRating(rating);
        item.setReviewCount(reviewCount);
        listViewItemList.add(item);
    }

    public void clear() {
        listViewItemList.clear();
    }


}