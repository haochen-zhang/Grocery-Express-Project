package edu.gatech.cs6310;

public class Performance {
    private Long numberInvoked;
    private Long totalDurationNanos;

    public Performance(Long numberInvoked, Long totalDurationNanos) {
        this.numberInvoked = numberInvoked;
        this.totalDurationNanos = totalDurationNanos;
    }

    public Long getNumberInvoked() {
        return numberInvoked;
    }

    public void setNumberInvoked(Long numberInvoked) {
        this.numberInvoked = numberInvoked;
    }

    public Long getTotalDurationNanos() {
        return totalDurationNanos;
    }

    public void setTotalDurationNanos(Long totalDurationNanos) {
        this.totalDurationNanos = totalDurationNanos;
    }
}
