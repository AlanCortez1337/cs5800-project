package com.cortezalan.problems.problem1.utils;

public class CharacterProperty {
    private FontColor fontColor;
    private FontFamily fontFamily;
    private FontSize fontSize;

    public CharacterProperty(FontColor fontColor, FontFamily fontFamily, FontSize fontSize) {
        this.fontColor = fontColor;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }

    public FontColor getFontColor() {
        return this.fontColor;
    }
    public FontFamily getFontFamily() {
        return this.fontFamily;
    }
    public FontSize getFontSize() {
        return this.fontSize;
    }

    @Override
    public String toString() {
        return  "FontColor: " + this.getFontColor() + "\n" +
                "FontFamily: " + this.getFontFamily() + "\n" +
                "FontSize: " + this.getFontSize() + "\n";
    }
}
