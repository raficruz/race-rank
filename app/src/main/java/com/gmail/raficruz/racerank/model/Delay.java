package com.gmail.raficruz.racerank.model;

import java.io.Serializable;
import java.time.Duration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Delay implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 828665956989989928L;

    private Integer lapsBehind;
    private Duration timeBehind;
}
