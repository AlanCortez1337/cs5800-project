package com.cortezalan.problems.problem1.utils;

public class CharacterPropertyBuilder {
    private FontColor fontColor;
    private FontFamily fontFamily;
    private FontSize fontSize;

    public CharacterPropertyBuilder() {}

    public CharacterPropertyBuilder setFontColor(FontColor fontColor) {
        this.fontColor = fontColor;
        return this;
    }
    public CharacterPropertyBuilder setFontFamily(FontFamily fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }
    public CharacterPropertyBuilder setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public CharacterProperty createCharacterProperty() {
        if (fontColor == null) {
            this.fontColor = FontColor.BLACK;
        }
        if (fontFamily == null) {
            this.fontFamily = FontFamily.ARIAL;
        }
        if (fontSize == null) {
            this.fontSize = FontSize.SIZE_12;
        }

        return new CharacterProperty(this.fontColor, this.fontFamily, this.fontSize);
    }
}
