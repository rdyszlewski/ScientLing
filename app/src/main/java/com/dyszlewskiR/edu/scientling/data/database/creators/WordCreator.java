package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
import com.dyszlewskiR.edu.scientling.models.entity.Translation;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable.CategoriesColumns;
import static com.dyszlewskiR.edu.scientling.data.database.tables.DefinitionsTable.DefinitionsColumns;
import static com.dyszlewskiR.edu.scientling.data.database.tables.PartsOfSpeechTable.PartsOfSpeechColumns;
import static com.dyszlewskiR.edu.scientling.data.database.tables.WordsTable.WordsColumns;

/**
 * Created by Razjelll on 13.11.2016.
 */

public class WordCreator {

    public static Word createFromCursor(Cursor cursor) {
        Word word = new Word();
        Definition definition = null;
        Category category = null;
        PartOfSpeech partOfSpeech = null;
        int columnsCount = cursor.getColumnCount();
        for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            switch (cursor.getColumnName(columnIndex)) {
                case WordsColumns.ID:
                    word.setId(cursor.getLong(columnIndex));
                    break;
                case WordsColumns.CONTENT:
                    word.setContent(cursor.getString(columnIndex));
                    break;
                case WordsColumns.DEFINITION_FK:
                    if (!cursor.isNull(columnIndex)) {
                        if (definition == null) {
                            definition = new Definition(cursor.getLong(columnIndex));
                        } else {
                            definition.setId(cursor.getLong(columnIndex));
                        }
                    }
                    break;
                case WordsColumns.LESSON_FK:
                    word.setLessonId(cursor.getLong(columnIndex));
                    break;
                case WordsColumns.PART_OF_SPEECH_FK:
                    if (!cursor.isNull(columnIndex)) {
                        if (partOfSpeech == null) {
                            partOfSpeech = new PartOfSpeech(cursor.getLong(columnIndex));
                        } else {
                            partOfSpeech.setId(cursor.getLong(columnIndex));
                        }
                    }
                    break;
                case WordsColumns.CATEGORY_FK:
                    if (!cursor.isNull(columnIndex)) {
                        if (category == null) {
                            category = new Category(cursor.getLong(columnIndex));
                        } else {
                            category.setId(cursor.getLong(columnIndex));
                        }
                    }
                    break;
                case WordsColumns.DIFFICULT:
                    word.setDifficult((byte) cursor.getInt(columnIndex));
                    break;
                case WordsColumns.MASTER_LEVEL:
                    word.setMasterLevel((byte) cursor.getInt(columnIndex));
                    break;
                case WordsColumns.SELECTED:
                    word.setSelected(cursor.getInt(columnIndex) == 1);
                    break;
                case WordsColumns.OWN:
                    word.setOwn(cursor.getInt(columnIndex) == 1);
                    break;
                case WordsColumns.LEARNING_DATE:
                    if (!cursor.isNull(columnIndex)) {
                        word.setLearningDate(cursor.getInt(columnIndex));
                    }
                    break;
                case DefinitionsColumns.CONTENT_ALIAS:
                    if (!cursor.isNull(columnIndex)) {
                        if (definition == null) {
                            definition = new Definition();
                        }
                        definition.setContent(cursor.getString(columnIndex));
                    }
                    break;
                case DefinitionsColumns.TRANSLATION_ALIAS:
                    if (!cursor.isNull(columnIndex)) {
                        if (definition == null) {
                            definition = new Definition();
                        }
                        definition.setTranslation(cursor.getString(columnIndex));
                    }
                    break;
                case PartsOfSpeechColumns.NAME_ALIAS:
                    if (!cursor.isNull(columnIndex)) {
                        if (partOfSpeech == null) {
                            partOfSpeech = new PartOfSpeech();
                        }
                        partOfSpeech.setName(cursor.getString(columnIndex));
                    }
                    break;
                case CategoriesColumns.NAME_ALIAS:
                    if (!cursor.isNull(columnIndex)) {
                        if (category == null) {
                            category = new Category();
                        }
                        category.setName(cursor.getString(columnIndex));
                    }
                    break;
                case WordsColumns.IMAGE_NAME:
                    if (!cursor.isNull(columnIndex)) {
                        word.setImageName(cursor.getString(columnIndex));
                    }
                    break;
                case WordsColumns.RECORD_NAME:
                    if (!cursor.isNull(columnIndex)) {
                        word.setRecordName(cursor.getString(columnIndex));
                    }
                    break;
            }
            word.setDefinition(definition);
            word.setPartsOfSpeech(partOfSpeech);
            word.setCategory(category);
        }
        return word;
    }

    private static final String CONTENT = "content";
    private static final String TRANSLATIONS = "translations";
    private static final String DEFINITION = "definition";
    private static final String CATEGORY = "category";
    private static final String PART_OF_SPEECH = "part";
    private static final String DIFFICULTY = "difficulty";
    private static final String SENTENCES = "sentences";
    private static final String HINTS = "hints";
    private static final String IMAGE = "image";
    private static final String RECORD = "record";
    private static final String LESSON = "lesson";

    public static Word createFromJson(JsonNode node) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Word word = new Word();
        word.setContent(node.path(CONTENT).asText());

        word.setTranslations(getTranslationsList(node, objectMapper));
        word.setDefinition(getDefinitionFromJson(node));
        long categoryId = node.path(CATEGORY).asLong();
        if(categoryId > 0){
            word.setCategory(new Category(categoryId));
        }
        //word.setCategory(new Category(node.path(CATEGORY).asLong()));
        long partOfSpeechId = node.path(PART_OF_SPEECH).asLong();
        if(partOfSpeechId > 0){
            word.setPartsOfSpeech(new PartOfSpeech(partOfSpeechId));
        }
        //word.setPartsOfSpeech(new PartOfSpeech(node.path(PART_OF_SPEECH).asLong()));
        word.setDifficult((byte) node.path(DIFFICULTY).asInt());
        word.setSentences(getSentencesList(node, objectMapper));
        word.setHints(getHintList(node, objectMapper));
        word.setImageName(node.path(IMAGE).asText());
        word.setRecordName(node.path(RECORD).asText());
        word.setLessonId(node.path(LESSON).asLong());

        return word;
    }

    private static ArrayList<Translation> getTranslationsList(JsonNode node, ObjectMapper mapper) throws IOException {
        JsonNode translationsNode = node.path(TRANSLATIONS);
        if(translationsNode != null){
            ArrayList<Translation> translations = mapper.convertValue(translationsNode, new TypeReference<List<Translation>>(){});
            if(translations != null && !translations.isEmpty()){
                return translations;
            }
        }
        return null;
    }

    private static Definition getDefinitionFromJson(JsonNode node){
        JsonNode definitionNode = node.path(DEFINITION);
        return DefinitionCreator.createFromJson(definitionNode);
    }

    private static ArrayList<Sentence> getSentencesList(JsonNode node, ObjectMapper mapper){
        JsonNode sentencesNode = node.path(SENTENCES);
        if(sentencesNode!= null ){
            ArrayList<Sentence> sentences = mapper.convertValue(sentencesNode, new TypeReference<List<Sentence>>(){});
            if(sentences!=null && !sentences.isEmpty()){
                return sentences;
            }
        }
        return null;
    }

    private static ArrayList<Hint> getHintList(JsonNode node, ObjectMapper mapper){
        JsonNode hintsNode = node.path(HINTS);
        if(hintsNode != null){
            ArrayList<Hint> hints = mapper.convertValue(hintsNode, new TypeReference<List<Hint>>(){});
            if(hints!=null && !hints.isEmpty()){
                return hints;
            }
        }
        return null;
    }



}
