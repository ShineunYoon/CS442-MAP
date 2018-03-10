package com.shineunyoon.stockwatch;

/**
 * Created by shineunyoon on 2018. 2. 21..
 */

public class Stock {

    String symbol;
    String company_name;
    double price;
    double price_change;
    double change_percentage;

    // Constructor
    public Stock (String symbol, String company_name, double price, double price_change, double change_percentage) {

        this.symbol = symbol;
        this.company_name = company_name;
        this.price = price;
        this.price_change = price_change;
        this.change_percentage = change_percentage;

    }

    public Stock(String symbol, String company_name) {

        this.symbol = symbol;
        this.company_name = company_name;

    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice_change() {
        return price_change;
    }

    public void setPrice_change(double price_change) {
        this.price_change = price_change;
    }

    public double getChange_percentage() {
        return change_percentage;
    }

    public void setChange_percentage(double change_percentage) {
        this.change_percentage = change_percentage;
    }
}
