package com.gmail.raficruz.racerank;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.gmail.raficruz.racerank.controller.RaceManager;
import com.gmail.raficruz.racerank.model.RacePilotStats;
import com.gmail.raficruz.racerank.model.vo.RaceLogEntry;
import com.gmail.raficruz.racerank.util.FileUtils;

public class RaceRank {

    public static void main(String[] args) {
        RaceManager manager = new RaceManager();

        List<RaceLogEntry> entries = FileUtils.processFile();

        for (RaceLogEntry entry : entries) {
            manager.registerLap(
                LocalTime.parse(entry.getLapCompletedAt(), DateTimeFormatter.ISO_LOCAL_TIME),
                entry.getPilotNumber(),
                entry.getPilotName(),
                Integer.valueOf(entry.getLapNumber()),
                FileUtils.getDurationFromString(entry.getLapDuration()),
                Double.parseDouble(entry.getAverageSpeed().replace(",", "."))
            );
        }
        List<RacePilotStats> raceStatistics = manager.proccessResult();
        manager.getBestLapOfEachPilot(raceStatistics);
        manager.getBestLapOfRace(raceStatistics);
        manager.getSpeedAverageOfEachPilot(raceStatistics);
        manager.getDelayToWinnerOfEachPilot(raceStatistics);
    }

}
