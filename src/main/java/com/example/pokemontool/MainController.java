package com.example.pokemontool;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    @FXML
    Label nameLabel;
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
    ImageView pokemon_icon;
    @FXML
    Label hpVal, atkVal, defVal, spaVal, spdVal, speVal;
    @FXML
    Rectangle hpBar, atkBar, defBar, spaBar, spdBar, speBar;
    @FXML
    GridPane baseStatsGrid;
    @FXML
    Label moveVal, typeVal, categoryVal, ppVal, powerVal, accuracyVal, effectVal;

    private Stage stage;
    private Scene scene;
    private Parent root;
    String pokemon_name;


    public void displayImage(String pokemon_name) {
        int index = Main.pokemon.indexOf(pokemon_name)+1;
        Task<Image> imageTask = new Task<>() {
            @Override
                    protected Image call() {
                String url = "https://pkmn.net/sprites/crystal/" + index + ".gif";
                return new Image(url);
            }
        };

        pokemon_icon.setImage(new Image(getClass().getResourceAsStream("/loading.gif")));

        imageTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                pokemon_icon.setImage(imageTask.getValue());
            }
        });
        new Thread(imageTask).start();
    }


    public void displayName(String username) {
        nameLabel.setText("Hello: " + username);
    }


    public void goToNextPoke(ActionEvent e) throws IOException {
        pokemon_name = Main.pokemon.get(Main.pokemon.indexOf(pokemon_name)+1);
        populateMovesetArea();
    }
    public void goToPrevPoke(ActionEvent e) throws IOException {
        pokemon_name = Main.pokemon.get(Main.pokemon.indexOf(pokemon_name)-1);
        populateMovesetArea();
    }


    //Switch back to main manu
    public void switchToScene1(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    //Update pokemon_name variable when Pokemon name is entered
    public void changePokemonManually(ActionEvent e) throws IOException {
        pokemon_name = pokemonSearchField.getText().toLowerCase();
        populateMovesetArea();
    }


    //given the index of a Pokemon, display its evolution information
    public void displayEvolutionInfo(int poke) {
        Pattern evolution_level = Pattern.compile(".+,([0-9]+),([a-zA-Z]+)");
        Matcher matcher;
        String evo_data = Main.pokemonstats.get(poke).get(1).trim();
        if (evo_data.equals("db 0 ; no more evolutions"))
            evoLvlLabel.setText("Does not evolve.");

        else if (evo_data.startsWith("db EVOLVE_ITEM"))
        {
            String[] evo_item_data = evo_data.split(",");
            evoLvlLabel.setText("Evolves into " + evo_item_data[2] + " with " + evo_item_data[1]);
        }
        else if (evo_data.startsWith("db EVOLVE_HAPPINESS"))
        {
            String[] evo_item_data = evo_data.split(",");
            evoLvlLabel.setText("Evolves into " + evo_item_data[2] + " with high friendship");
        }
        else
        {
            matcher = evolution_level.matcher(evo_data);
            matcher.find();
            String lvl = matcher.group(1);
            String next_evo = matcher.group(2);
            evoLvlLabel.setText("Evolves into " + next_evo + " starting at level " + lvl);
        }
    }


    //given the index of a Pokemon, display its moveset
    public void displayMoveset(int poke) {
        pokemonMovesetsVBox.getChildren().clear();
        for (int move_i = 3; move_i<Main.pokemonstats.get(poke).size()-1;move_i++)
        {
            String pokemon_move = Main.pokemonstats.get(poke).get(move_i).trim();
            pokemon_move = pokemon_move.replaceAll("^db ","").replaceAll(",", "\t").replaceAll("_"," ");
            Button move = new Button(pokemon_move);
            move.setPadding(new Insets(1,4,1,4));

            String finalPokemon_move = pokemon_move.replaceAll("[0-9].","").trim();
            move.setOnAction(e -> displayMoveInfo(finalPokemon_move));

            pokemonMovesetsVBox.getChildren().add(move);
        }
    }


    List<String> physicalTypes = new ArrayList<>(
            Arrays.asList("Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel"));
    private boolean isPhysical (String move) {
        return (physicalTypes.contains(move));
    }


    public void displayMoveInfo (String pokemon_move) {
        for (String[] move : Main.moves) {
            if (move[0].equalsIgnoreCase(pokemon_move)) {
                moveVal.setText(move[0]);
                typeVal.setText(move[1]);

                if (move[3].equals("N/A"))
                    categoryVal.setText("Status");
                else if (isPhysical(move[1]))
                    categoryVal.setText("Physical");
                else
                    categoryVal.setText("Special");

                ppVal.setText(move[2]);
                powerVal.setText(move[3]);
                accuracyVal.setText(move[4]);
                effectVal.setText(move[5]);
            }
        }
    }


    //Finds the Pokemon (in the variable pokemon_name) in the pokemonstats list, and then populates the evo info box and moveset info box
    public void populateMovesetArea() throws IOException {
        System.out.println(pokemon_name);
        Pattern poke_name = Pattern.compile("^"+(pokemon_name)+"Ev", Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        for (int poke = 0; poke<Main.pokemonstats.size();poke++)
        {
            matcher = poke_name.matcher(Main.pokemonstats.get(poke).get(0));
            if (matcher.find()) {
                displayEvolutionInfo(poke);
                displayMoveset(poke);
            }
        }

        displayImage(pokemon_name);
        populateStatsArea();
    }





    //Populates Pokemon basestats area
    public void populateStatsArea() throws IOException {
        InputStream in = getClass().getResource("/base_stats/" + pokemon_name + ".asm").openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        reader.readLine();
        reader.readLine();
        String[] base_stats_str = reader.readLine().trim().replaceAll(" ","").substring(2).split(",");
        int[] base_stats = new int[6];
        for (int stat = 0; stat < base_stats_str.length;stat++) {
            base_stats[stat] = Integer.parseInt(base_stats_str[stat]);
        }

        int stat_index = 0;
        int bar_index = 0;
        for (Node node : baseStatsGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) != null){
                if (GridPane.getColumnIndex(node) == 1) {
                    Label stat = (Label) node;
                    stat.setText(base_stats_str[stat_index]);
                    stat_index++;
                }
                else if (GridPane.getColumnIndex(node) == 2) {
                    Rectangle bar = (Rectangle) node;
                    bar.setWidth(base_stats[bar_index]*0.8);
                    bar.setFill(Color.hsb(base_stats[bar_index]-25,1.0,1.0));
                    bar_index++;
                }
            }
        }

    }

}
