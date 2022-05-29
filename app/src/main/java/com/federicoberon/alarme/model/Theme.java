package com.federicoberon.alarme.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Pankaj on 03-11-2017.
 */

public class Theme {
    private int id;
    private int primaryColor;
    private int primaryDarkColor;
    private int accentColor;
    private Drawable background;

    public Theme(int primaryColor, int primaryDarkColor, int accentColor, Drawable background) {
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.accentColor = accentColor;
        this.background = background;
    }

    public Theme(int id , int primaryColor, int primaryDarkColor, int accentColor, Drawable background) {
        this.id = id;
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.accentColor = accentColor;
        this.background = background;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public void setPrimaryDarkColor(int primaryDarkColor) {
        this.primaryDarkColor = primaryDarkColor;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }
}
