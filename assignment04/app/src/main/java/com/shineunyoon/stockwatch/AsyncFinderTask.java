package com.shineunyoon.stockwatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
import java.util.ArrayList;

public class AsyncFinderTask extends AsyncTask<String, Void, String> {

    private MainActivity ma;
    private String address = "http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=";

    public AsyncFinderTask(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder sb = new StringBuilder();

        try {

            URL url = new URL(address + params[0]);
            Log.d("AsyncFinder: ", "parameter passed");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();


            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    protected void onPostExecute(String s) {

        Log.d("AsyncFinder: ","start onPostExecute");
        final ArrayList<Stock> selection = parseJson(s);

        AlertDialog.Builder builder = new AlertDialog.Builder(ma);
        final Stock[] stock = new Stock[1];
        // 5) Adding a stock – specified stock is not found
        if (selection.isEmpty()) {
            String obj3 = "";
            try {
                JSONObject obj1 = new JSONObject(s);
                JSONObject obj2 = obj1.getJSONObject("ResultSet");
                obj3 = obj2.getString("Query");

                Log.d("onPostExecute: ", ""+obj3);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            builder.setTitle("Symbol Not Found: " + obj3);
            builder.setMessage("Data for Stock Symbol");
            builder.create().show();
            return;
        }

        // 1) Adding a stock – when only one stock matched
        else if (selection.size() == 1) {
            stock[0] = selection.get(0);
            AsyncLoaderTask loader = new AsyncLoaderTask(ma);
            loader.execute(stock[0]);
        }

        // 2) Adding a stock – multiple stocks matched
        else {
            CharSequence[] stocks = new CharSequence[selection.size()];
            int i =0;
            for (Stock each : selection){
                CharSequence tmp = each.getSymbol()+" - "+each.getCompany_name();
                stocks[i] = tmp;
                i++;
            }

            builder.setTitle("Make a selection");
            builder.setItems(stocks, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stock[0] = selection.get(which);
                    AsyncLoaderTask loader = new AsyncLoaderTask(ma);
                    Log.d("stock[0] ",""+stock[0]);
                    loader.execute(stock[0]);
                }
            });

            builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.create().show();
        }
    }

    private ArrayList<Stock> parseJson(String s) {
        ArrayList<Stock> selection = new ArrayList<>();

        try {
            Log.d("AsyncFinder - parseJson ",""+s);
            JSONObject obj1 = new JSONObject(s);
            JSONObject obj2 = obj1.getJSONObject("ResultSet");
            JSONArray obj3 = obj2.getJSONArray("Result");

            for (int i =0; i<obj3.length(); i++){
                JSONObject jStock = (JSONObject) obj3.get(i);

                Log.d("AsyncFinder - parseJson ",""+jStock.getString("symbol"));
                if (jStock.getString("type").equals("S") && !(jStock.getString("symbol").contains("."))){
                    selection.add(new Stock(jStock.getString("symbol"), jStock.getString("name")));
                }
            }

            return selection;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
}