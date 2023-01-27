package com.example.pokemontool;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    ImageView boxImage0, boxImage1, boxImage2, boxImage3, boxImage4, boxImage5;
    @FXML
    GridPane boxGrid;
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
    PokemonBox pokemonBox = new PokemonBox();

    //loads Charmander when program is first loaded
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateGUI();
        Platform.runLater(this::loadContextMenu);

    }

    //opens a new instance of the program for the use of comparing Pokemon
    public void newInstance() throws Exception {
        Main main = new Main();
        Stage PrimaryStage = new Stage();
        main.start(PrimaryStage);
    }
    private void loadContextMenu() {

        for (Node node : boxGrid.getChildrenUnmodifiable()) {
            if (node instanceof ImageView) {
                System.out.println(node);
                int startingIndex = 8;
                int gridIndex = Integer.parseInt(node.getId().substring(startingIndex));

                MenuItem saveMI = new MenuItem("Save");
                saveMI.setOnAction(event -> saveBoxedPokemon(gridIndex, node));

                MenuItem loadMI = new MenuItem("Load");
                loadMI.setOnAction(event -> loadBoxedPokemon(gridIndex));

                ContextMenu boxCM = new ContextMenu();
                boxCM.getItems().add(saveMI);
                boxCM.getItems().add(loadMI);
                node.setOnContextMenuRequested(e -> boxCM.show(node, e.getScreenX(), e.getScreenY()));
            }
        }
    }
    private void saveBoxedPokemon(int index, Node imageView) {
        pokemonBox.setBoxedPokemon(myPokemon, index);
        displayImage((ImageView) imageView);

    }
    private void loadBoxedPokemon(int index) {
        myPokemon = pokemonBox.getBoxedPokemon(index);
        updateGUI();
    }
    public void goToNextPoke() {
        int nextIndex =myPokemon.getPokemonIndex()+1;
        String nextPoke = myPokemon.nameOf(nextIndex);
        myPokemon = new Pokemon(nextPoke);
        updateGUI();
    }
    public void goToPrevPoke() {
        int prevIndex =myPokemon.getPokemonIndex()-1;
        String prevPoke = myPokemon.nameOf(prevIndex);
        myPokemon = new Pokemon(prevPoke);
        updateGUI();
    }

    //Update pokemon_name variable when Pokemon name is entered
    public void changePokemonManually() {
        String newPokeName = pokemonSearchField.getText().toLowerCase();
        if (!Main.pokemon.contains("newPokeName")) { return; }
        myPokemon = new Pokemon(newPokeName);
        updateGUI();
    }


    //calls all the necessary methods to update the GUI for the provided Pokemon
    private void updateGUI() {
        populateStatsArea();
        displayImage(pokemonIcon);
        displayMoveset();
        displayEvolutionInfo();
        populateTypeTags();
    }

    //grabs pokemon icon from pkmn.net and displays it
    private void displayImage(ImageView imageView) {
        Task<Image> imageTask = new Task<>() {
            @Override
            protected Image call() {
                String url = myPokemon.getPictureURL();
                return new Image(url);
            }
        };
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/loading.gif")));
            imageView.setFitWidth(64);
            imageView.setFitHeight(64);
        } catch (NullPointerException e) {
            throw new RuntimeException("loading.gif not present");
        }

        imageTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {

                imageView.setImage(imageTask.getValue());
                imageView.setFitWidth(0);
                imageView.setFitHeight(0);
                if (!imageView.getId().equals(pokemonIcon.getId())) {
                    imageView.setScaleX(0.5);
                    imageView.setScaleY(0.5);
                }
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
    private void displayEvolutionInfo() {
        evoVBox.getChildren().clear();
        int pokeIndex = myPokemon.getPokemonIndex();
        int evoDataIndex = 1;
        String evoDataLine = Main.pokemonEvosAndMovesets.get(pokeIndex).get(evoDataIndex).trim();
        if (evoDataLine.equals("db 0 ; no more evolutions")) {
            Label evoLabel = new Label("Does not evolve");
            evoVBox.getChildren().add(evoLabel);
        }
        else {
            while (!evoDataLine.equals("db 0 ; no more evolutions")) {
                String[] evoData = evoDataLine.split("~");
                for (int i = 0; i < evoData.length; i++) {
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
            Platform.runLater(this::fixWindowSize); //Needed because scene is not fully loaded when fixWindowSize
                                                    //is first called from this method, causing null pointer exception
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
        int opacity = 99;

        Pokemon.Type[] type = myPokemon.getType();
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
        int[] baseStats = myPokemon.getBaseStats();

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
