package com.cortezalan.problems.problem1;

import com.cortezalan.problems.problem1.utils.CharacterProperty;
import com.cortezalan.problems.problem1.utils.FontColor;
import com.cortezalan.problems.problem1.utils.FontFamily;
import com.cortezalan.problems.problem1.utils.FontSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterPropertyTest {
    private CharacterProperty characterProperty;

    @BeforeEach
    protected void setUp() throws Exception {
        characterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);
    }

    @Test
    public void initializeCharacterPropertyTest() {
        CharacterProperty newCharacterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);

        assertEquals(newCharacterProperty.getFontSize(), this.characterProperty.getFontSize());
        assertEquals(newCharacterProperty.getFontFamily(), this.characterProperty.getFontFamily());
        assertEquals(newCharacterProperty.getFontColor(), this.characterProperty.getFontColor());
    }

    @Test
    public void correctToStringPrint() {
        CharacterProperty newCharacterProperty = new CharacterProperty(FontColor.BLACK, FontFamily.ARIAL, FontSize.SIZE_12);

        assertEquals(newCharacterProperty.toString(), this.characterProperty.toString());
    }
}
