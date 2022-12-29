package com.example.pokemontool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pokemon {
    String pokemonName;
    int pokemonIndex;
    ArrayList<String[]> moveset;
    public void changePokemon(String name) {
        pokemonName = name;
        pokemonIndex = indexOf(name);

        moveset = displayMoveset(pokemonIndex);
    }

//    private void displayMoveInfo(String pokemonMove) {
//        for (String[] move : Main.moves) {
//            if (move[0].equalsIgnoreCase(pokemonMove)) {
//                moveVal.setText(move[0]);
//                typeVal.setText(move[1]);
//                ppVal.setText(move[2]);
//                powerVal.setText(move[3]);
//                accuracyVal.setText(move[4]);
//                effectVal.setText(move[5]);
//
//                if (move[3].equals("N/A"))
//                    categoryVal.setText("Status");
//                else if (myPokemon.isPhysical(move[1]))
//                    categoryVal.setText("Physical");
//                else
//                    categoryVal.setText("Special");
//
//                fixWindowSize();
//                break;
//            }
//        }
//    }

    //given the index of a Pokemon, display its moveset

    private ArrayList<String[]> displayMoveset(int pokeIndex) {
        ArrayList<String[]> arrayListBuilder = new ArrayList<>();
        int movesetSize = Main.pokemonEvosAndMovesets.get(pokeIndex).size()-1;

        //in pokemonEvosAndMovesets, moves begin at index 3
        for (int moveIndex = 3, movesetIndex = 0; moveIndex<movesetSize; moveIndex++, movesetIndex++)
        {
            String pokemonMove = Main.pokemonEvosAndMovesets.get(pokeIndex).get(moveIndex).trim();
            while (pokemonMove.startsWith("db E") || pokemonMove.startsWith("db 0")) {
                moveIndex++;
                pokemonMove = Main.pokemonEvosAndMovesets.get(pokeIndex).get(moveIndex).trim();
            }

            pokemonMove = pokemonMove.replaceAll("db ","").replaceAll("_"," ");

            String[] pokemonMoveArray = pokemonMove.split("~");
            arrayListBuilder.add(pokemonMoveArray);
        }
        return arrayListBuilder;
    }

    public void incrementPokemon(int incrementVal) {
        int nextPokeIndex = getPokemonIndex()+incrementVal;
        String nextPokeName = nameOf(nextPokeIndex);
        changePokemon(nextPokeName);
    }


    public int getPokemonIndex() {
        return(pokemonIndex);
    }
    public String getPokemonName() {
        return(pokemonName);
    }
    public ArrayList<String[]> getMoveset() { return(moveset); }

    public boolean isPhysical (String category) {
        List<String> physicalTypes = new ArrayList<>(
                Arrays.asList("Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel"));
        return (physicalTypes.contains(category));
    }

    private String nameOf(int pokeIndex) {
        return Main.pokemon.get(pokeIndex);
    }

    //Given a Pokemon name, return the index of that Pokemon to be used for methods that require it
    private int indexOf(String pokeName) {
        for (int indexOfPoke = 0; indexOfPoke<Main.pokemonEvosAndMovesets.size(); indexOfPoke++)
        {
            if (pokeName.equalsIgnoreCase(Main.pokemonEvosAndMovesets.get(indexOfPoke).get(0)))
                return indexOfPoke;
        }
        System.out.println("Pokemon not found");
        return 1;
    }
}
