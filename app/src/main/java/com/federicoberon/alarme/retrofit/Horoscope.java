package com.federicoberon.alarme.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Horoscope {

    @SerializedName("date_range")
    @Expose
    private String dateRange;
    @SerializedName("current_date")
    @Expose
    private String currentDate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("compatibility")
    @Expose
    private String compatibility;
    @SerializedName("mood")
    @Expose
    private String mood;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("lucky_number")
    @Expose
    private String luckyNumber;
    @SerializedName("lucky_time")
    @Expose
    private String luckyTime;

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLuckyNumber() {
        return luckyNumber;
    }

    public void setLuckyNumber(String luckyNumber) {
        this.luckyNumber = luckyNumber;
    }

    public String getLuckyTime() {
        return luckyTime;
    }

    public void setLuckyTime(String luckyTime) {
        this.luckyTime = luckyTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Horoscope.class.getName()).append('@')
                .append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("dateRange");
        sb.append('=');
        sb.append(((this.dateRange == null)?"<null>":this.dateRange));
        sb.append(',');
        sb.append("currentDate");
        sb.append('=');
        sb.append(((this.currentDate == null)?"<null>":this.currentDate));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("compatibility");
        sb.append('=');
        sb.append(((this.compatibility == null)?"<null>":this.compatibility));
        sb.append(',');
        sb.append("mood");
        sb.append('=');
        sb.append(((this.mood == null)?"<null>":this.mood));
        sb.append(',');
        sb.append("color");
        sb.append('=');
        sb.append(((this.color == null)?"<null>":this.color));
        sb.append(',');
        sb.append("luckyNumber");
        sb.append('=');
        sb.append(((this.luckyNumber == null)?"<null>":this.luckyNumber));
        sb.append(',');
        sb.append("luckyTime");
        sb.append('=');
        sb.append(((this.luckyTime == null)?"<null>":this.luckyTime));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.luckyNumber == null)? 0 :this.luckyNumber.hashCode()));
        result = ((result* 31)+((this.mood == null)? 0 :this.mood.hashCode()));
        result = ((result* 31)+((this.color == null)? 0 :this.color.hashCode()));
        result = ((result* 31)+((this.dateRange == null)? 0 :this.dateRange.hashCode()));
        result = ((result* 31)+((this.currentDate == null)? 0 :this.currentDate.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.compatibility == null)? 0 :this.compatibility.hashCode()));
        result = ((result* 31)+((this.luckyTime == null)? 0 :this.luckyTime.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Horoscope) == false) {
            return false;
        }
        Horoscope horoscope = ((Horoscope) other);
        return (((((((((this.luckyNumber == horoscope.luckyNumber)
                ||((this.luckyNumber!= null)&&this.luckyNumber.equals(horoscope.luckyNumber)))
                &&((this.mood == horoscope.mood)||((this.mood!= null)
                &&this.mood.equals(horoscope.mood))))&&((this.color == horoscope.color)
                ||((this.color!= null)&&this.color.equals(horoscope.color))))
                &&((this.dateRange == horoscope.dateRange)||((this.dateRange!= null)
                &&this.dateRange.equals(horoscope.dateRange))))
                &&((this.currentDate == horoscope.currentDate)||((this.currentDate!= null)
                &&this.currentDate.equals(horoscope.currentDate))))
                &&((this.description == horoscope.description)||((this.description!= null)
                &&this.description.equals(horoscope.description))))
                &&((this.compatibility == horoscope.compatibility)||((this.compatibility!= null)
                &&this.compatibility.equals(horoscope.compatibility))))
                &&((this.luckyTime == horoscope.luckyTime)||((this.luckyTime!= null)
                &&this.luckyTime.equals(horoscope.luckyTime))));
    }

}