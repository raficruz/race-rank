/*
* Copyright(c) by VR Benefícios
*
* All rights reserved.
*
* This software is confidential and proprietary information of
* VR Benefícios ("Confidential Information").
* You shall not disclose such Confidential Information and shall
* use it only in accordance with the terms of the license agreement
* you entered with VR Benefícios.
*/
package com.gmail.raficruz.racerank.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.gmail.raficruz.racerank.model.vo.RaceLogEntry;

public class FileUtils {

    private static final int LAP_COMPLETED_AT_COLUMN=0;
    private static final int PILOT_NUMBER_COLUMN=1;
    private static final int PILOT_NAME_COLUMN=2;
    private static final int LAP_NUMBER_COLUMN=3;
    private static final int LAP_DURATION_COLUMN=4;
    private static final int AVERAGE_SPEED_COLUMN=5;

    private static final Logger LOGGER = Logger.getLogger( FileUtils.class.getName() );
    
    public static List<RaceLogEntry> processFile() {
        String line = null;
        List<RaceLogEntry> entries = new ArrayList<>();

        try(InputStream in = new FileInputStream(new File("src/main/resources/lap-entries.txt"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in)))
        {
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                entries.add(processLine(line));
            }
        }catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Não foi possível processar o arquivo de logs", e);
        }
        return entries;
    }

    private static RaceLogEntry processLine(String line) {
        String[] lapData = line.split("[\\s]");
        List<String> fileColumnValue = new ArrayList<>();

        for (String data : lapData) {
            String columnValue = data.trim().replace("–", "");
            if(!StringUtils.isBlank(columnValue)) {
                fileColumnValue.add(columnValue); 
            }
        }
        return RaceLogEntry.builder()
            .averageSpeed(fileColumnValue.get(AVERAGE_SPEED_COLUMN))
            .lapCompletedAt(fileColumnValue.get(LAP_COMPLETED_AT_COLUMN))
            .lapDuration(fileColumnValue.get(LAP_DURATION_COLUMN))
            .lapNumber(fileColumnValue.get(LAP_NUMBER_COLUMN))
            .pilotNumber(fileColumnValue.get(PILOT_NUMBER_COLUMN))
            .pilotName(fileColumnValue.get(PILOT_NAME_COLUMN))
        .build();
    }

    public static Duration getDurationFromString(final String lapDuration){
        String[] lapTimeElements = lapDuration.replace(".", ":").split(":");

        return Duration.ofMinutes(Long.parseLong(lapTimeElements[0]))
            .plusSeconds(Long.parseLong(lapTimeElements[1]))
            .plusMillis(Long.parseLong(lapTimeElements[2]));
    }
}
