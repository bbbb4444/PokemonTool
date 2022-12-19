package com.example.pokemontool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    TextArea pokemonMovesetArea;

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

    private Stage stage;
    private Scene scene;
    private Parent root;
    String pokemon_name;


    public void displayImage(String pokemon_name) {
        int index = Main.pokemon.indexOf(pokemon_name)+1;
        String url = "https://pkmn.net/sprites/crystal/" + index + ".gif";
        Image bulba = new Image(url);
        System.out.println(url);
        pokemon_icon.setImage(bulba);
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

    //Change the pokemon label when pokemon name is entered
    public void changePokemonManually(ActionEvent e) throws IOException {
        pokemon_name = pokemonSearchField.getText().toLowerCase();
        populateMovesetArea();
    }

    //Populate moveset text area using pokemon label
    public void populateMovesetArea() throws IOException {

        pokemonMovesetArea.setText("");
        System.out.println(pokemon_name);

        String line;

        Pattern poke_name = Pattern.compile("^"+(pokemon_name)+"Ev", Pattern.CASE_INSENSITIVE);
        Pattern level_and_move = Pattern.compile(".+ (.+),(.+)");
        Pattern evolution_level = Pattern.compile(".+,([0-9]+),([a-zA-Z]+)");
        Matcher matcher;
        for (int poke = 0; poke<Main.pokemonstats.size();poke++)
        {
            matcher = poke_name.matcher(Main.pokemonstats.get(poke).get(0));
            if (matcher.find()) {

                //display evolution data
                String evo_data = Main.pokemonstats.get(poke).get(1).trim();
                if (evo_data.equals("db 0 ; no more evolutions"))
                    evoLvlLabel.setText("Does not evolve.");

                else if (evo_data.startsWith("db EVOLVE_ITEM"))
                {
                    String[] evo_item_data = evo_data.split(",");
                    evoLvlLabel.setText("Evolves into " + evo_item_data[2] + " with " + evo_item_data[1]);
                }

                else
                {
                    matcher = evolution_level.matcher(evo_data);
                    matcher.find();
                    String lvl = matcher.group(1);
                    String next_evo = matcher.group(2);
                    evoLvlLabel.setText("Evolves into " + next_evo + " starting at level " + lvl);
                }

                //display moveset
                for (int move_i = 3; move_i<Main.pokemonstats.get(poke).size()-1;move_i++)
                {
                    String pokemon_move = Main.pokemonstats.get(poke).get(move_i).trim();
                    pokemon_move = pokemon_move.replaceAll("^db ","").replaceAll(",", "\t").replaceAll("_"," ");
                    Button move = new Button(pokemon_move);
                    move.setPadding(new Insets(1,4,1,4));
                    pokemonMovesetsVBox.getChildren().add(move);

                    pokemonMovesetArea.appendText(pokemon_move+"\n");
                }
            }
        }

        displayImage(pokemon_name);
        populateStatsArea();
    }

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


//        hpVal.setText(base_stats_str[0]);
//        atkVal.setText(base_stats_str[1]);
//        defVal.setText(base_stats_str[2]);
//        spaVal.setText(base_stats_str[3]);
//        spdVal.setText(base_stats_str[4]);
//        speVal.setText(base_stats_str[5]);
//
//        hpBar.setWidth(base_stats[0]);
//        hpBar.setFill(Color.hsb(base_stats[0],1.0,1.0));
//        atkBar.setWidth(base_stats[1]);
//        atkBar.setFill(Color.hsb(base_stats[1],1.0,1.0));
//        defBar.setWidth(base_stats[2]);
//        defBar.setFill(Color.hsb(base_stats[2],1.0,1.0));

    }

}
