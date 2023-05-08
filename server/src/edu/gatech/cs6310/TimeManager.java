package edu.gatech.cs6310;

public class TimeManager { // TODO null and empty handling?

    // Fields
    private static TimeManager instance;
    private Timestamp currentTime;

    public TimeManager(Timestamp currentTime) {
        this.currentTime = currentTime;
    }

    public static TimeManager generateInstance(Timestamp stamp) {
        instance = new TimeManager(stamp);
        return instance;
    }

    public static TimeManager getInstance() {
        return instance;
    }

    public Timestamp getTimestamp() {
        return this.currentTime;
    }

    public void incrementTimeByMinutes(int i) {
        this.currentTime.incrementByMinutes(i);
    }

    public void setTime(Timestamp newTime) {
        this.currentTime = newTime;
    }
}