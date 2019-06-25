package com.gmail.raficruz.racerank.model.vo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RaceLogEntry implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 3733816270245299321L;

    String lapCompletedAt;
    String pilot;
    String lapNumber;
    String lapDuration;
    String averageSpeed;
}
