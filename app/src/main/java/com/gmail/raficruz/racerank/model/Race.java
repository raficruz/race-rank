package com.gmail.raficruz.racerank.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author rafael-oliveira
 *
 * Represents a race as set of pilots doing laps
 *
 */
@Data
@Builder
public class Race implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6069476254278339855L;

    // Maximum of laps a pilot can complete
    private Integer totalLaps;
    
    // All pilots in this race
    private List<CompletedLap> laps;
    
    private boolean finished;
}
