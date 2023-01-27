package com.example.pokemontool;

import java.util.Arrays;

public class PokemonBox {
    private final Pokemon[] box = new Pokemon[6];

    public void setBoxedPokemon(Pokemon pokemon, int boxIndex) {
        box[boxIndex] = pokemon.clone();
        System.out.println(Arrays.toString(box));
    }
    public Pokemon getBoxedPokemon(int boxIndex) {
        return box[boxIndex];
    }
}
