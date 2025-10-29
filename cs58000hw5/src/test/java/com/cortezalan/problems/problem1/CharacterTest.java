package com.cortezalan.problems.problem1;

import com.cortezalan.problems.problem1.utils.Character;
import com.cortezalan.problems.problem1.utils.CharacterProperty;
import com.cortezalan.problems.problem1.utils.FontColor;
import com.cortezalan.problems.problem1.utils.FontFamily;
import com.cortezalan.problems.problem1.utils.FontSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterTest {
    private Character character;

    @BeforeEach
    protected void setUp() throws Exception {
        CharacterProperty characterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);
        character = new Character('A', characterProperty);
    }

    @Test
    public void initializeCharacterTest() {
        CharacterProperty newCharacterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);
        Character newCharacter = new Character('A', newCharacterProperty);

        assertEquals(newCharacter.character, this.character.character);
        assertEquals(newCharacter.properties.getFontColor(), this.character.properties.getFontColor());
        assertEquals(newCharacter.properties.getFontFamily(), this.character.properties.getFontFamily());
        assertEquals(newCharacter.properties.getFontSize(), this.character.properties.getFontSize());
    }

    @Test
    public void editCharacterTextTest() {
        CharacterProperty newCharacterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);
        Character newCharacter = new Character('A', newCharacterProperty);

        newCharacter.character = 'B';

        assertEquals('B', newCharacter.character);
    }

    @Test
    public void correctToStringPrint() {
        CharacterProperty newCharacterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);
        Character newCharacter = new Character('A', newCharacterProperty);

        assertEquals(newCharacter.toString(), this.character.toString());
    }
}
