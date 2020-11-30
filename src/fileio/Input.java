package fileio;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

import actor.ActorsAwards;
import command.Video;
import entertainment.Genre;

/**
 * The class contains information about input
 * <p>
 * DO NOT MODIFY
 */
public final class Input {
    /**
     * List of actors
     */
    private final List<ActorInputData> actorsData;
    /**
     * List of users
     */
    private final List<UserInputData> usersData;
    /**
     * List of commands
     */
    private final List<ActionInputData> commandsData;
    /**
     * List of movies
     */
    private final List<MovieInputData> moviesData;
    /**
     * List of serials aka tv shows
     */
    private final List<SerialInputData> serialsData;

    public Input() {
        this.actorsData = null;
        this.usersData = null;
        this.commandsData = null;
        this.moviesData = null;
        this.serialsData = null;
    }

    public Input(final List<ActorInputData> actors, final List<UserInputData> users,
                 final List<ActionInputData> commands,
                 final List<MovieInputData> movies,
                 final List<SerialInputData> serials) {
        this.actorsData = actors;
        this.usersData = users;
        this.commandsData = commands;
        this.moviesData = movies;
        this.serialsData = serials;
    }

    public List<ActorInputData> getActors() {
        return actorsData;
    }

    public List<UserInputData> getUsers() {
        return usersData;
    }

    public List<ActionInputData> getCommands() {
        return commandsData;
    }

    public List<MovieInputData> getMovies() {
        return moviesData;
    }

    public List<SerialInputData> getSerials() {
        return serialsData;
    }

    /**
     * Command actions
     */

    /**
     * Favorite command
     */
    public int favorite(final String title, final String username) {
        int cod = 0;
        int action = 0;
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                // caut userul cu usernameul cerut
                for (Map.Entry<String, Integer> history : user.getHistory().entrySet()) {
                    if (history.getKey().equals(title)) {
                        cod = 1; // videoul se afla in istoricul userului
                    }
                }
                if (cod == 1) {
                    cod = 0;
                    for (String favorite : user.getFavoriteMovies()) {
                        if (favorite.equals(title)) {
                            cod = 1; // videoul se afla deja in lista de favorite
                        }
                    }
                } else {
                    action = 1; // videoul nu a fost vazut
                    return action;
                }
                if (cod == 0) {
                    user.getFavoriteMovies().add(title);
                    action = 2; // videoul este adaugat la favorite
                    return action;
                }
                // action = 0 videoul se afla deja la favorite
            }
        }
        return action;
    }

    /**
     * View command
     */
    public int view(final String title, final String username) {
        int cod = 0;
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) { // caut userul respectiv
                for (Map.Entry<String, Integer> history : user.getHistory().entrySet()) {
                    if (history.getKey().equals(title)) {
                        cod = 1; // daca a fost vizionat deja o sa maresc contorul
                    }
                    if (cod == 1) {
                        history.setValue(history.getValue() + 1); // pun noul numar de vizionari
                        return history.getValue();
                    }
                }
                // videoul a fost vizionat pentru prima data
                user.getHistory().put(title, 1);
            }
        }
        return 1;
    }



     private Map<String, Map<String, ArrayList<Video>>> userRating =
            new HashMap<String, Map<String, ArrayList<Video>>>();
    // Video contine Integer number, Double grade
    // in acest map o sa retin toate ratingurile date de fiecare user pentru fiecare video

    /**
     * Rating command
     */

    public String commandRating(final String title, final String username,
                      final double grade, final int seasonNumber) {
        int cod = 0;
        Map<String, ArrayList<Video>> videoRating = new HashMap<String, ArrayList<Video>>();
        ArrayList<Video> ratings = new ArrayList<Video>();
        Map<Integer, Double> serialCheck = new HashMap<Integer, Double>();
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                for (Map.Entry<String, Integer> history : user.getHistory().entrySet()) {
                    if (history.getKey().equals(title)) { // am gasit titlul in history
                        cod = 1;
                        Video video = new Video(seasonNumber, grade);
                        // creez un obiect video cu numarul sezonului si nota
                        if (userRating.get(username) == null) {
                            // daca este prima aparitie a userului
                            userRating.put(username, videoRating);
                        }
                        videoRating = userRating.get(username); // initializez map-ul userului
                        if (videoRating.isEmpty()) { // daca userul nu a dat ratinguri
                            ratings.add(video);
                            videoRating.put(title, ratings); // il pun direct
                        } else {
                            if (videoRating.containsKey(title)) {
                                // daca gasesc titlul in lista de ratinguri
                                if (seasonNumber == 0) {
                                    // daca este film a fost deja rate-uit si ies
                                    return "error -> " + title + " has been already rated";
                                } else {
                                    // daca este serial caut daca
                                    // sezonul curent se afla deja in lista
                                    ratings = videoRating.get(title);
                                    for (int i = 0; i < ratings.size(); i++) {
                                        if (ratings.get(i).getNumber() == seasonNumber) {
                                            return "error -> " + title
                                                    + " has been already rated";
                                        }
                                    }
                                    ratings.add(video);
                                }
                            } else {
                                ratings.add(video);
                            }
                        }
                        videoRating.put(title, ratings);
                        userRating.put(username, videoRating);
                    }
                }
                if (cod == 0) {
                    return "error -> " + title + " is not seen";
                }
            }
        }
        return "success -> " + title + " was rated with " + grade + " by " + username;
    }
}
