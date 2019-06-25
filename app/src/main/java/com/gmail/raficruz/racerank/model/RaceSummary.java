package com.gmail.raficruz.racerank.model;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rafael-oliveira
 * 
 * Represents a summary of Race 
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceSummary {
    private Pilot winnerOfRace;
    private CompletedLap bestLapOfRace;
    private LocalTime raceEnding;
    
    private List<RacePilotStats> individualAchievements;
}
