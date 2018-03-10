package com.shineunyoon.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // String symbol, String company_name, double price, double price_change, double change_percentage
    public TextView symbol;
    public TextView company_name;
    public TextView price;
    public TextView price_change_percentage; // combination of price_change and change_percentage

    public MyViewHolder(View view) {
        super(view);

        // symbol, name, price, change, percentage
        symbol = (TextView)view.findViewById(R.id.symbol);
        company_name = (TextView)view.findViewById(R.id.company_name);
        price = (TextView)view.findViewById(R.id.price);
        price_change_percentage = (TextView)view.findViewById(R.id.price_change_percentage);
    }
}
