package edu.gatech.cs6310;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PerformanceManager {
    private static PerformanceManager instance;
    private Map<String, Performance> functionRuntimeMap; // Map<function_name, {amount_invoked, total_duration}>
    private Instant startTime, endTime;

    public PerformanceManager() {
        this.functionRuntimeMap = new HashMap<>();
    }

    public static PerformanceManager getInstance() {
        if (instance == null) {
            instance = new PerformanceManager();
        }
        return instance;
    }

    public void startTracking() {
        this.startTime = Instant.now();
    }

    public void endTracking(String functionName) {
        this.endTime = Instant.now();
        Duration timeTaken = Duration.between(this.startTime, this.endTime);
        Long timeTakenNanos = timeTaken.toNanos();

        addToMap(functionName, timeTakenNanos);
    }

    private void addToMap(String functionName, Long timeTakenNanos) {
        Long numberInvoked = 0L;
        Long totalDuration = 0L;
        if (this.functionRuntimeMap.containsKey(functionName)) {
            Performance performance = this.functionRuntimeMap.get(functionName);
            numberInvoked = performance.getNumberInvoked();
            totalDuration = performance.getTotalDurationNanos();
        }
        Performance newPerformance =
                new Performance(numberInvoked + 1, totalDuration + timeTakenNanos);
        this.functionRuntimeMap.put(functionName, newPerformance);
    }

    public ErrorCode displayPerformance() {
        for (Map.Entry<String, Performance> entry : this.functionRuntimeMap.entrySet()) {
            System.out.println("function_name:" + entry.getKey() +
                    ". times_invoked: " + entry.getValue().getNumberInvoked() +
                    ". total_time_nanos: " + entry.getValue().getTotalDurationNanos() +
                    ". avg_time_nanos: " +
                    (entry.getValue().getTotalDurationNanos() / entry.getValue().getNumberInvoked()));
        }
        return ErrorCode.OK;
    }
}