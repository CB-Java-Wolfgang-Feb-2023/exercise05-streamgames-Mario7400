package games.app;

import games.model.Game;
import games.model.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Games {

    private static final Path CSV = Paths.get("Games/games.csv");
    private static final String BUNDESLIGA = "BUNDESLIGA";
    private static final String BAYERN = "FC Bayern Muenchen";

    public static void main(String[] args) throws IOException {

        List<Game> games = null;
        try (Stream<String> lines = Files.lines(CSV)) {
            games = lines.skip(1).map(Game::fromString).collect(toList());
        } catch (Exception e) {
            throw new NoSuchElementException("No CSV file found");
        }

        games.forEach(System.out::println);
        System.out.println();

        // -------------------

        // TODO: Wie viele Spiele sind Bundesliga Spiele?
        // (Lösung mit filter)

        long bundesligaGameCount = games.stream()
                .filter(value -> value.getInfo().startsWith(BUNDESLIGA))
                .count();

        System.out.println("There were " + bundesligaGameCount + " Bundesliga games");
        System.out.println();

        // -------------------

        // TODO: Welche Spiele sind Auswärts- und welche Heimspiele?
        // (Lösung mit partitionBy)

        Map<Boolean, List<Game>> homeAwayMap = games.stream()
                .collect(Collectors.partitioningBy(value -> value.getHome().equals(BAYERN)));

        System.out.println("*** HOME ***");
        homeAwayMap.get(true).forEach(System.out::println);
        System.out.println("*** AWAY ***");
        homeAwayMap.get(false).forEach(System.out::println);
        System.out.println();

        // -------------------

        // TODO Gruppiere die Spiele in won, lost und draw (draw = Unentschieden)
        // (Lösung mit groupingBy)

        Map<Result, List<Game>> wonLostDrawMap = games.stream()
                .collect(Collectors.groupingBy(value -> {
                    if(value.getHomeGoals() > value.getAwayGoals()){
                        return Result.WON;
                    } else if (value.getAwayGoals() > value.getHomeGoals()) {
                        return Result.LOST;
                    } else {
                        return Result.DRAW;
                    }
                }));

        System.out.println("*** WON ***");
        wonLostDrawMap.get(Result.WON).forEach(System.out::println);
        System.out.println("*** DRAW ***");
        wonLostDrawMap.get(Result.DRAW).forEach(System.out::println);
        System.out.println("*** LOST ***");
        wonLostDrawMap.get(Result.LOST).forEach(System.out::println);
        System.out.println();

        // -------------------

        // TODO Wie viele Tore wurden im Durchschnitt pro Spiel erzielt? mapToInt
        // (Lösung mit mapToInt)
        double avgGoalsPerGame1 = games.stream()
                .mapToInt(value -> value.getAwayGoals()+ value.getHomeGoals())
                .average()
                .orElse(0.0);

        System.out.printf("Average goals per game: %.2f\n", avgGoalsPerGame1);

        // TODO Wie viele Tore wurden im Durchschnitt pro Spiel erzielt? averagingDouble
        // (Lösung mit withCollectors.averagingDouble)
        double avgGoalsPerGame2 = games.stream()
                .collect(Collectors.averagingDouble(value -> value.getAwayGoals()+value.getHomeGoals()));

        System.out.printf("Average goals per game: %.2f\n", avgGoalsPerGame2);
        System.out.println();

        // -------------------

        // TODO Wie viele Spiele hat Bayern München zu Hause gewonnen?
        // (home equals BAYERN)?
        // (Lösung mit double filter und count)
        long wonHomeGamesCount = games.stream()
                .filter(value -> value.getHome().equals(BAYERN) && value.getHomeGoals() > value.getAwayGoals())
                .count();

        System.out.println(BAYERN + " won " + wonHomeGamesCount + " games at home");
        System.out.println();

        // -------------------

        // TODO Was war das Spiel mit den wenigsten Toren? sorted findFirst
        // (Lösung mit sorted und findFirst)
        Optional<Game> leastNumberOfGoalsGame1 = games.stream()
                .sorted(Comparator.comparingInt(value -> value.getAwayGoals()+ value.getHomeGoals()))
                .findFirst();

        System.out.println("Game with least number of goals: " + leastNumberOfGoalsGame1);

        // TODO Was war das Spiel mit den wenigsten Toren? min Comparator.comparingInt
        // (Lösung mit min und Comparator.comparingInt)
        Optional<Game> leastNumberOfGoalsGame2 = games.stream()
                .min(Comparator.comparingInt(value -> value.getHomeGoals() + value.getAwayGoals()));

        System.out.println("Game with least number of goals: " + leastNumberOfGoalsGame2);
        System.out.println();

        // -------------------

        // TODO Welche unterschiedlichen (distinct) Startzeiten gibt es?
        // (Lösung mit einem stream und Collectors.joining)
        String startingTimesString = games.stream()
                .map(value -> value.getTime())
                .distinct()
                .collect(Collectors.joining(System.lineSeparator()));

        System.out.println("Distinct starting times: " + startingTimesString);
        System.out.println();

        // -------------------

        // TODO hat Bayern ein Auswärtsspiel mit mindestens 2 Toren Unterschied gewonnen?
        // (home equals BAYERN)?
        // (Lösung mit anyMatch)

        boolean bayernWon = games.stream()
                .filter(value -> value.getAway().equals(BAYERN))
                .anyMatch(value -> (value.getHomeGoals()-value.getAwayGoals()) >=2 || (value.getAwayGoals()-value.getHomeGoals()) >=2);

        System.out.println("Bayern won away game with at least 2 goals difference: " + (bayernWon ? "yes" : "no"));
        System.out.println();

        // -------------------

        // TODO Ein Freund von dir gab dir die Spiele von 2019, die jedoch nach der Heimmannschaft gruppiert wurden. Du möchtest aber alle Spiele als einfache Liste abrufen!
        // (Lösung with flatMap und Collectors.toList)
        Map<String, List<Game>> games2019ByHomeTeam = games.stream()
                .filter(game -> game.getDate().contains("2019"))
                .collect(Collectors.groupingBy(Game::getHome));
        List<Game> flattenedGames = games2019ByHomeTeam.values().stream()
                .flatMap(value -> value.stream())
                .collect(Collectors.toList());

        flattenedGames.forEach(System.out::println);
    }
}
