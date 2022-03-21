package com.federicoberon.newapp.model;

/**
 * Simple pojo class to manage sign terms internally
 */
public class HoroscopeNames {
    private String name;
    private String name_in_url;
    private String name_in_url_two;
    private int iconId;

    public HoroscopeNames() {
    }

    /**
     * @param name Name
     * @param name_in_url Way to use it in a main API
     * @param name_in_url_two Way to use it in a second API
     */
    public HoroscopeNames(String name, String name_in_url, String name_in_url_two, int iconId) {
        this.name = name;
        this.name_in_url = name_in_url;
        this.name_in_url_two = name_in_url_two;
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_in_url() {
        return name_in_url;
    }

    public void setName_in_url(String name_in_url) {
        this.name_in_url = name_in_url;
    }

    public String getName_in_url_two() {
        return name_in_url_two;
    }

    public void setName_in_url_two(String name_in_url_two) {
        this.name_in_url_two = name_in_url_two;
    }
}
