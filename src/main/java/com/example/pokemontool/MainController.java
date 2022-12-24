package com.example.pokemontool;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    @FXML
    Label evoLvlLabel;
    @FXML
    TextField pokemonSearchField;
    @FXML
    Button next;
    @FXML
    Button prev;
    @FXML
    VBox pokemonMovesetsVBox;
    @FXML
    ImageView pokemonIcon;
    @FXML
    Label hpVal, atkVal, defVal, spaVal, spdVal, speVal;
    @FXML
    Rectangle hpBar, atkBar, defBar, spaBar, spdBar, speBar;
    @FXML
    GridPane baseStatsGrid;
    @FXML
    Label moveVal, typeVal, categoryVal, ppVal, powerVal, accuracyVal, effectVal;
    @FXML
    GridPane moveDetailGrid;
    @FXML
    VBox mainVBox;
    @FXML
    VBox evoVBox;

    String pokemonName;

    //loads Charmander when program is first loaded
    public void initialize() {
        pokemonName = "Charmander";
        changePokemon();
    }

    //opens a new instance of the program for the use of comparing Pokemon
    public void newInstance() throws Exception {
        Main main = new Main();
        Stage PrimaryStage = new Stage();
        main.start(PrimaryStage);
    }



    public void goToNextPoke() {
        pokemonName = Main.pokemon.get(Main.pokemon.indexOf(pokemonName)+1);
        changePokemon();
    }
    public void goToPrevPoke() {
        pokemonName = Main.pokemon.get(Main.pokemon.indexOf(pokemonName)-1);
        changePokemon();
    }

    //Update pokemon_name variable when Pokemon name is entered
    public void changePokemonManually() {
        pokemonName = pokemonSearchField.getText().toLowerCase();
        changePokemon();
    }


    //calls all the necessary methods to update the Pokemon
    private void changePokemon() {
        pokemonName = pokemonName.toLowerCase();
        int pokemonIndex = indexOf(pokemonName);
        populateStatsArea(pokemonIndex);
        displayImage(pokemonName);
        displayMoveset(pokemonIndex);
        displayEvolutionInfo(pokemonIndex);
    }

    //grabs pokemon icon from pkmn.net and displays it
    private void displayImage(String pokeName) {
        int index = Main.pokemon.indexOf(pokeName)+1;
        Task<Image> imageTask = new Task<>() {
            @Override
            protected Image call() {
                String url = "https://pkmn.net/sprites/crystal/" + index + ".gif";
                return new Image(url);
            }
        };
        pokemonIcon.setImage(new Image(getClass().getResourceAsStream("/loading.gif")));
        pokemonIcon.setFitWidth(64);
        pokemonIcon.setFitHeight(64);

        imageTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                pokemonIcon.setImage(imageTask.getValue());
                pokemonIcon.setFitWidth(0);
                pokemonIcon.setFitHeight(0);
            }
        });
        new Thread(imageTask).start();
    }

    private void displayMoveInfo(String pokemonMove) {
        for (String[] move : Main.moves) {
            if (move[0].equalsIgnoreCase(pokemonMove)) {
                moveVal.setText(move[0]);
                typeVal.setText(move[1]);
                ppVal.setText(move[2]);
                powerVal.setText(move[3]);
                accuracyVal.setText(move[4]);
                effectVal.setText(move[5]);

                if (move[3].equals("N/A"))
                    categoryVal.setText("Status");
                else if (isPhysical(move[1]))
                    categoryVal.setText("Physical");
                else
                    categoryVal.setText("Special");

                fixWindowSize();
            }
        }
    }

    //given the index of a Pokemon, display its evolution information
    private void displayEvolutionInfo(int pokeIndex) {
        evoVBox.getChildren().clear();

        int evoDataIndex = 1;
        String evo_data = Main.pokemonEvosAndMovesets.get(pokeIndex).get(evoDataIndex).trim();
        if (evo_data.equals("db 0 ; no more evolutions")) {
            Label evol = new Label("Does not evolve");
            evoVBox.getChildren().add(evol);
        }
        else {
            while (evo_data.equals("db 0 ; no more evolutions") == false) {
                System.out.println(evo_data);

                if (evo_data.startsWith("db EVOLVE_ITEM")) {
                    String[] evo_item_data = evo_data.split("~");
                    Label evol = new Label("Evolves into " + evo_item_data[2] + " with " + evo_item_data[1]);
                    evoVBox.getChildren().add(evol);
                } else if (evo_data.startsWith("db EVOLVE_HAPPINESS")) {
                    String[] evo_item_data = evo_data.split("~");
                    Label evol = new Label("Evolves into " + evo_item_data[2] + " with high friendship");
                    evoVBox.getChildren().add(evol);
                } else if (evo_data.startsWith("db EVOLVE_LEVEL")) {
                    String[] evo_item_data = evo_data.split("~");
                    Label evol = new Label("Evolves into " + evo_item_data[2] + " starting at level " + evo_item_data[1]);
                    evoVBox.getChildren().add(evol);
                }
                evoDataIndex++;
                evo_data = Main.pokemonEvosAndMovesets.get(pokeIndex).get(evoDataIndex).trim();
            }
            try {
                evoVBox.getScene().getWindow().sizeToScene();
            } catch (NullPointerException e) {
                System.out.println("Whoops");
            }
        }
    }

    //given the index of a Pokemon, display its moveset
    private void displayMoveset(int poke) {
        pokemonMovesetsVBox.getChildren().clear();
        for (int moveIndex = 3; moveIndex<Main.pokemonEvosAndMovesets.get(poke).size()-1; moveIndex++)
        {
            String pokemonMove = Main.pokemonEvosAndMovesets.get(poke).get(moveIndex).trim();
            while (pokemonMove.startsWith("db E") || pokemonMove.startsWith("db 0")) {
                moveIndex++;
                pokemonMove = Main.pokemonEvosAndMovesets.get(poke).get(moveIndex).trim();
                System.out.println(pokemonMove);
            }
            pokemonMove = pokemonMove.replaceAll("db ","").replaceAll("~", "\t").replaceAll("_"," ");
            System.out.println(pokemonMove);
            Button move = new Button(pokemonMove);
            move.setPadding(new Insets(1,4,1,4));

            String finalPokemonMove = pokemonMove.replaceAll("[0-9].","").trim();
            move.setOnAction(e -> displayMoveInfo(finalPokemonMove));

            pokemonMovesetsVBox.getChildren().add(move);
        }
    }

    //Populates Pokemon basestats area
    private void populateStatsArea(int pokeIndex) {
        String[][] baseStats = Main.baseStats;
        int stat_index = 1;
        int bar_index = 1;
        for (Node node : baseStatsGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) != null){
                if (GridPane.getColumnIndex(node) == 1) {
                    Label stat = (Label) node;
                    stat.setText(baseStats[pokeIndex][stat_index]);
                    stat_index++;
                }
                else if (GridPane.getColumnIndex(node) == 2) {
                    Rectangle bar = (Rectangle) node;
                    System.out.println(baseStats[pokeIndex][bar_index]);
                    bar.setWidth(Integer.parseInt(baseStats[pokeIndex][bar_index])*0.8);
                    bar.setFill(Color.hsb(Integer.parseInt(baseStats[pokeIndex][bar_index])-25,1.0,1.0));
                    bar_index++;
                }
            }
        }
    }


    //expands the window to accommodate added content (move effect description and evolution information)
    private void fixWindowSize() {
        mainVBox.getScene().getWindow().sizeToScene();
    }


    private boolean isPhysical (String category) {
        List<String> physicalTypes = new ArrayList<>(
                Arrays.asList("Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel"));
        return (physicalTypes.contains(category));
    }

    //Given a Pokemon name, return the index of that Pokemon to be used for methods that require it
    private int indexOf(String pokeName) {
        System.out.println(pokeName);
        Pattern pokeNamePattern = Pattern.compile(pokeName, Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        for (int indexOfPoke = 0; indexOfPoke<Main.pokemonEvosAndMovesets.size(); indexOfPoke++)
        {
            matcher = pokeNamePattern.matcher(Main.pokemonEvosAndMovesets.get(indexOfPoke).get(0));
            if (matcher.find()) {
                return indexOfPoke;
            }
        }
        System.out.println("Pokemon not found");
        return 1;
    }

}
