package com.example.pokemontool;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pokemon {
    private String pokemonName;
    private int pokemonIndex;
    private ArrayList<String[]> moveset;
    private int[] baseStats;
    private Type[] type;

    public enum Type {
        NORMAL("normal", Color.web("A8A77A")),
        FIGHTING("fighting", Color.web("C22E28")),
        FLYING("flying", Color.web("A98FF3")),
        POISON("poison", Color.web("A33EA1")),
        GROUND("ground", Color.web("E2BF65")),
        ROCK("rock", Color.web("B6A136")),
        BUG("bug", Color.web("A6B91A")),
        GHOST("ghost", Color.web("735797")),
        STEEL("steel", Color.web("B7B7CE")),
        FIRE("fire", Color.web("EE8130")),
        WATER("water", Color.web("6390F0")),
        GRASS("grass", Color.web("7AC74C")),
        ELECTRIC("electric", Color.web("F7D02C")),
        PSYCHIC("psychic", Color.web("F95587")),
        ICE("ice", Color.web("96D9D6")),
        DRAGON("dragon", Color.web("6F35FC")),
        DARK("dark", Color.web("705746"));

        private final String type;
        private final Color color;
        Type(String type, Color color) {
            this.type = type;
            this.color = color;
        }
        public String getType() {
            return type;
        }
        public Color getColor() {
            return color;
        }
    }

    public Pokemon(String name) {
        pokemonName = name;
        pokemonIndex = indexOf(name);
        moveset = generateMoveset(pokemonIndex);
        baseStats = generateBaseStats(pokemonIndex);
        type = generateType(pokemonIndex);
    }
    public void changePokemon(String name) {
        pokemonName = name;
        pokemonIndex = indexOf(name);
        moveset = generateMoveset(pokemonIndex);
        baseStats = generateBaseStats(pokemonIndex);
        type = generateType(pokemonIndex);
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
    private int[] generateBaseStats(int pokeIndex) {
        int[] baseStats = new int[6];
        System.out.println(baseStats.length);
        for (int i = 0; i < baseStats.length; i++) {
            baseStats[i] = Integer.parseInt(Main.baseStats[pokeIndex][i+1]); //Name at index 0, stats start at index 1
        }
        return baseStats;
    }
    private Type[] generateType(int pokeIndex) {
        Type[] type = new Type[2];
        for (int i = 7, t = 0; i < Main.baseStats[pokeIndex].length; i++, t++) {
            type[t] = Type.valueOf(Main.baseStats[pokeIndex][i].toUpperCase());
        }
        return type;
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
    public int[] getBaseStats() { return(baseStats); }
    public Type[] getType() { return(type); }
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
