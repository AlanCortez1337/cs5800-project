package com.cortezalan.problems.problem1;

import com.cortezalan.problems.problem1.utils.*;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Document newDocument = new Document();

        newDocument.typeCharacterToDocument('H', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('e', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('l', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('l', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('o', Optional.empty(), Optional.empty(), Optional.empty());

        newDocument.typeCharacterToDocument('W', Optional.of(FontColor.BLUE), Optional.of(FontFamily.CALIBRI), Optional.of(FontSize.SIZE_14));
        newDocument.typeCharacterToDocument('o', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('r', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('l', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('d', Optional.empty(), Optional.empty(), Optional.empty());

        newDocument.typeCharacterToDocument('C', Optional.of(FontColor.RED), Optional.of(FontFamily.VERDANA), Optional.of(FontSize.SIZE_16));
        newDocument.typeCharacterToDocument('S', Optional.empty(), Optional.empty(), Optional.empty());

        newDocument.typeCharacterToDocument('5', Optional.of(FontColor.BLACK), Optional.of(FontFamily.VERDANA), Optional.of(FontSize.SIZE_12));
        newDocument.typeCharacterToDocument('8', Optional.empty(), Optional.empty(), Optional.empty());

        newDocument.editDocument(CharacterEditChoice.LAST_CHARACTER, Optional.of(FontColor.RED), Optional.of(FontFamily.ARIAL), Optional.of(FontSize.SIZE_14));

        newDocument.typeCharacterToDocument('0', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('0', Optional.empty(), Optional.empty(), Optional.empty());

        newDocument.printDocumentCharacterData();
    }
}
