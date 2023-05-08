package edu.gatech.cs6310;

import java.util.*;
import java.text.SimpleDateFormat;

public class Timestamp {

    public static final int DAY_START = 7;
    public static final int DAY_END = 19;

    private long secs;
    private int hour;
    
    public Timestamp(long secs) {
        this.secs = secs;
        this.updateHour();
    }

    public String toString() {
        Date date = new Date(this.secs * 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public boolean havePassed(Timestamp stamp) {
        return  this.secs >= stamp.secs;
    }

    public boolean havePassed(long secs) {
        return  this.secs >= secs;
    }

    public void incrementByMinutes(int minutes) {
        this.secs += minutes * 60;
        this.updateHour();
    }

    public boolean isDay() {
        return this.hour >= DAY_START && this.hour <= DAY_END;
    }

    private void updateHour() {
        Date date = new Date(this.secs * 1000);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
    }
}