package com.shineunyoon.assignment02;

/**
 * Created by shineunyoon on 2018. 1. 28..
 */

public class Notepad {
    private String savedTime;
    private String savedText;

    public String getSavedText() {
        return savedText;
    }
    public void setSavedText(String savedText) {
        this.savedText = savedText;
    }

    public String getSavedTime() {
        return savedTime;
    }
    public void setSavedTime(String savedTime) {
        this.savedTime = savedTime;
    }

    public String toString() {
        return savedTime + ": " + savedText;
    }
}
