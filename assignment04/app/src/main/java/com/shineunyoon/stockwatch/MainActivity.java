package com.shineunyoon.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<Stock> stockList = new ArrayList<>();

    private RecyclerView rv;
    private StockAdapter stockAdapter;
    private SwipeRefreshLayout sw;
    private DatabaseHandler dh;
    private static MainActivity ma;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        ma = this;

        // For RecyclerView
        rv = (RecyclerView) findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(stockList, this);
        rv.setAdapter(stockAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));



        // For Refresh
        // 6) Swipe-Refresh (pull-down) reloads (re-downloads) all stock data:
        sw = (SwipeRefreshLayout) findViewById(R.id.swiper);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                // 7) Swipe-Refresh attempt with no network connection (No buttons on the error dialog):
                if (!isNetworkAvailable()) {
                    networkErrorMsg();
                    sw.setRefreshing(false);
                    return;
                }

                if (stockList.size() == 0) {
                    sw.setRefreshing(false);
                    return;
                }

                for (Stock stock : stockList) {
                    AsyncLoaderTask loader = new AsyncLoaderTask(ma);
                    loader.execute(stock);
                }
                sw.setRefreshing(false);
            }
        });

        dh = new DatabaseHandler(this);
        if (isNetworkAvailable() == false) {
            return;
        }
        ArrayList<String[]> stocks = dh.loadStocks();

        for(String[] tmp: stocks) {
            Stock s = new Stock(tmp[0], tmp[1]);
            AsyncLoaderTask loader = new AsyncLoaderTask(this);
            loader.execute(s);
        }
    }

    protected void onDestroy() {
        dh.shutDown();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_stock, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Add Stock
            case R.id.add_menu:
                // 3) Adding a stock with no Network Connection
                if (isNetworkAvailable()) stockOption();
                else networkErrorMsg();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void stockOption() {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog, null);

        // Show the dialog with the editText
        AlertDialog.Builder add_builder  = new AlertDialog.Builder(this);
        add_builder.setTitle("Stock Selection");
        add_builder.setMessage("Please enter a Stock Symbol");
        add_builder.setView(view);

        add_builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText inputText = (EditText) view.findViewById(R.id.input);
                String selection = inputText.getText().toString();

                // 4) Adding a stock – specified stock is a duplicate
                for (Stock stock: stockList) {
                    if (selection.equals(stock.getSymbol())) {
                        duplicateAlert(selection);
                        return;
                    }
                }
                new AsyncFinderTask(ma).execute(selection);
            }
        });

        add_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        add_builder.create().show();
    }

    public void duplicateAlert(String symbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol " + symbol + " is already displayed");
        builder.setIcon(R.mipmap.ic_warning_black_48dp);

        builder.create().show();
    }

    public void networkErrorMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
        builder.create().show();
    }

    public boolean isNetworkAvailable() {
        cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    // 9) Tap on a stock to open the MarketWatch.com website entry for the selected stock
    @Override
    public void onClick (View v) {
        String url;

        int pos = rv.getChildLayoutPosition(v);
        Stock stock = stockList.get(pos);
        url = "http://www.marketwatch.com/investing/stock/" + stock.getSymbol();

        Intent webPage = new Intent(Intent.ACTION_VIEW);
        webPage.setData(Uri.parse(url));
        startActivity(webPage);
    }

    public void addNewStock(Stock stock){
        for(Stock each: stockList){
            if(each.getSymbol().equals(stock.getSymbol())) return;
        }
        stockList.add(stock);
        Collections.sort(stockList, new Comparator<Stock > () {
            @Override
            public int compare(Stock s1, Stock s2) {
                return s1.getSymbol().compareTo(s2.getSymbol());
            }
        });
        dh.addStock(stock);
        stockAdapter.notifyDataSetChanged();
    }

    // 8) Long-Press on a stock to delete it:
    @Override
    public boolean onLongClick(View v) {
        final int pos = rv.getChildLayoutPosition(v);

        AlertDialog.Builder delete_builder = new AlertDialog.Builder(this);
        delete_builder.setIcon(R.mipmap.ic_delete_forever_black_48dp);
        delete_builder.setTitle("Delete Stock");
        delete_builder.setMessage("Delete Stock Symbol " + stockList.get(pos).getSymbol() + "?");

        // Yes to msg
        delete_builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dh.deleteStock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                stockAdapter.notifyDataSetChanged();
            }
        });

        // No to msg
        delete_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                return;
            }
        });

        delete_builder.create().show();
        return false;
    }
}
