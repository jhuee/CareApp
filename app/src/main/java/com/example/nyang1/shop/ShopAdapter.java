package com.example.nyang1.shop;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyang1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {
    ArrayList<ShopVO> array;
    Context context;


    public ShopAdapter(ArrayList<ShopVO> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image=array.get(position).getImage();
        int lprice = array.get(position).getLPrice();
        String mallName = array.get(position).getMallName();

        holder.txtlprice.setText("최저가 : " + array.get(position).getLPrice() + "원");
        holder.txttitle.setText(Html.fromHtml(array.get(position).getTitle()));
        holder.txtmallName.setText(Html.fromHtml(array.get(position).getMallName()));
        Picasso.get().load(image).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView txttitle, txtlprice, txtmallName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txttitle=itemView.findViewById(R.id.txttitle);
            txtlprice=itemView.findViewById(R.id.txtlprice);
            image=itemView.findViewById(R.id.image);
            txtmallName=itemView.findViewById(R.id.txtmallName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(array.get(pos).getLink()));
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}