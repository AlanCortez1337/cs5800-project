package com.cortezalan.problems.problem1.utils;

public class Character {
    public char character;
    public CharacterProperty properties;

    public Character(char character, CharacterProperty properties) {
        this.character = character;
        this.properties = properties;
    }

    @Override
    public String toString() {
        return  "Character: " + this.character + "\n" +
                "PROPERTIES \n ----- \n" + this.properties.toString();
    }
}
