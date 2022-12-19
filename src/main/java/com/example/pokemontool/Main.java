package com.example.pokemontool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    static List<List<String>> pokemonstats = new ArrayList<>();
    static List<String> pokemon = new ArrayList<>();

    public static void main(String[] args) {launch(args);}

    //Initializes array of pokemon names by reading the pokemon-lower.txt file
    public void initializePokemon() throws IOException {
        InputStream in = getClass().getResource("/pokemon-lower.txt").openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        for (int i=0; i<251;i++) {
            String line = reader.readLine();
            pokemon.add(line);
        }
        System.out.println(pokemon);
    }

    //initializes moveset array by reading the movesets.txt file and adding each moveset as a list to the index of the pokemon in the pokemonstats list.
    public void initializeMovesets() throws IOException {
        InputStream in = getClass().getResource("/movesets.txt").openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Pattern poke_name = Pattern.compile("(^.+)EvosAttacks:", Pattern.CASE_INSENSITIVE);
        for (int i=0; i<251;) {
            System.out.println(i);
            List<String> moves = new ArrayList<>();
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            Matcher matcher = poke_name.matcher(line);
            if (matcher.find()) {
                while (!line.equals("")) {
                    moves.add(line);
                    line = reader.readLine();
                    System.out.println(line);
                }
                pokemonstats.add(moves);
                i++;
            }

            System.out.println(pokemonstats);
            //pokemon.add(line);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializePokemon();
        initializeMovesets();

        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        //String css = this.getClass().getResource("styles.css").toExternalForm();
        //scene.getStylesheets().add(css);

        Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Pokemon Nuzlocke Helper");
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
