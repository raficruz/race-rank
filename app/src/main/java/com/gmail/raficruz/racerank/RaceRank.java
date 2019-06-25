package com.gmail.raficruz.racerank;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.gmail.raficruz.racerank.controller.RaceManager;
import com.gmail.raficruz.racerank.model.RacePilotStats;
import com.gmail.raficruz.racerank.model.vo.RaceLogEntry;

public class RaceRank {

    public static void main(String[] args) {
        RaceManager manager = new RaceManager();

        List<RaceLogEntry> entries = readLogFile();

        for (RaceLogEntry entry : entries) {
            manager.registerLap(
                LocalTime.parse(entry.getLapCompletedAt(), DateTimeFormatter.ISO_LOCAL_TIME),
                entry.getPilot().substring(0, 3),
                entry.getPilot().substring(6),
                Integer.valueOf(entry.getLapNumber()),
                getDuration(entry.getLapDuration()),
                Double.parseDouble(entry.getAverageSpeed().replace(",", "."))
            );
        }
        List<RacePilotStats> raceStatistics = manager.proccessResult();
        manager.getBestLapOfEachPilot(raceStatistics);
        manager.getBestLapOfRace(raceStatistics);
        manager.getSpeedAverageOfEachPilot(raceStatistics);
        manager.getDelayToWinnerOfEachPilot(raceStatistics);
    }

    private static Duration getDuration(final String lapDuration){
        String[] lapTimeElements = lapDuration.replace(".", ":").split(":");

        return Duration.ofMinutes(Long.parseLong(lapTimeElements[0]))
            .plusSeconds(Long.parseLong(lapTimeElements[1]))
            .plusMillis(Long.parseLong(lapTimeElements[2]));
    }

    private static List<RaceLogEntry> readLogFile() {
        List<RaceLogEntry> entries = new ArrayList<>();

        entries.add(RaceLogEntry.builder().lapCompletedAt("23:49:08.277").pilot("038 – F.MASSA"      ).lapNumber("1").lapDuration("1:02.852").averageSpeed("44,275").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:49:10.858").pilot("033 – R.BARRICHELLO").lapNumber("1").lapDuration("1:04.352").averageSpeed("43,243").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:49:11.075").pilot("002 – K.RAIKKONEN"  ).lapNumber("1").lapDuration("1:04.108").averageSpeed("43,408").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:49:12.667").pilot("023 – M.WEBBER"     ).lapNumber("1").lapDuration("1:04.414").averageSpeed("43,202").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:49:30.976").pilot("015 – F.ALONSO"     ).lapNumber("1").lapDuration("1:18.456").averageSpeed("35,47" ).build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:50:11.447").pilot("038 – F.MASSA"      ).lapNumber("2").lapDuration("1:03.170").averageSpeed("44,053").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:50:14.860").pilot("033 – R.BARRICHELLO").lapNumber("2").lapDuration("1:04.002").averageSpeed("43,48" ).build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:50:15.057").pilot("002 – K.RAIKKONEN"  ).lapNumber("2").lapDuration("1:03.982").averageSpeed("43,493").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:50:17.472").pilot("023 – M.WEBBER"     ).lapNumber("2").lapDuration("1:04.805").averageSpeed("42,941").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:50:37.987").pilot("015 – F.ALONSO"     ).lapNumber("2").lapDuration("1:07.011").averageSpeed("41,528").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:51:14.216").pilot("038 – F.MASSA"      ).lapNumber("3").lapDuration("1:02.769").averageSpeed("44,334").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:51:18.576").pilot("033 – R.BARRICHELLO").lapNumber("3").lapDuration("1:03.716").averageSpeed("43,675").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:51:19.044").pilot("002 – K.RAIKKONEN"  ).lapNumber("3").lapDuration("1:03.987").averageSpeed("43,49" ).build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:51:21.759").pilot("023 – M.WEBBER"     ).lapNumber("3").lapDuration("1:04.287").averageSpeed("43,287").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:51:46.691").pilot("015 – F.ALONSO"     ).lapNumber("3").lapDuration("1:08.704").averageSpeed("40,504").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:52:01.796").pilot("011 – S.VETTEL"     ).lapNumber("1").lapDuration("3:31.315").averageSpeed("13,169").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:52:17.003").pilot("038 – F.MASS"       ).lapNumber("4").lapDuration("1:02.787").averageSpeed("44,321").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:52:22.586").pilot("033 – R.BARRICHELLO").lapNumber("4").lapDuration("1:04.010").averageSpeed("43,474").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:52:22.120").pilot("002 – K.RAIKKONEN"  ).lapNumber("4").lapDuration("1:03.076").averageSpeed("44,118").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:52:25.975").pilot("023 – M.WEBBER"     ).lapNumber("4").lapDuration("1:04.216").averageSpeed("43,335").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:53:06.741").pilot("015 – F.ALONSO"     ).lapNumber("4").lapDuration("1:20.050").averageSpeed("34,763").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:53:39.660").pilot("011 – S.VETTEL"     ).lapNumber("2").lapDuration("1:37.864").averageSpeed("28,435").build());
        entries.add(RaceLogEntry.builder().lapCompletedAt("23:54:57.757").pilot("011 – S.VETTEL"     ).lapNumber("3").lapDuration("1:18.097").averageSpeed("35,633").build());

        return entries;
    }
}
