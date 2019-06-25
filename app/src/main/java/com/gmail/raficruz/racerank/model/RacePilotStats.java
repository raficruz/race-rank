package com.gmail.raficruz.racerank.model;

import java.io.Serializable;
import java.time.Duration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RacePilotStats implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6109668497593261151L;

    private Pilot pilot;
    private Integer arrivalPosition;
    private Long completedLapsNumber;
    private Duration totalTimeSpent;
    private CompletedLap bestLap;
    private CompletedLap lastLapCompleted;
    private Double speedAverage;
    private Delay delayToWinner;
}
