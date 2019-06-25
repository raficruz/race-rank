package com.gmail.raficruz.racerank.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author rafael-oliveira
 * 
 * Represents one lap completed by a pilot in the race 
 *
 */
@Data
@Builder
public class CompletedLap implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 193510537024541007L;

    private Pilot pilot;

    //Lap number
    private Integer number;
    //Time the pilot completed this lap
    private LocalTime completedAt;
    //How long has the pilot completed this lap
    private Duration completedIn;
    //Average speed in this lap
    private Double averageSpeed;
    
    private Boolean valid;
}
