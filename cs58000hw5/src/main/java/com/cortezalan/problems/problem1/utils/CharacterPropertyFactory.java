package com.cortezalan.problems.problem1.utils;

import java.util.HashMap;
import java.util.Optional;

public class CharacterPropertyFactory {
    private HashMap<String, CharacterProperty> characterProperties = new HashMap<>();
    public CharacterProperty getCharacterProperties(Optional<FontColor> fontColor, Optional<FontFamily> fontFamily, Optional<FontSize> fontSize) {
        final String characterPropertyKey = fontColor.orElse(FontColor.BLACK) + "-" + fontFamily.orElse(FontFamily.ARIAL) + "-" + fontSize.orElse(FontSize.SIZE_12);

        if (characterProperties.containsKey(characterPropertyKey)) {
            return characterProperties.get(characterPropertyKey);
        } else {
            CharacterPropertyBuilder newCharacterProperty = new CharacterPropertyBuilder();

            if (fontColor.isPresent()) {
                newCharacterProperty = newCharacterProperty.setFontColor(fontColor.get());
            }
            if(fontFamily.isPresent()) {
                newCharacterProperty = newCharacterProperty.setFontFamily(fontFamily.get());
            }
            if(fontSize.isPresent()) {
                newCharacterProperty = newCharacterProperty.setFontSize(fontSize.get());
            }

            return newCharacterProperty.createCharacterProperty();
        }
    }
}
