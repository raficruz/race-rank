package com.gmail.raficruz.racerank.controller;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.gmail.raficruz.racerank.model.CompletedLap;
import com.gmail.raficruz.racerank.model.Delay;
import com.gmail.raficruz.racerank.model.Pilot;
import com.gmail.raficruz.racerank.model.Race;
import com.gmail.raficruz.racerank.model.RacePilotStats;
import com.gmail.raficruz.racerank.model.RaceSummary;


public class RaceManager {

    private static final Logger LOGGER = Logger.getLogger( RaceManager.class.getName() );

    static {
        startTheRace();
    }

    private static Race race;

    /**
     * Record the laps of each rider, watching the total laps of the race and if the
     * winner has already crossed the finish line
     */
    public void registerLap(final LocalTime lapCompletedAt, final String pilotNumber, final String pilotName,
        final Integer lapNumber, final Duration lapDuration, final Double averageSpeed)
    {
        Pilot currentPilot = Pilot.builder().number(pilotNumber).name(pilotName).build();
        addOneLapToPilot(lapCompletedAt, lapNumber, lapDuration, averageSpeed, currentPilot);

    }

    private static void startTheRace()
    {
        // Check if exists a started race, if not starts one
        if (Objects.isNull(race)) {
            race = Race.builder().totalLaps(4).build();
        }
        // Check if exists registered laps
        if (race.getLaps() == null) {
            race.setLaps(new ArrayList<>());
        }
    }

    private void addOneLapToPilot(final LocalTime lapCompletedAt, final Integer lapNumber, final Duration lapDuration,
        final Double averageSpeed, Pilot pilot)
    {
        if (!isPilotStartedSpecifiedLap(race, lapNumber, pilot)) {
            Pilot p = getPilot(race, pilot);
            race.getLaps().add(CompletedLap.builder().completedAt(lapCompletedAt).number(lapNumber).pilot(p)
                    .completedIn(lapDuration).averageSpeed(averageSpeed).valid(Boolean.TRUE).build());
        }
    }

    /**
     * Checks if one pilot have started
     */
    private boolean isPilotStartedSpecifiedLap(final Race race, final Integer lapNumber, Pilot pilot)
    {
        return race.getLaps().stream().anyMatch(
            lap -> lap.getPilot().getNumber().equals(pilot.getNumber()) && lap.getNumber().equals(lapNumber));
    }

    /**
     * Get a registered pilot in race
     */
    private Pilot getPilot(final Race race, Pilot pilot)
    {
        Optional<CompletedLap> l = race.getLaps().stream()
            .filter(lap -> lap.getPilot().getNumber().equals(pilot.getNumber())).findAny();
        if (l.isPresent()) {
            return l.get().getPilot();
        }
        return pilot;
    }

    /**
     * Assemble the race result with the following information: Arrival Position,
     * Pilot Code, Pilot Name, Qt of Completed Lap and Total Time of Race.
     */
    public List<RacePilotStats> proccessResult() {
        List<RacePilotStats> pilotsStatistics = new ArrayList<>();
        RaceSummary raceSummary = new RaceSummary();

        processRaceEnding(raceSummary);

        //Grouping all laps completed by pilot
        race.getLaps().stream()
        .collect(Collectors.groupingBy(CompletedLap::getPilot))
        .forEach((p, l) -> {
            RacePilotStats pilotStats;

            //Set each lap started after the winner arrives as invalid because the race is finished
            l.stream()
            .forEach( v->
                v.setValid(v.getCompletedAt().minus(v.getCompletedIn()).isBefore(raceSummary.getRaceEnding())));

            if (pilotsStatistics.stream().noneMatch(s -> s.getPilot().getNumber().equals(p.getNumber()))) {
                pilotStats = RacePilotStats.builder().pilot(p).build();
                processCompletedLapsNumber(l, pilotStats);
                processLastLapCompleted(l, pilotStats);
                proccessSpeedAverageOfEachPilot(l, pilotStats);
                proccessTotalTimeSpentForEachPilot(l, pilotStats);
                processBestLapOfEachPilot(l, pilotStats);
                pilotsStatistics.add(pilotStats);
            }

        });

        //Process positions in race
        processPositions(pilotsStatistics);

        //Proccess the Race's winner
        Optional<RacePilotStats> winner = pilotsStatistics.stream()
            .sorted((s1, s2) -> s1.getArrivalPosition() - s2.getArrivalPosition())
            .findFirst();
        if(winner.isPresent()) {
            raceSummary.setWinnerOfRace(winner.get().getPilot());
        }

        //Proccess the fastest lap of each pilot
        raceSummary.setBestLapOfRace(processBestLapOfRace(race.getLaps()));

        //Proccess how many time each pilot arrived after the winner
        proccessDelayToWinnerOfEachPilot(pilotsStatistics, raceSummary, race.getTotalLaps());

        raceSummary.setIndividualAchievements(pilotsStatistics);

        showRaceResult(pilotsStatistics);
        showRaceSummary(raceSummary);
        return pilotsStatistics;
    }

    private void showRaceSummary(RaceSummary raceSummary) {
        System.out.println("======================== MELHOR COLTA DA CORRIDA =======================");
        System.out.println(
                "O vencedor da corrida foi "
                    +raceSummary.getWinnerOfRace().getNumber() + " - "
                    +raceSummary.getWinnerOfRace().getName());
        System.out.println(
            "A volta mais rápida foi volta "
            + raceSummary.getBestLapOfRace().getNumber()
            + " de "
            + raceSummary.getBestLapOfRace().getPilot().getNumber() + " - "
            + raceSummary.getBestLapOfRace().getPilot().getName()
            + " com o tempo de "
            +DurationFormatUtils.formatDurationHMS(raceSummary.getBestLapOfRace().getCompletedIn().toMillis())
        );
    }

    private void showRaceResult(List<RacePilotStats> raceStatistics) {
        System.out.println("========================= RESULTADO DA CORRIDA =========================");
        System.out.println("Posição\t\tPiloto\t\t\tVoltas\tTempo Total de Prova");
        raceStatistics.stream()
        .sorted((s1, s2) -> s1.getArrivalPosition() - s2.getArrivalPosition())
        .forEach( s -> {
            System.out.println(
                s.getArrivalPosition() + "\t\t"
                + s.getPilot().getNumber() + " - " + s.getPilot().getName() + "\t"
                + (s.getPilot().getName().length()<=8?"\t":"")
                + s.getCompletedLapsNumber() + "\t" +
                DurationFormatUtils.formatDurationHMS(s.getTotalTimeSpent().toMillis()));
        });
    }

    private void processRaceEnding(RaceSummary summary) {
        Optional <CompletedLap> winnerLap = race.getLaps().stream()
            .filter( l -> l.getNumber() == race.getTotalLaps())
            .sorted( (s1, s2) -> (s1.getCompletedAt().compareTo(s2.getCompletedAt())))
            .findFirst();

        if(winnerLap.isPresent()){
            summary.setRaceEnding(winnerLap.get().getCompletedAt());
        } else {
            summary.setRaceEnding(LocalTime.MIDNIGHT);
        }
    }

    private CompletedLap processBestLapOfRace(List<CompletedLap> l) {
        Optional<CompletedLap> lap =
            l.stream()
                .filter(CompletedLap::getValid)
                .sorted((s1,s2)-> s1.getCompletedIn().compareTo(s2.getCompletedIn()))
                .findFirst();
        if(lap.isPresent()) {
            return lap.get();
        }
        return CompletedLap.builder().build();
    }


    /**
     * Calculates the best lap of each pilot
     */
    public Map<Pilot, CompletedLap> getBestLapOfEachPilot(List<RacePilotStats> raceStatistics) {
        Map<Pilot, CompletedLap> individualsBestLaps = new HashMap<>();

        System.out.println("\n\n========================== MELHORES VOLTAS DE CADA PILOTO ==========================");

        raceStatistics.stream()
        .sorted( (s1, s2) -> (int) (s1.getBestLap().getCompletedIn().toMillis() - s2.getBestLap().getCompletedIn().toMillis()))
        .forEach( ps -> {

            individualsBestLaps.put(ps.getPilot(), ps.getBestLap());

            System.out.println("A melhor volta de " +
                    ps.getPilot().getNumber() + "-" +
                    ps.getPilot().getName() +
                    " foi " +
                    DurationFormatUtils.formatDurationHMS(ps.getBestLap().getCompletedIn().toMillis()) +
                    " e aconteceu na volta " +
                    ps.getBestLap().getNumber());
        });
        return individualsBestLaps;
    }

    /**
     * Calculate the best lap of the race
     */
    public RacePilotStats getBestLapOfRace(List<RacePilotStats> raceStatistics) {
        
        System.out.println("\n\n============================== MELHOR VOLTA DA CORRIDA =============================");

        Optional<RacePilotStats> bestLapOfRace = raceStatistics.stream()
            .sorted((s1,s2)-> s1.getBestLap().getCompletedIn().compareTo(s2.getBestLap().getCompletedIn()))
                .findFirst();

        if(bestLapOfRace.isPresent()) {
            System.out.println("A melhor volta da corrida foi "
                    + DurationFormatUtils.formatDurationHMS(bestLapOfRace.get().getBestLap().getCompletedIn().toMillis())
                    + " efetuado por "
                    + bestLapOfRace.get().getPilot().getNumber()
                    + "-"
                    + bestLapOfRace.get().getPilot().getName()
                    + " na volta "
                    + bestLapOfRace.get().getBestLap().getNumber()
                    + ".");

            return bestLapOfRace.get();
        }
        return RacePilotStats.builder().build();
    }

    /**
     * Calculates the average speed of each pilot during the entire race
     */
    public Map<Pilot, Double> getSpeedAverageOfEachPilot(List<RacePilotStats> raceStatistics) {
        Map<Pilot, Double> individualsSpeedAverage = new HashMap<>();

        System.out.println("\n\n======================== MEDIA DE VELOCIDADE DE CADA PILOTO ========================");

        raceStatistics.stream()
        .sorted((s1, s2) -> s1.getArrivalPosition() - s2.getArrivalPosition())
        .forEach( ps -> {
            individualsSpeedAverage.put(ps.getPilot(), ps.getSpeedAverage());
            System.out.println("A média de velocidade de "
                    + ps.getPilot().getNumber()
                    + "-"
                    + ps.getPilot().getName()
                    + " foi de "
                    + new DecimalFormat("0.00").format(ps.getSpeedAverage())
                    + " KM/H.");

        });
        return individualsSpeedAverage;
    }

    public Map<Pilot, Delay> getDelayToWinnerOfEachPilot(List<RacePilotStats> raceStatistics){
        Map<Pilot, Delay> individualsDelay = new HashMap<>();
        System.out.println("\n\n===================== Diferença entre cada piloto e o vencedor =====================");
        
        raceStatistics.stream()
        .sorted((s1, s2) -> s1.getArrivalPosition() - s2.getArrivalPosition())
        .forEach( ps -> {
            individualsDelay.put(ps.getPilot(), ps.getDelayToWinner());
            System.out.println(
                    ps.getArrivalPosition()
                    + " - "
                    + ps.getPilot().getNumber()
                    + "-"
                    + ps.getPilot().getName() + "\t"
                    + (ps.getPilot().getName().length()<8?"\t":"")
                    + " ==> "
                    + DurationFormatUtils.formatDurationHMS(ps.getDelayToWinner().getTimeBehind().toMillis())
                    + " "
                    + ps.getDelayToWinner().getLapsBehind()
                    + " voltas a atrás.");

        });
        
        return individualsDelay;
    }

    /**
     * Calculate how long each rider arrived after the winner
     */
    public void proccessDelayToWinnerOfEachPilot(List<RacePilotStats> raceStatistics,
        RaceSummary summary, Integer numberOfLaps) {

        raceStatistics
            .stream()
            .sorted((s1, s2) -> s1.getArrivalPosition() - s2.getArrivalPosition())
            .forEach( p ->
                p.setDelayToWinner(
                    Delay.builder()
                    .timeBehind(
                        Duration.of(ChronoUnit.MILLIS.between(summary.getRaceEnding(), 
                                    p.getLastLapCompleted().getCompletedAt()), ChronoUnit.MILLIS
                        )
                    )
                    .lapsBehind(numberOfLaps - p.getLastLapCompleted().getNumber())
                    .build()
                )
            );
    }

    private void processBestLapOfEachPilot(List<CompletedLap> l, RacePilotStats pilotStats) {
        Optional<CompletedLap> bestlap = 
                l.stream()
                .filter(CompletedLap::getValid)
                .sorted((s1, s2) -> (int) (s1.getCompletedIn().toMillis() - s2.getCompletedIn().toMillis()))
                .findFirst();

        if(bestlap.isPresent()) {
            pilotStats.setBestLap(bestlap.get());
        }
    }

    private void processLastLapCompleted(List<CompletedLap> l, RacePilotStats pilotStats) {
        Optional<CompletedLap> lastCompleted = l.stream()
        .filter(CompletedLap::getValid)
        .max((l1, l2) -> (l1.getNumber().compareTo(l2.getNumber())));

        pilotStats.setLastLapCompleted(lastCompleted.orElse(null));
    }

    private void processCompletedLapsNumber(List<CompletedLap> l, RacePilotStats pilotStats) {
        pilotStats.setCompletedLapsNumber(l.stream()
                .filter(CompletedLap::getValid)
                .count());
    }
    
    private void proccessSpeedAverageOfEachPilot(List<CompletedLap> l, RacePilotStats pilotStats) {
        pilotStats.setSpeedAverage(
                l.stream()
                .filter(CompletedLap::getValid)
                .mapToDouble(CompletedLap::getAverageSpeed).sum() / pilotStats.getCompletedLapsNumber());
    }
    
    private void proccessTotalTimeSpentForEachPilot(List<CompletedLap> l, RacePilotStats pilotStats) {
        pilotStats.setTotalTimeSpent(Duration.of(
                l.stream()
                .filter(CompletedLap::getValid)
                .mapToLong(cl -> cl.getCompletedIn().toMillis()).sum(),
                ChronoUnit.MILLIS));
    }
    
    private void processPositions(List<RacePilotStats> stats) {
        AtomicInteger counter = new AtomicInteger(0);
        stats.stream()
        .sorted((s1, s2) -> (int) (s1.getTotalTimeSpent().toMillis() - s2.getTotalTimeSpent().toMillis()))
        .forEach(x -> x.setArrivalPosition(counter.incrementAndGet()));
    }
}
