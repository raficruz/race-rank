package com.gmail.raficruz.racerank.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;
/**
 * 
 * @author rafael-oliveira
 *
 * Represents a Race pilot
 *
 */
@Data
@Builder
public class Pilot implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2820569914060559652L;

    //Pilot name
    private String name;

    //Pilot car's number
    private String number;    
}
