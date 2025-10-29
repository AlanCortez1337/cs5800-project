package com.cortezalan.problems.problem1;

import com.cortezalan.problems.problem1.utils.*;
import com.cortezalan.problems.problem1.utils.Character;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentTest {
    private Document document;
    private Document savedDocument;
    private CharacterPropertyFactory characterPropertyFactory;
    private CharacterProperty characterProperty1;
    private CharacterProperty characterProperty2;
    private Character character1;
    private Character character2;

    @BeforeEach
    protected void setUp() throws Exception {
        document = new Document();
        savedDocument = new Document();
        characterPropertyFactory = new CharacterPropertyFactory();
        characterProperty1 = characterPropertyFactory.getCharacterProperties(Optional.empty(), Optional.empty(), Optional.empty());
        characterProperty2 = characterPropertyFactory.getCharacterProperties(Optional.of(FontColor.BLUE), Optional.empty(), Optional.empty());

        character1 = new Character('a', characterProperty1);
        character2 = new Character('b', characterProperty2);

        savedDocument.typeCharacterToDocument('a', Optional.empty(), Optional.empty(), Optional.empty());
        savedDocument.typeCharacterToDocument('b', Optional.of(FontColor.BLUE), Optional.empty(), Optional.empty());

    }

    @Test
    public void typeCharacterInDocument() {
        Document newDocument = new Document();

        newDocument.typeCharacterToDocument('a', Optional.empty(), Optional.empty(), Optional.empty());

        assertEquals(1, newDocument.characters.size());
        assertEquals(this.character1.toString(), newDocument.characters.get(0).toString());
    }

    @Test
    public void editFontFamilyInDocument() {
        Document newDocument = new Document();

        newDocument.typeCharacterToDocument('b', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.editDocument(CharacterEditChoice.LAST_CHARACTER, Optional.of(FontColor.BLUE), Optional.empty(), Optional.empty());

        assertEquals(1, newDocument.characters.size());
        assertEquals(this.character2.toString(), newDocument.characters.get(0).toString());
    }

    @Test
    public void saveDocument() {
        Document newDocument = new Document();

        newDocument.typeCharacterToDocument('a', Optional.empty(), Optional.empty(), Optional.empty());
        newDocument.typeCharacterToDocument('b', Optional.of(FontColor.BLUE), Optional.empty(), Optional.empty());

        Document savedDocument = newDocument.saveDocument();

        assertEquals(savedDocument.characters.toString(), savedDocument.characters.toString());
    }

    @Test
    public void loadOldDocument() {
        Document newDocument = new Document();

        newDocument.loadDocument(savedDocument.characters);

        assertEquals(newDocument.characters.toString(), savedDocument.characters.toString());
    }
}
