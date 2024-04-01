package com.example.archit_art_gallery;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ItemsData {
    String COLUMN_NAME;
    int COLUMN_HAS_CODE;
    int COLUMN_RATE;
    int COLUMN_QUANTITY;

    public ItemsData(String name, int has, int rate, int qty) {
        this.COLUMN_NAME = name;
        this.COLUMN_HAS_CODE = has;
        this.COLUMN_RATE = rate;
        this.COLUMN_QUANTITY = qty;
    }

    public ItemsData() {
        this.COLUMN_NAME = "";
        this.COLUMN_HAS_CODE = 0;
        this.COLUMN_RATE = 0;
        this.COLUMN_QUANTITY = 0;
    }

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public int getCOLUMN_HAS_CODE() {
        return COLUMN_HAS_CODE;
    }

    public void setCOLUMN_HAS_CODE(int COLUMN_HAS_CODE) {
        this.COLUMN_HAS_CODE = COLUMN_HAS_CODE;
    }

    public int getCOLUMN_RATE() {
        return COLUMN_RATE;
    }

    public void setCOLUMN_RATE(int COLUMN_RATE) {
        this.COLUMN_RATE = COLUMN_RATE;
    }

    public int getCOLUMN_QUANTITY() {
        return COLUMN_QUANTITY;
    }

    public void setCOLUMN_QUANTITY(int COLUMN_QUANTITY) {
        this.COLUMN_QUANTITY = COLUMN_QUANTITY;
    }
}
