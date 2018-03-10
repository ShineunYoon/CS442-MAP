package com.shineunyoon.stockwatch;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;


public class AsyncLoaderTask extends AsyncTask<Stock, Void, String> {

    private MainActivity ma;
    private Stock stock;

    public AsyncLoaderTask(MainActivity ma) {
        this.ma = ma;
        Log.d("AsyncLoader: ","Constructor Done");
    }

    @Override
    protected String doInBackground(Stock... params) {

        Log.d("AsyncLoader ","doInBackground");
        stock = params[0];
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL("https://api.iextrading.com/1.0/stock/" + stock.getSymbol() + "/quote");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            Log.d("AsyncLoader ","doInBackground - is read");

            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        Log.d("AsyncLoader ","onPostExecute");
        parseJson(s);
        if (stock.getPrice() == 0) {
            return;
        } else {
            ma.addNewStock(stock);
        }
    }

    public void parseJson(String s){
        Log.d("AsyncLoader ","parseJson");
        Log.d("parseJson",""+s);
        try {
            JSONObject jStock = new JSONObject(s);

            DecimalFormat formater1 = new DecimalFormat("#.##");

            Double tmp1 = jStock.getDouble("latestPrice");
            String tmp2 = formater1.format(tmp1);
            stock.setPrice(Double.parseDouble(tmp2));


            tmp1 = jStock.getDouble("change");
            tmp2 = formater1.format(tmp1);
            stock.setPrice_change(Double.parseDouble(tmp2));

            tmp1 = jStock.getDouble("changePercent");
            tmp2 = formater1.format(tmp1);
            stock.setChange_percentage(Double.parseDouble(tmp2));

        } catch (JSONException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ma);
            builder.setTitle("Response Code: 404 Not Found");
            builder.setMessage("Symbol Does Not Exist");
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
}
