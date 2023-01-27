package com.example.pokemontool;

public class PokemonBox {
    private Pokemon[] box = new Pokemon[5];

    public void changeBoxedPokemon(Pokemon pokemon, int boxIndex) {
        box[boxIndex] = pokemon;
    }
}
