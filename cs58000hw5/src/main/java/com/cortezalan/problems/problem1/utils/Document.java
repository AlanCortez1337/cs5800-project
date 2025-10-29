package com.cortezalan.problems.problem1.utils;

import java.util.ArrayList;
import java.util.Optional;

public class Document {
    public ArrayList<Character> characters = new ArrayList<>();

    public Document() {}

    public void typeCharacterToDocument(char character, Optional<FontColor> fontColor, Optional<FontFamily> fontFamily, Optional<FontSize> fontSize) {

        Character newCharacter;

        if (!characters.isEmpty() && fontColor.isEmpty() && fontFamily.isEmpty() && fontSize.isEmpty()) {

            CharacterProperty currentCharacterProperties = characters.getLast().properties;

            newCharacter = new Character(character, currentCharacterProperties);
        } else {
            CharacterPropertyFactory characterPropertyFactory = new CharacterPropertyFactory();
            CharacterProperty characterProperties = characterPropertyFactory.getCharacterProperties(fontColor, fontFamily, fontSize);

            newCharacter = new Character(character, characterProperties);
        }
        characters.add(newCharacter);

    }

    public void editDocument(CharacterEditChoice editChoice, Optional<FontColor> fontColor, Optional<FontFamily> fontFamily, Optional<FontSize> fontSize) {
        CharacterPropertyFactory characterPropertyFactory = new CharacterPropertyFactory();
        CharacterProperty characterProperties = characterPropertyFactory.getCharacterProperties(fontColor, fontFamily, fontSize);

        switch(editChoice) {
            case CharacterEditChoice.ALL_CHARACTERS:
                for (Character character : characters) {
                    character.properties = characterProperties;
                }
                break;
            case CharacterEditChoice.LAST_CHARACTER:
                characters.getLast().properties = characterProperties;
            default:
        }
    }

    public Document saveDocument() {
        return this;
    }

    public void loadDocument(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public void printDocumentCharacterData () {
        StringBuilder documentText = new StringBuilder();

        for (Character character : characters) {
            documentText.append(character.character);
        }
        System.out.println("DOCUMENT TEXT: " + documentText);


        System.out.println("DETAILS:");

        for (Character character : characters) {
            System.out.println(character.toString());
        }
    }
}
