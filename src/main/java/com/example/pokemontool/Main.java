package com.example.pokemontool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Main extends Application {

    public static double windowHeight;
    static List<List<String>> pokemonEvosAndMovesets = new ArrayList<>();
    static List<String> pokemon = new ArrayList<>();
    static String[][] moves = new String[251][];
    static String[][] baseStats = new String[251][];
    private final String[] types = {
            "Normal","Fighting","Flying","Poison","Ground","Rock","Bug","Ghost","Steel",
            "Fire", "Water","Grass","Electric","Psychic","Ice","Dragon","Dark"};
    static Label[] typeLabels = new Label[17];
    public static void main(String[] args) {launch(args);}


    private void initializeMoveInformation() throws IOException {
        InputStream in = getClass().getResourceAsStream("/moveData.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (int i=0; i<251;i++) {
            String[] line = reader.readLine().split("\t");
            moves[i] = line;
        }
    }

    //Initializes array of Pokemon names by reading the pokemon.txt file
    private void initializePokemon() throws IOException {
        InputStream in = getClass().getResourceAsStream("/pokemon.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (int i=0; i<251;i++) {
            String line = reader.readLine().toLowerCase();
            pokemon.add(line);
        }
    }

    //Initializes array of basestats
    private void initializeBaseStats() throws IOException {
        InputStream in = getClass().getResourceAsStream("/basestats.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (int i = 0; i < 251; i++)
            baseStats[i] = reader.readLine().split(",");
        System.out.println(Arrays.deepToString(baseStats));
    }

    private void typeToLabels() {
        int index = 0;
        for (String type : types) {
            Label typeLabel = new Label(type);
            //typeLabel.getStyleClass().add();
            typeLabels[index] = typeLabel;
            index++;
        }
    }

    //initializes moveset array by reading the movesets.txt file and adding each moveset as a list to the index of the Pokemon in the pokemonstats list.
    private void initializeMovesets() throws IOException {
        InputStream in = getClass().getResourceAsStream("/movesets.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (int i=0; i<251; i++) {
            List<String> moves;
            String line = reader.readLine().trim();
            moves = List.of(line.split("\t"));
            pokemonEvosAndMovesets.add(moves);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializePokemon();
        initializeMovesets();
        initializeMoveInformation();
        initializeBaseStats();
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
//        loader.setControllerFactory(controllerClass -> new MainController());

        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Pokemon Nuzlocke Helper");
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.show();
        windowHeight = primaryStage.getHeight();
    }

}
