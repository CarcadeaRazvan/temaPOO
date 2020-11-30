package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.ActionInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation

        JSONObject object;

        for (ActionInputData command : input.getCommands()) { // parcurg toate comenzile
            // si verific ce tip de actiune si de tip am de facut
            if (command.getActionType().equals("command")) {
                if (command.getType().equals("favorite")) {
                    int todo = input.favorite(command.getTitle(), command.getUsername());
                    if (todo == 1) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "error -> " + command.getTitle() + " is not seen");
                        arrayResult.add(object);
                    } else if (todo == 2) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "success -> " + command.getTitle() + " was added as favourite");
                        arrayResult.add(object);
                    } else if (todo == 0) {
                        object = fileWriter.writeFile(command.getActionId(), "",
                                "error -> " + command.getTitle()
                                        + " is already in favourite list");
                        arrayResult.add(object);
                    }
                } else if (command.getType().equals("view")) {
                    int count = input.view(command.getTitle(), command.getUsername());
                    object = fileWriter.writeFile(command.getActionId(), "",
                            "success -> " + command.getTitle() + " was viewed with total "
                                    + "views of " + count);
                    arrayResult.add(object);
                } else {
                    String message = input.commandRating(command.getTitle(),
                            command.getUsername(), command.getGrade(),
                            command.getSeasonNumber());
                    object = fileWriter.writeFile(command.getActionId(), "", message);
                    arrayResult.add(object);
                }
            } else if (command.getActionType().equals("query")) {
                if (command.getObjectType().equals("actors")) {
                    if (command.getCriteria().equals("average")) {
                        String message = input.average(command.getNumber(),
                                command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("awards")) {
                        final int index = 3;
                        String message = input.awards(command.getNumber(),
                                command.getFilters().get(index), command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("filter_description")) {
                        String message = input.filterDescription(command.getFilters().get(2),
                                command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    }
                } else if (command.getObjectType().equals("shows")) {
                    if (command.getCriteria().equals("ratings")) {
                        String message = input.ratingQuery(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("favorite")) {
                        String message = input.favoriteQuery(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("longest")) {
                        String message = input.longest(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else {
                        String message = input.mostViewed(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    }
                } else if (command.getObjectType().equals("movies")) {
                    if (command.getCriteria().equals("ratings")) {
                        String message = input.ratingQuery(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("favorite")) {
                        String message = input.favoriteQuery(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else if (command.getCriteria().equals("longest")) {
                        String message = input.longest(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    } else {
                        String message = input.mostViewed(command.getNumber(),
                                command.getSortType(), command.getObjectType(),
                                command.getFilters().get(0), command.getFilters().get(1));
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    }
                } else if (command.getObjectType().equals("users")) {
                    if (command.getCriteria().equals("num_ratings")) {
                        String message = input.numberOfRatings(command.getNumber(),
                                command.getSortType());
                        object = fileWriter.writeFile(command.getActionId(), "", message);
                        arrayResult.add(object);
                    }
                }
            }
        }
        fileWriter.closeJSON(arrayResult);
    }
}
