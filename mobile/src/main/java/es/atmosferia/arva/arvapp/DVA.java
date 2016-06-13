package es.atmosferia.arva.arvapp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DVA implements Serializable {

    private String dev_id;
    private double latitude;
    private double longitude;
    private String lastUpdate;

    //Constructors

    public DVA(){
        Calendar c = GregorianCalendar.getInstance();
        c.get(Calendar.HOUR_OF_DAY);
        this.lastUpdate = c.toString();
    }

    public DVA(String dev_id, double latitude, double longitude){
        Calendar c = GregorianCalendar.getInstance();
        c.get(Calendar.HOUR_OF_DAY);

        this.dev_id = dev_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdate = c.toString();
    }

    //Get methods

    public String getDev_id(){
        return dev_id;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getLastUpdate(){
        return lastUpdate;
    }

    //Set methods

    public void setDev_id(String dev_id){
        this.dev_id = dev_id;
        Calendar c = GregorianCalendar.getInstance();
        c.get(Calendar.HOUR_OF_DAY);
        this.lastUpdate = c.toString();
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
        Calendar c = GregorianCalendar.getInstance();
        c.get(Calendar.HOUR_OF_DAY);
        this.lastUpdate = c.toString();
    }

    public void setLongitude(double longitude){
         this.longitude = longitude;
         Calendar c = GregorianCalendar.getInstance();
         c.get(Calendar.HOUR_OF_DAY);
         this.lastUpdate = c.toString();
    }

    public void setLastUpdate(){
        Calendar c = GregorianCalendar.getInstance();
        c.get(Calendar.HOUR_OF_DAY);
        this.lastUpdate = c.toString();
    }
}
