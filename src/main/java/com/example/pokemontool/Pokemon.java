package com.example.pokemontool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pokemon {
    String pokemonName;
    int pokemonIndex;
    ArrayList<String[]> moveset;
    private enum Type {
        NORMAL, FIGHTING, FLYING, POISON, GROUND, ROCK, BUG, GHOST, STEEL, FIRE, WATER, GRASS, ELECTRIC, PSYCHIC, ICE, DRAGON, DARK
    }
    public Pokemon(String name) {
        pokemonName = name;
        pokemonIndex = indexOf(name);
        moveset = generateMoveset(pokemonIndex);
    }
    public void changePokemon(String name) {
        pokemonName = name;
        pokemonIndex = indexOf(name);
        moveset = generateMoveset(pokemonIndex);
    }

    private ArrayList<String[]> generateMoveset(int pokeIndex) {
        ArrayList<String[]> arrayListBuilder = new ArrayList<>();
        int movesetSize = Main.pokemonEvosAndMovesets.get(pokeIndex).size()-1;

        //in pokemonEvosAndMovesets, moves begin at index 3 at the earliest
        for (int moveIndex = 3, movesetIndex = 0; moveIndex<movesetSize; moveIndex++, movesetIndex++)
        {
            String pokemonMove = Main.pokemonEvosAndMovesets.get(pokeIndex).get(moveIndex).trim();
            //Entries that start with db E or db 0 aren't moves
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
    //Given a pokemon index, return the name of that pokemon
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
