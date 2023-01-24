package com.example.pokemontool;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

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
    HBox typeContainer;
    @FXML
    GridPane moveDetailGrid;
    @FXML
    VBox mainVBox;
    @FXML
    VBox evoVBox;

    Pokemon myPokemon = new Pokemon("Charmander");


    //loads Charmander when program is first loaded
    public void initialize() {
        updateGUI(myPokemon);
    }

    //opens a new instance of the program for the use of comparing Pokemon
    public void newInstance() throws Exception {
        Main main = new Main();
        Stage PrimaryStage = new Stage();
        main.start(PrimaryStage);
    }



    public void goToNextPoke() {
        myPokemon.incrementPokemon(1);
        updateGUI(myPokemon);
    }
    public void goToPrevPoke() {
        myPokemon.incrementPokemon(-1);
        updateGUI(myPokemon);
    }

    //Update pokemon_name variable when Pokemon name is entered
    public void changePokemonManually() {
        String newPokeName = pokemonSearchField.getText().toLowerCase();
        myPokemon.changePokemon(newPokeName);
        updateGUI(myPokemon);
    }


    //calls all the necessary methods to update the GUI for the provided Pokemon
    private void updateGUI(Pokemon pokemon) {
        int pokemonIndex = pokemon.getPokemonIndex();
        populateStatsArea();
        displayImage(pokemonIndex);
        displayMoveset();
        displayEvolutionInfo(pokemonIndex);
        populateTypeTags();
    }

    //grabs pokemon icon from pkmn.net and displays it
    private void displayImage(int pokeIndex) {
        Task<Image> imageTask = new Task<>() {
            @Override
            protected Image call() {
                String url = "https://pkmn.net/sprites/crystal/" + (pokeIndex+1) + ".gif";
                return new Image(url);
            }
        };
        try {
            pokemonIcon.setImage(new Image(getClass().getResourceAsStream("/loading.gif")));
            pokemonIcon.setFitWidth(64);
            pokemonIcon.setFitHeight(64);
        } catch (NullPointerException e) {
            throw new RuntimeException("loading.gif not present");
        }

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
                else if (myPokemon.isPhysical(move[1]))
                    categoryVal.setText("Physical");
                else
                    categoryVal.setText("Special");

                fixWindowSize();
                break;
            }
        }
    }

    //given the index of a Pokemon, display its evolution information
    private void displayEvolutionInfo(int pokeIndex) {
        evoVBox.getChildren().clear();
        int evoDataIndex = 1;
        String evoDataLine = Main.pokemonEvosAndMovesets.get(pokeIndex).get(evoDataIndex).trim();
        if (evoDataLine.equals("db 0 ; no more evolutions")) {
            Label evoLabel = new Label("Does not evolve");
            evoVBox.getChildren().add(evoLabel);
        }
        else {
            while (!evoDataLine.equals("db 0 ; no more evolutions")) {
                String[] evoData = evoDataLine.split("~");
                for (int i = 0; i<evoData.length;i++) {
                    evoData[i] = toTitleCase(evoData[i]);
                }
                if (evoDataLine.startsWith("db EVOLVE ITEM")) {
                    Label evoLabel = new Label("Evolves into " + evoData[2] + " with " + evoData[1]);
                    evoVBox.getChildren().add(evoLabel);
                } else if (evoDataLine.startsWith("db EVOLVE HAPPINESS")) {
                    Label evoLabel = new Label("Evolves into " + evoData[2] + " with high friendship");
                    evoVBox.getChildren().add(evoLabel);
                } else if (evoDataLine.startsWith("db EVOLVE LEVEL")) {
                    Label evoLabel = new Label("Evolves into " + evoData[2] + " starting at level " + evoData[1]);
                    evoVBox.getChildren().add(evoLabel);
                }
                evoDataIndex++;
                evoDataLine = Main.pokemonEvosAndMovesets.get(pokeIndex).get(evoDataIndex).trim();
            }
            try {
                evoVBox.getScene().getWindow().sizeToScene();
            } catch (NullPointerException e) {
                System.out.println("Whoops");
            }
        }
    }

    //given the index of a Pokemon, display its moveset
    private void displayMoveset() {
        pokemonMovesetsVBox.getChildren().clear();
        ArrayList<String[]> moveset = myPokemon.getMoveset();
        for (String[] moveInfo : moveset) {
            String moveLevel = moveInfo[0];
            String moveName = moveInfo[1];
            HBox entry = populateEntry(moveLevel, moveName);
            pokemonMovesetsVBox.getChildren().add(entry);
        }
    }
    //Populates type tags
    private void populateTypeTags() {
        typeContainer.getChildren().clear();
        String opacity = "99";

        Pokemon.Type[] type = myPokemon.type;
        Label typeOne = new Label();
        typeOne.getStyleClass().add("type-style");
        String hexTypeOneColor = "#" + type[0].getColor().toString().substring(2,8) + opacity;
        typeOne.setStyle("-fx-background-color: " + hexTypeOneColor);
        typeOne.setText(String.valueOf(type[0]));
        typeContainer.getChildren().add(typeOne);
        if (type[1] != null) {
            Label typeTwo = new Label();
            typeTwo.getStyleClass().add("type-style");
            String hexTypeTwoColor = "#" + type[1].getColor().toString().substring(2,8) + opacity;
            typeTwo.setStyle("-fx-background-color: " + hexTypeTwoColor);
            typeTwo.setText(String.valueOf(type[1]));
            typeContainer.getChildren().add(typeTwo);
        }
    }

    //Populates Pokemon basestats area
    private void populateStatsArea() {
        int[] baseStats = myPokemon.baseStats;

        int stat_index = 0;
        int bar_index = 0;
        //Loop through nodes in the base stats grid.
        // Column index 1 is for the raw stat value.
        // Column index 2 is for the corresponding visual bar length and color
        for (Node node : baseStatsGrid.getChildren()) {

            if (GridPane.getColumnIndex(node) != null){

                if (GridPane.getColumnIndex(node) == 1) {
                    Label stat = (Label) node;
                    stat.setText(String.valueOf(baseStats[stat_index]));
                    stat_index++;
                }
                else if (GridPane.getColumnIndex(node) == 2) {
                    Rectangle bar = (Rectangle) node;
                    bar.setWidth(Integer.parseInt(String.valueOf(baseStats[bar_index]))*0.8);
                    bar.setFill(Color.hsb(Integer.parseInt(String.valueOf(baseStats[bar_index]))-25,1.0,1.0));
                    bar_index++;
                }
            }
        }
    }



    //Helper functions

    //Expands the window to accommodate added content (move effect description and evolution information)
    private void fixWindowSize() {
        mainVBox.getScene().getWindow().sizeToScene();
    }

    private HBox populateEntry(String level, String move) {
        HBox entry = new HBox();
        level = level.concat("\t");
        move = toTitleCase(move);

        Label levelLabel = new Label(level);
        Button moveButton = new Button(move);
        levelLabel.getStyleClass().add("level-entry");
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.getChildren().addAll(levelLabel,moveButton);
        moveButton.setPadding(new Insets(1,4,1,4));
        moveButton.getStyleClass().add("move-button");

        String finalMove = move;
        moveButton.setOnAction(e -> displayMoveInfo(finalMove));
        return entry;
    }

    private String toTitleCase(String string) {
        boolean startOfWord = true;
        StringBuilder builder = new StringBuilder();
        for (char ch: string.toCharArray()) {
            if (startOfWord) {
                builder.append(Character.toUpperCase(ch));
                startOfWord = false;
            }
            else builder.append(Character.toLowerCase(ch));
            if (ch == ' ')
                startOfWord = true;
        }
        return builder.toString();
    }
}
