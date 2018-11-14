package Model;

import android.util.Log;

public class Barista {
    private String name;
    private String AddressLine;
    private String contactNumber;
    private String open_Hours_Midweek;
    private String close_Hours_midweek;
    private String is_Open_Saturday;
    private String open_Hours_Saturday;
    private String close_hours_Saturday;
    private String is_Open_Sunday;
    private Float Rating;

    public Float getRating() {
        return Rating;
    }

    public void setRating(Float rating) {
        Rating = rating;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine() {
        return AddressLine;
    }

    public void setAddressLine(String addressLine) {
        AddressLine = addressLine;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOpen_Hours_Midweek() {
        return open_Hours_Midweek;
    }

    public void setOpen_Hours_Midweek(String open_Hours_Midweek) {
        this.open_Hours_Midweek = open_Hours_Midweek;
    }

    public String getClose_Hours_midweek() {
        return close_Hours_midweek;
    }

    public void setClose_Hours_midweek(String close_Hours_midweek) {
        this.close_Hours_midweek = close_Hours_midweek;
    }

    public String getIs_Open_Saturday() {
        return is_Open_Saturday;
    }

    public void setIs_Open_Saturday(String is_Open_Saturday) {
        this.is_Open_Saturday = is_Open_Saturday;
    }

    public String getOpen_Hours_Saturday() {
        return open_Hours_Saturday;
    }

    public void setOpen_Hours_Saturday(String open_Hours_Saturday) {
        this.open_Hours_Saturday = open_Hours_Saturday;
    }

    public String getClose_hours_Saturday() {
        return close_hours_Saturday;
    }

    public void setClose_hours_Saturday(String close_hours_Saturday) {
        this.close_hours_Saturday = close_hours_Saturday;
    }

    public String getIs_Open_Sunday() {
        return is_Open_Sunday;
    }

    public void setIs_Open_Sunday(String is_Open_Sunday) {
        this.is_Open_Sunday = is_Open_Sunday;
    }
}
