package com.shineunyoon.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shineunyoon on 2018. 2. 22..
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Stock> stockList;
    private MainActivity ma;

    public StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        this.ma = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_row, parent, false);

        itemView.setOnClickListener(ma);
        itemView.setOnLongClickListener(ma);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        holder.symbol.setText(stock.getSymbol());
        holder.company_name.setText(stock.getCompany_name());
        holder.price.setText(Double.toString(stock.getPrice()));

        if (stock.getPrice_change() > 0) {
            holder.price_change_percentage.setText("▲ " + Double.toString(stock.getPrice_change()) + "(" + Double.toString(stock.getChange_percentage())+"%)");
            holder.symbol.setTextColor(Color.GREEN);
            holder.company_name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.price_change_percentage.setTextColor(Color.GREEN);

        } else {
            holder.price_change_percentage.setText("▼ " + Double.toString(stock.getPrice_change()) + "(" + Double.toString(stock.getChange_percentage())+"%)");
            holder.symbol.setTextColor(Color.RED);
            holder.company_name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.price_change_percentage.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
