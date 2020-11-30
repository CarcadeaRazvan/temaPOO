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
    private Map<String, Map<String, Double>> eachRating
            = new HashMap<String, Map<String, Double>>();
    // aici retin ratingul final al fiecarui video pe care utilizatorul l-a dat
    // Map<String, Double> ratingMap = new HashMap<String, Double>();

    /**
     * Average query
     */

    public String average(final int number, final String sortType) {
        int i = 0;
        ArrayList<String> query = new ArrayList<String>();
        Map<String, ArrayList<String>> castMap = new HashMap<String, ArrayList<String>>();
        for (String name : userRating.keySet()) {
            Map<String, ArrayList<Video>> videos = userRating.get(name);
            ArrayList<ArrayList<Video>> each = new ArrayList<>();
            Map<String, Double> videoRating = new HashMap<String, Double>();
            for (String video : videos.keySet()) {
                each.add(0, videos.get(video));
                Integer sez = videos.get(video).get(0).getNumber();
                // verific daca videoul este serial sau nu comparand numarul sezoanelor cu 0
                // daca este mai mare inseamna ca este serial
                int seasonNumber = 0;
                if (sez != 0) {
                    for (int k = 0; k < serialsData.size(); k++) {
                        if (serialsData.get(k).getTitle().equals(video)) {
                            seasonNumber = serialsData.get(k).getNumberSeason();
                        }
                    }
                }
                for (int j = 0; j < videos.get(video).size(); j++) {
                    videos.get(video).get(j).setSeasonNumber(seasonNumber);
                }
                Double sum = 0.0;
                for (int j = 0; j < videos.get(video).size(); j++) {
                    sum += videos.get(video).get(j).getGrade();
                    // calculez suma tuturor ratingurilor din lista unui titlu
                }
                if (videos.get(video).get(0).getSeasonNumber() != 0) {
                    // daca are numar de sezoane >0 este serial
                    // si impart la numarul total de sezoane
                    sum /= seasonNumber;
                }
                videoRating.put(video, sum);
            }
            eachRating.put(name, videoRating);
        }
        Map<String, Double> finalRating = new HashMap<String, Double>();
        for (i = 0; i < moviesData.size(); i++) {
            String currentTitle = moviesData.get(i).getTitle();
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            // pentru filme calculez suma notelor date de toti userii pentru filmul respectiv
            for (String user : eachRating.keySet()) {
                for (String mapTitle : eachRating.get(user).keySet()) {
                    if (mapTitle.equals(currentTitle)) {
                        cod = 1;
                        appearence++;
                        sum += eachRating.get(user).get(mapTitle);
                    }
                }
            }
            if (cod == 1) {
                castMap.put(currentTitle, moviesData.get(i).getCast());
                finalRating.put(currentTitle, sum / appearence);
            }
        }
        for (i = 0; i < serialsData.size(); i++) {
            String currentTitle = serialsData.get(i).getTitle();
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            // pentru seriale calculez ce rating a dat fiecare user pentru serialul respectiv
            // considerand sezoanele fara rating avand rating 0
            for (String user : eachRating.keySet()) {
                for (String mapTitle : eachRating.get(user).keySet()) {
                    if (mapTitle.equals(currentTitle)) {
                        cod = 1;
                        appearence++;
                        sum += eachRating.get(user).get(mapTitle);
                    }
                }
            }
            if (cod == 1) {
                castMap.put(currentTitle, serialsData.get(i).getCast());
                finalRating.put(currentTitle, sum / appearence);
            }
        }
        Map<String, Double> actorMap = new HashMap<String, Double>();
        for (i = 0; i < actorsData.size(); i++) {
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            // calculez media fiecarui actor din toate videourile in care a participat
            for (String video : finalRating.keySet()) {
                for (String name : castMap.get(video)) {
                    if (actorsData.get(i).getName().equals(name)) {
                        cod = 1;
                        appearence++;
                        sum += finalRating.get(video);
                    }
                }
            }
            // daca a aparut in vreun video ii pun ratingul total in map
            if (cod == 1) {
                actorMap.put(actorsData.get(i).getName(), sum / appearence);
            }
        }
        // sortez in functie de sortType map-ul initial dupa rating, iar apoi dupa nume
        Map<String, Double> firstN;
        if (sortType.equals("desc")) {
            Map<String, Double> sortedActors =
                    actorMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                            .limit(actorMap.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
            firstN =
                    sortedActors.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            Map<String, Double> sortedActors =
                    actorMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .limit(actorMap.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
            firstN =
                    sortedActors.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        for (String name : firstN.keySet()) {
            query.add(name);
        }
        return "Query result: " + query.toString();
    }

    /**
     * Awards query
     */

    public String awards(final int number, final List<String> filters,
                         final String sortType) {
        ArrayList<String> actors = new ArrayList<String>();
        ArrayList<ActorsAwards> filterAwards = new ArrayList<ActorsAwards>();
        Map<String, Integer> awardsNumber = new HashMap<String, Integer>();
        // parcurg toti actorii si caut premiile in awards-urile fiecaruia
        for (int i = 0; i < actorsData.size(); i++) {
            int cod = 1;
            int sum = 0;
            String desctiption = null;
            // presupun ca initial ar contine toate filtrele
            // iar daca nu gasesc un cuvant fac cod-ul 0
            for (String award : filters) {
                desctiption = actorsData.get(i).getAwards().toString();
                if (desctiption.indexOf(award) < 0) {
                    cod = 0;
                }
            }
            if (cod == 1) {
                // inseamna ca actorul are toate awards-urile cerute si fac suma
                // tuturor premiilor primite de actor
                for (ActorsAwards actorAward : actorsData.get(i).getAwards().keySet()) {
                    sum += actorsData.get(i).getAwards().get(actorAward);
                }
                actors.add(actorsData.get(i).getName());
                awardsNumber.put(actorsData.get(i).getName(), sum);
            }
        }
        actors.clear();
        Map<String, Integer> sortedAwards;
        // sortez in functie de sortType initial dupa numar, iar apoi dupa nume
        if (sortType.equals("desc")) {
            Map<String, Integer> sortedAlfa =
                    awardsNumber.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedAwards =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            Map<String, Integer> sortedAlfa =
                    awardsNumber.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedAwards =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        for (String s : sortedAwards.keySet()) {
            actors.add(s);
        }
        return "Query result: " + actors.toString();
    }

    /**
     * Filter Description query
     */

    public String filterDescription(final List<String> filters, final String sortType) {
        ArrayList<String> actors = new ArrayList<String>();
        for (int i = 0; i < actorsData.size(); i++) {
            // parcurg toti actorii si caut in descrierea fiecaruia toate keywords-urile cerute
            String description = actorsData.get(i).getCareerDescription();
            int cod = 1;
            String[] words = description.split("\\W+");
            // sparg descrierea in cuvinte pentru a putea cauta prin ele
            // presupun din nou ca ar contine toate cuvintele
            for (String word : filters) {
                int ok = 0;
                for (int j = 0; j < words.length; j++) {
                    if (words[j].equalsIgnoreCase(word)) {
                        ok = 1;
                    }
                }
                // daca nu gasesc un cuvant fac cod-ul 0
                if (ok == 0) {
                    cod = 0;
                }
            }
            if (cod == 1) {
                // daca am ajuns aici inseamna ca actorul are in descriere toate cuvintele cerute
                actors.add(actorsData.get(i).getName());
            }
        }
        Collections.sort(actors);
        // sortez vectorul, iar daca sortType este descrescator intru pe if-ul urmator
        if (sortType.equals("desc")) {
            Collections.reverse(actors);
        }
        return "Query result: " + actors.toString();
    }
    private Map<String, Map<String, Double>> allRatings
            = new HashMap<String, Map<String, Double>>();

    /**
     * Rating query
     */

    public String ratingQuery(final int number, final String sortType,
                              final String objectType, final List<String> year,
                              final List<String> genres) {
        int i = 0;
        ArrayList<String> query = new ArrayList<String>();
        ArrayList<Video> avg = new ArrayList<Video>();
        // aici o sa calculez din nou toate rating-urile videourilor ca la comanda rating
        for (String name : userRating.keySet()) {
            Map<String, ArrayList<Video>> videos = userRating.get(name);
            ArrayList<ArrayList<Video>> each = new ArrayList<>();
            Map<String, Double> videoRating = new HashMap<String, Double>();
            for (String video : videos.keySet()) {
                each.add(0, videos.get(video));
                Integer sez = videos.get(video).get(0).getNumber();
                int seasonNumber = 0;
                if (sez != 0) {
                    for (int k = 0; k < serialsData.size(); k++) {
                        if (serialsData.get(k).getTitle().equals(video)) {
                            seasonNumber = serialsData.get(k).getNumberSeason();
                        }
                    }
                }
                for (int j = 0; j < videos.get(video).size(); j++) {
                    videos.get(video).get(j).setSeasonNumber(seasonNumber);
                }
                Double sum = 0.0;
                for (int j = 0; j < videos.get(video).size(); j++) {
                    sum += videos.get(video).get(j).getGrade();
                    // calculez suma tuturor ratingurilor din lista unui titlu
                }
                if (videos.get(video).get(0).getSeasonNumber() != 0) {
                    // daca are numar de sezoane >0 este serial
                    // si impart la numarul total de sezoane
                    sum /= seasonNumber;
                }
                videoRating.put(video, sum);
            }
            allRatings.put(name, videoRating);
        }
        Map<String, Double> finalMovieRating = new HashMap<String, Double>();
        Map<String, Double> finalShowRating = new HashMap<String, Double>();
        // calculez ratingul pe seriale si pe filme
        if (objectType.equals("movies")) {
            for (i = 0; i < moviesData.size(); i++) {
                String currentTitle = moviesData.get(i).getTitle();
                int appearence = 0;
                Double sum = 0.0;
                int cod = 0;
                for (String user : allRatings.keySet()) {
                    for (String mapTitle : allRatings.get(user).keySet()) {
                        if (mapTitle.equals(currentTitle)) {
                            cod = 1;
                            appearence++;
                            sum += allRatings.get(user).get(mapTitle);
                        }
                    }
                }
                if (year.get(0) != null) {
                    if (Integer.parseInt(year.get(0).toString())
                            != moviesData.get(i).getYear()) {
                        cod = 0;
                    }
                }
                for (String genre1 : genres) {
                    int okGenre = 0;
                    for (String genre2 : moviesData.get(i).getGenres()) {
                        if (genre2.equals(genre1)) {
                            okGenre = 1;
                        }
                    }
                    if (okGenre == 0) {
                        cod = 0;
                    }
                }
                if (cod == 1) {
                    finalMovieRating.put(currentTitle, sum / appearence);
                }
            }
        } else {
            for (i = 0; i < serialsData.size(); i++) {
                String currentTitle = serialsData.get(i).getTitle();
                int appearence = 0;
                Double sum = 0.0;
                int cod = 0;
                for (String user : allRatings.keySet()) {
                    for (String mapTitle : allRatings.get(user).keySet()) {
                        if (mapTitle.equals(currentTitle)) {
                            cod = 1;
                            appearence++;
                            sum += allRatings.get(user).get(mapTitle);
                        }
                    }
                }
                if (year.get(0) != null) {
                    if (Integer.parseInt(year.get(0).toString())
                            != serialsData.get(i).getYear()) {
                        cod = 0;
                    }
                }
                int okGenre = 0;
                for (String genre1 : genres) {
                    okGenre = 0;
                    for (String genre2 : serialsData.get(i).getGenres()) {
                        if (genre2.equals(genre1)) {
                            okGenre = 1;
                        }
                    }
                    if (okGenre == 0) {
                        cod = 0;
                    }
                }
                if (cod == 1) {
                    finalShowRating.put(currentTitle, sum / appearence);
                }
            }
        }
        // daca query-ul a fost aplicat pe filme pun in query doar filmele, altfel serialele
        if (objectType.equals("movies")) {
            for (String title : finalMovieRating.keySet()) {
                query.add(title);
            }
        } else {
            for (String title : finalShowRating.keySet()) {
                query.add(title);
            }
        }
        Collections.sort(query);
        if (sortType.equals("desc")) {
            Collections.reverse(query);
        }
        return "Query result: " + query.toString();
    }

    /**
     * Favorite query
     */

    public String favoriteQuery(final int number, final String sortType,
                                final String objectType, final List<String> year,
                                final List<String> genres) {
        Map<String, Integer> favoriteNumber = new HashMap<String, Integer>();
        for (UserInputData user : usersData) {
            for (String favorite : user.getFavoriteMovies()) {
                // calculez pentru fiecare video toate adaugarile la favorite
                // de la toti utilizatorii
                if (objectType.equals("movies")) {
                    for (int i = 0; i < moviesData.size(); i++) {
                        if (favorite.equals(moviesData.get(i).getTitle())) {
                            int cod = 1;
                            if (genres.get(0) != null) {
                                for (String genre1 : genres) {
                                    int found = 0;
                                    for (String genre2 : moviesData.get(i).getGenres()) {
                                        if (genre1.equals(genre2)) {
                                            found = 1;
                                        }
                                    }
                                    if (found == 0) {
                                        cod = 0;
                                    }
                                }
                            }
                            if (year.get(0) != null) {
                                if (Integer.parseInt(year.get(0).toString())
                                        != moviesData.get(i).getYear()) {
                                    cod = 0;
                                }
                            }
                            if (cod == 1) {
                                // daca am ajuns aici inseamna ca videoul respecta criteriile
                                int num;
                                if (favoriteNumber.get(favorite) != null) {
                                    Integer value = favoriteNumber.get(favorite);
                                    num = value + 1;
                                } else {
                                    num = 1;
                                }
                                favoriteNumber.put(favorite, num);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < serialsData.size(); i++) {
                        if (favorite.equals(serialsData.get(i).getTitle())) {
                            int cod = 1;
                            if (genres.get(0) != null) {
                                for (String genre1 : genres) {
                                    int found = 0;
                                    for (String genre2 : serialsData.get(i).getGenres()) {
                                        if (genre1.equals(genre2)) {
                                            found = 1;
                                        }
                                    }
                                    if (found == 0) {
                                        cod = 0;
                                    }
                                }
                            }
                            if (year.get(0) != null) {
                                if (Integer.parseInt(year.get(0).toString())
                                        != serialsData.get(i).getYear()) {
                                    cod = 0;
                                }
                            }
                            if (cod == 1) {
                                int num;
                                if (favoriteNumber.get(favorite) != null) {
                                    Integer value = favoriteNumber.get(favorite);
                                    num = value + 1;
                                } else {
                                    num = 1;
                                }
                                favoriteNumber.put(favorite, num);
                            }
                        }
                    }
                }
            }
        }
        Map<String, Integer> sortedFavorites;
        // sortez map-ul in functie de sortType
        if (sortType.equals("desc")) {
            Map<String, Integer> sortedAlfa =
                    favoriteNumber.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedFavorites =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            Map<String, Integer> sortedAlfa =
                    favoriteNumber.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedFavorites =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> query = new ArrayList<String>();
        for (String name : sortedFavorites.keySet()) {
            query.add(name);
        }
        return "Query result: " + query;
    }

    /**
     * Longest query
     */

    public String longest(final int number, final String sortType,
                          final String objectType, final List<String> year,
                          final List<String> genres) {
        Map<String, Integer> duration = new HashMap<String, Integer>();
        if (objectType.equals("movies")) {
            // pentru toate filmele calculez durata totala a acestora
            for (int i = 0; i < moviesData.size(); i++) {
                int cod = 1;
                if (genres.get(0) != null) {
                    for (String genre1 : genres) {
                        int found = 0;
                        for (String genre2 : moviesData.get(i).getGenres()) {
                            if (genre1.equals(genre2)) {
                                found = 1;
                            }
                        }
                        if (found == 0) {
                            cod = 0;
                        }
                    }
                }
                if (year.get(0) != null) {
                    if (Integer.parseInt(year.get(0).toString())
                            != moviesData.get(i).getYear()) {
                        cod = 0;
                    }
                }
                if (cod == 1) {
                    duration.put(moviesData.get(i).getTitle(),
                            moviesData.get(i).getDuration());
                }
            }
        } else {
            for (int i = 0; i < serialsData.size(); i++) {
                // pentru fiecare serial calculez durata totala
                int cod = 1;
                if (genres.get(0) != null) {
                    for (String genre1 : genres) {
                        int found = 0;
                        for (String genre2 : serialsData.get(i).getGenres()) {
                            if (genre1.equals(genre2)) {
                                found = 1;
                            }
                        }
                        if (found == 0) {
                            cod = 0;
                        }
                    }
                }
                if (year.get(0) != null) {
                    if (Integer.parseInt(year.get(0).toString())
                            != serialsData.get(i).getYear()) {
                        cod = 0;
                    }
                }
                if (cod == 1) {
                    int total = 0;
                    for (int j = 0; j < serialsData.get(i).getNumberSeason(); j++) {
                        total += serialsData.get(i).getSeasons().get(j).getDuration();
                    }
                    duration.put(serialsData.get(i).getTitle(), total);
                }
            }
        }
        ArrayList<String> query = new ArrayList<String>();
        Map<String, Integer> sortedLongest;
        // sortez map-ul in functie de sortType
        if (sortType.equals("desc")) {
            Map<String, Integer> sortedAlfa =
                    duration.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                            .limit(duration.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedLongest =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            Map<String, Integer> sortedAlfa =
                    duration.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .limit(duration.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedLongest =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        for (String name : sortedLongest.keySet()) {
            query.add(name);
        }
        return "Query result: " + query;
    }

    /**
     * Most viewed query
     */

    public String mostViewed(final int number, final String sortType,
                             final String objectType, final List<String> year,
                             final List<String> genres) {
        Map<String, Integer> viewedNumber = new HashMap<String, Integer>();
        for (UserInputData user : usersData) {
            // pentru fiecare user parcurg history-ul acestuia si calculez numarul
            // total de vizionari pentru fiecare video in parte
            for (String name : user.getHistory().keySet()) {
                if (objectType.equals("movies")) {
                    for (int i = 0; i < moviesData.size(); i++) {
                        if (name.equals(moviesData.get(i).getTitle())) {
                            int cod = 1;
                            if (genres.get(0) != null) {
                                for (String genre1 : genres) {
                                    int found = 0;
                                    for (String genre2 : moviesData.get(i).getGenres()) {
                                        if (genre1.equals(genre2)) {
                                            found = 1;
                                        }
                                    }
                                    if (found == 0) {
                                        cod = 0;
                                    }
                                }
                            }
                            if (year.get(0) != null) {
                                if (Integer.parseInt(year.get(0).toString())
                                        != moviesData.get(i).getYear()) {
                                    cod = 0;
                                }
                            }
                            if (cod == 1) {
                                int num = user.getHistory().get(name);
                                if (viewedNumber.get(name) != null) {
                                    Integer value = viewedNumber.get(name);
                                    num += value;
                                }
                                viewedNumber.put(name, num);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < serialsData.size(); i++) {
                        if (name.equals(serialsData.get(i).getTitle())) {
                            int cod = 1;
                            if (genres.get(0) != null) {
                                for (String genre1 : genres) {
                                    int found = 0;
                                    for (String genre2 : serialsData.get(i).getGenres()) {
                                        if (genre1.equals(genre2)) {
                                            found = 1;
                                        }
                                    }
                                    if (found == 0) {
                                        cod = 0;
                                    }
                                }
                            }
                            if (year.get(0) != null) {
                                if (Integer.parseInt(year.get(0).toString())
                                        != serialsData.get(i).getYear()) {
                                    cod = 0;
                                }
                            }
                            if (cod == 1) {
                                int num = user.getHistory().get(name);
                                if (viewedNumber.get(name) != null) {
                                    Integer value = viewedNumber.get(name);
                                    num += value;
                                }
                                viewedNumber.put(name, num);
                            }
                        }
                    }
                }
            }
        }
        Map<String, Integer> sortedViews;
        // sortez in functie de sortType
        if (sortType.equals("desc")) {
            Map<String, Integer> sortedAlfa =
                    viewedNumber.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                            .limit(viewedNumber.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedViews =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            Map<String, Integer> sortedAlfa =
                    viewedNumber.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .limit(viewedNumber.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedViews =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> query = new ArrayList<String>();
        for (String name : sortedViews.keySet()) {
            query.add(name);
        }
        return "Query result: " + query;
    }

    /**
     * Number of ratings query
     */

    public String numberOfRatings(final int number, final String sortType) {
        Map<String, Integer> numberRatings = new HashMap<String, Integer>();
        for (String name : userRating.keySet()) {
            // parcurg toti userii si calculez numarul de ratinguri dat de fiecare
            Map<String, ArrayList<Video>> videos = userRating.get(name);
            int contor = 0;
            for (String video : videos.keySet()) {
                contor++;
            }
            numberRatings.put(name, contor);
        }
        Map<String, Integer> sortedUsers;
        // sortez in functie de sortType
        if (sortType.equals("desc")) {
            Map<String, Integer> sortedAlfa =
                    numberRatings.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                            .limit(numberRatings.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedUsers =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        } else {
            Map<String, Integer> sortedAlfa =
                    numberRatings.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .limit(numberRatings.size())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));

            sortedUsers =
                    sortedAlfa.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(number)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue,
                                    (e1, e2) -> e1, LinkedHashMap::new));
        }
        ArrayList<String> query = new ArrayList<String>();
        for (String name : sortedUsers.keySet()) {
            query.add(name);
        }
        return "Query result: " + query;
    }
    /**
     * Standard recommendation
     */

    public String standard(final String username) {
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                for (int i = 0; i < moviesData.size(); i++) {
                    int cod = 0;
                    for (String movie : user.getHistory().keySet()) {
                        if (moviesData.get(i).getTitle().equals(movie)) {
                            cod = 1;
                        }
                    }
                    if (cod == 0) {
                        // daca am gasit primul video nevazut din baza de date
                        // il returnez ca recommendation
                        return "StandardRecommendation result: " + moviesData.get(i).getTitle();
                    }
                }
                for (int i = 0; i < serialsData.size(); i++) {
                    int cod = 0;
                    for (String movie : user.getHistory().keySet()) {
                        if (serialsData.get(i).getTitle().equals(movie)) {
                            cod = 1;
                        }
                    }
                    if (cod == 0) {
                        // daca am ajuns aici inseamna ca toate filmele au fost vizionate
                        // si returnez primul serial nevazut
                        return "StandardRecommendation result: " + serialsData.get(i).getTitle();
                    }
                }
            }
        }
        // toate videourile din baza de date au fost vizionate
        return "StandardRecommendation cannot be applied!";
    }

    /**
     * Best unseen recommendation
     */

    public String bestUnseen(final String username) {
        for (String name : userRating.keySet()) {
            Map<String, ArrayList<Video>> videos = userRating.get(name);
            ArrayList<ArrayList<Video>> each = new ArrayList<>();
            Map<String, Double> videoRating = new HashMap<String, Double>();
            // o sa calculez din nou ratingul tuturor videourilor
            for (String video : videos.keySet()) {
                each.add(0, videos.get(video));
                Integer sez = videos.get(video).get(0).getNumber();
                int seasonNumber = 0;
                if (sez != 0) {
                    for (int k = 0; k < serialsData.size(); k++) {
                        if (serialsData.get(k).getTitle().equals(video)) {
                            seasonNumber = serialsData.get(k).getNumberSeason();
                        }
                    }
                }
                for (int j = 0; j < videos.get(video).size(); j++) {
                    videos.get(video).get(j).setSeasonNumber(seasonNumber);
                }
                Double sum = 0.0;
                for (int j = 0; j < videos.get(video).size(); j++) {
                    sum += videos.get(video).get(j).getGrade();
                    // calculez suma tuturor ratingurilor din lista unui titlu
                }
                if (videos.get(video).get(0).getSeasonNumber() != 0) {
                    // daca are numar de sezoane >0 este serial
                    // si impart la numarul total de sezoane
                    sum /= seasonNumber;
                }
                videoRating.put(video, sum);
            }
            eachRating.put(name, videoRating);
        }
        Map<String, Double> finalRating = new HashMap<String, Double>();
        for (int i = 0; i < moviesData.size(); i++) {
            String currentTitle = moviesData.get(i).getTitle();
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            for (String user : eachRating.keySet()) {
                for (String mapTitle : eachRating.get(user).keySet()) {
                    if (mapTitle.equals(currentTitle)) {
                        cod = 1;
                        appearence++;
                        sum += eachRating.get(user).get(mapTitle);
                    }
                }
            }
            if (cod == 1) {
                finalRating.put(currentTitle, sum / appearence);
            }
        }
        for (int i = 0; i < serialsData.size(); i++) {
            String currentTitle = serialsData.get(i).getTitle();
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            for (String user : eachRating.keySet()) {
                for (String mapTitle : eachRating.get(user).keySet()) {
                    if (mapTitle.equals(currentTitle)) {
                        cod = 1;
                        appearence++;
                        sum += eachRating.get(user).get(mapTitle);
                    }
                }
            }
            if (cod == 1) {
                finalRating.put(currentTitle, sum / appearence);
            }
        }
        // sortez toate videourile descrescator dupa rating
        Map<String, Double> sortedRatings =
                finalRating.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(finalRating.size())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        int cod = 0;
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                // caut prin map-ul sortat primul video nevazut din baza de date
                for (String title : sortedRatings.keySet()) {
                    cod = 0;
                    for (String video : user.getHistory().keySet()) {
                        if (title.equals(video)) {
                            cod = 1;
                        }
                    }
                    if (cod == 0) {
                        // am gasit primul video nevazut
                        return "BestRatedUnseenRecommendation result: " + title;
                    }
                }
            }
        }
        if (cod == 1) {
            // userul a vazut toate videourile cu rating, acum trebuie trecut prin baza de date
            for (UserInputData user : usersData) {
                if (user.getUsername().equals(username)) {
                    for (int i = 0; i < moviesData.size(); i++) {
                        int ok = 0;
                        for (String title : user.getHistory().keySet()) {
                            if (moviesData.get(i).getTitle().equals(title)) {
                                ok = 1;
                            }
                        }
                        if (ok == 0) {
                            return "BestRatedUnseenRecommendation result: "
                                    + moviesData.get(i).getTitle();
                        }
                    }
                    for (int i = 0; i < serialsData.size(); i++) {
                        int ok = 0;
                        for (String title : user.getHistory().keySet()) {
                            if (serialsData.get(i).getTitle().equals(title)) {
                                ok = 1;
                            }
                        }
                        if (ok == 0) {
                            return "BestRatedUnseenRecommendation result: "
                                    + serialsData.get(i).getTitle();
                        }
                    }
                }
            }
        }
        // userul a vazut toate videourile
        return "BestRatedUnseenRecommendation cannot be applied!";
    }

    /**
     * Popular recommendation
     */

    public String popular(final String username) {
        Map<String, Integer> popularity = new HashMap<String, Integer>();
        int premium = 1;
        // verific ca utilizatorul sa fie premium
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals("BASIC")) {
                    premium = 0;
                }
            }
        }
        if (premium == 1) {
            for (Genre genre : Genre.values()) {
                int sum = 0;
                // aici o sa calculez popularitatea fiecarui gen
                // pentru fiecare video adaug la suma numarul vizionarilor videourilor
                // din acel gen
                for (int i = 0; i < moviesData.size(); i++) {
                    for (String s : moviesData.get(i).getGenres()) {
                        if (s.equalsIgnoreCase(genre.name())) {
                            for (UserInputData user : usersData) {
                                if (user.getHistory().containsKey(
                                        moviesData.get(i).getTitle())) {
                                    sum += user.getHistory().get(moviesData.get(i).getTitle());
                                }
                            }
                        }
                    }
                }
                // la fel si pentru seriale
                for (int i = 0; i < serialsData.size(); i++) {
                    for (String s : serialsData.get(i).getGenres()) {
                        if (s.equalsIgnoreCase(genre.name())) {
                            for (UserInputData user : usersData) {
                                if (user.getHistory().containsKey(
                                        serialsData.get(i).getTitle())) {
                                    sum += user.getHistory().get(serialsData.get(i).getTitle());
                                }
                            }
                        }
                    }
                }
                // pun in map numarul de vizionari pentru genul curent
                popularity.put(genre.name(), sum);
            }
            // sortez descrescator map-ul dupa rating
            Map<String, Integer> sortedPopular = popularity.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(popularity.size())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));
            for (UserInputData user : usersData) {
                if (user.getUsername().equals(username)) {
                    for (String s : sortedPopular.keySet()) {
                        for (int i = 0; i < moviesData.size(); i++) {
                            // caut primul video nevazut din genul cel mai popular
                            for (String gen : moviesData.get(i).getGenres()) {
                                if (gen.equalsIgnoreCase(s)) {
                                    int cod = 0;
                                    for (String title : user.getHistory().keySet()) {
                                        if (title.equals(moviesData.get(i).getTitle())) {
                                            cod = 1;
                                        }
                                    }
                                    if (cod == 0) {
                                        // am gasit primul video nevizualizat
                                        return "PopularRecommendation result: "
                                                + moviesData.get(i).getTitle();
                                    }
                                }
                            }
                        }
                        // toate filmele au fost vazute, iar acum o sa caut prin seriale
                        for (int i = 0; i < serialsData.size(); i++) {
                            for (String gen : serialsData.get(i).getGenres()) {
                                if (gen.equalsIgnoreCase(s)) {
                                    int cod = 0;
                                    for (String title : user.getHistory().keySet()) {
                                        if (title.equals(serialsData.get(i).getTitle())) {
                                            cod = 1;
                                        }
                                    }
                                    if (cod == 0) {
                                        return "PopularRecommendation result: "
                                                + serialsData.get(i).getTitle();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // toate videourile din toate genurile au fost vizionate
        return "PopularRecommendation cannot be applied!";
    }

    /**
     * Favorite recommendation
     */

    public String favorite(final String username) {
        Map<String, Integer> mostFavorite = new HashMap<String, Integer>();
        int premium = 1;
        // verific ca utilizatorul sa fie premium
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getSubscriptionType().equals("BASIC")) {
                    premium = 0;
                }
            }
        }
        if (premium == 1) {
            for (UserInputData user : usersData) {
                for (String title : user.getFavoriteMovies()) {
                    if (mostFavorite.get(title) == null) {
                        mostFavorite.put(title, 1);
                    } else {
                        // calculez de cate ori a fost adaugat la favorite fiecare video
                        int value = mostFavorite.get(title);
                        mostFavorite.put(title, value + 1);
                    }
                }
            }
        }
        // sortez descrescator dupa numarul de adaugari
        Map<String, Integer> sortedFavorite = mostFavorite.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(mostFavorite.size())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        for (String seen : sortedFavorite.keySet()) {
            for (UserInputData user : usersData) {
                if (user.getUsername().equals(username)) {
                    int cod = 0;
                    // caut primul video nevazut de utilizator din map-ul de favorite
                    for (String title : user.getHistory().keySet()) {
                        if (title.equals(seen)) {
                            cod = 1;
                            // videoul a fost vazut
                        }
                    }
                    if (cod == 0) {
                        return "FavoriteRecommendation result: " + seen;
                    }
                }
            }
        }
        // toate videoruile au fost vizionate de catre utilizator
        return "FavoriteRecommendation cannot be applied!";
    }

    /**
     * Search recommendation
     */


    public String search(final String username, final String genre) {
        ArrayList<String> allTitles = new ArrayList<String>();
        Map<String, Double> ratings = new HashMap<String, Double>();
        // daca am un gen dat ca filtru
        if (genre != null) {
            // pun intr-un map toate videoruile dintr-un anumit gen
            for (int i = 0; i < moviesData.size(); i++) {
                for (String gen : moviesData.get(i).getGenres()) {
                    if (gen.equals(genre)) {
                        allTitles.add(moviesData.get(i).getTitle());
                    }
                }
            }
            for (int i = 0; i < serialsData.size(); i++) {
                for (String gen : serialsData.get(i).getGenres()) {
                    if (gen.equals(genre)) {
                        allTitles.add(serialsData.get(i).getTitle());
                    }
                }
            }
            for (String title : allTitles) {
                for (UserInputData user : usersData) {
                    if (user.getUsername().equals(username)) {
                        if (!user.getHistory().containsKey(title)) {
                            ratings.put(title, 0.0);
                            // pun in map-ul ratings toate videourile din acel gen
                            // care sunt nevizualizate
                        }
                    }
                }
            }
        }
        // calculez ratingul tuturor videourilor din acel gen pentru a le putea sorta
        for (String name : userRating.keySet()) {
            Map<String, ArrayList<Video>> videos = userRating.get(name);
            ArrayList<ArrayList<Video>> each = new ArrayList<>();
            Map<String, Double> videoRating = new HashMap<String, Double>();
            for (String video : videos.keySet()) {
                each.add(0, videos.get(video));
                Integer sez = videos.get(video).get(0).getNumber();
                int seasonNumber = 0;
                if (sez != 0) {
                    for (int k = 0; k < serialsData.size(); k++) {
                        if (serialsData.get(k).getTitle().equals(video)) {
                            seasonNumber = serialsData.get(k).getNumberSeason();
                        }
                    }
                }
                for (int j = 0; j < videos.get(video).size(); j++) {
                    videos.get(video).get(j).setSeasonNumber(seasonNumber);
                }
                Double sum = 0.0;
                for (int j = 0; j < videos.get(video).size(); j++) {
                    sum += videos.get(video).get(j).getGrade();
                    // calculez suma tuturor ratingurilor din lista unui titlu
                }
                if (videos.get(video).get(0).getSeasonNumber() != 0) {
                    // daca are numar de sezoane >0 este serial
                    // si impart la numarul total de sezoane
                    sum /= seasonNumber;
                }
                videoRating.put(video, sum);
            }
            eachRating.put(name, videoRating);
        }
        Map<String, Double> finalRating = new HashMap<String, Double>();
        for (int i = 0; i < moviesData.size(); i++) {
            String currentTitle = moviesData.get(i).getTitle();
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            for (String user : eachRating.keySet()) {
                for (String mapTitle : eachRating.get(user).keySet()) {
                    if (mapTitle.equals(currentTitle)) {
                        cod = 1;
                        appearence++;
                        sum += eachRating.get(user).get(mapTitle);
                    }
                }
            }
            if (cod == 1) {
                finalRating.put(currentTitle, sum / appearence);
            }
        }
        for (int i = 0; i < serialsData.size(); i++) {
            String currentTitle = serialsData.get(i).getTitle();
            int appearence = 0;
            Double sum = 0.0;
            int cod = 0;
            for (String user : eachRating.keySet()) {
                for (String mapTitle : eachRating.get(user).keySet()) {
                    if (mapTitle.equals(currentTitle)) {
                        cod = 1;
                        appearence++;
                        sum += eachRating.get(user).get(mapTitle);
                    }
                }
            }
            if (cod == 1) {
                finalRating.put(currentTitle, sum / appearence);
            }
        }
        // fac sortarea crescatoare dupa rating si dupa nume
        Map<String, Double> sortedAlfa =
                finalRating.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                        .limit(finalRating.size())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        Map<String, Double> sortedRatings =
                sortedAlfa.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                        .limit(sortedAlfa.size())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        ArrayList<String> searchResult = new ArrayList<String>();
        for (String name : sortedRatings.keySet()) {
            for (String title : ratings.keySet()) {
                if (name.equals(title)) {
                    // daca un video nu a fost vizualizat si are rating
                    // il adaug in vectorul final
                    searchResult.add(name);
                }
            }
        }
        if (searchResult.isEmpty()) {
            for (String title : ratings.keySet()) {
                // daca toate videourile au fost deja vizualizate le adaug pe cele initiale
                searchResult.add(title);
            }
        }
        if (searchResult.isEmpty()) {
            // niciun video nu a fost gasit
            return "SearchRecommendation cannot be applied!";
        }
        for (String title : ratings.keySet()) {
            if (!searchResult.contains(title)) {
                // adaug videourile ramase care nu au primit rating
                searchResult.add(0, title);
            }
        }
        // sortez rezultatul
        Collections.sort(searchResult);
        return "SearchRecommendation result: " + searchResult;

    }
}
