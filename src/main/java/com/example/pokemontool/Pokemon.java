package com.example.pokemontool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pokemon {
    String pokemonName;
    int pokemonIndex;

    public void changePokemon(String name) {
        pokemonName = name;
        pokemonIndex = indexOf(name);
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
