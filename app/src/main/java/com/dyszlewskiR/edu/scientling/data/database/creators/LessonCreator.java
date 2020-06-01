package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.fasterxml.jackson.databind.JsonNode;

import static com.dyszlewskiR.edu.scientling.data.database.tables.LessonsTable.LessonsColumns.GLOBAL_ID;
import static com.dyszlewskiR.edu.scientling.data.database.tables.LessonsTable.LessonsColumns.SET_FK;

public class LessonCreator {

    public static Lesson createFromCursor(Cursor cursor) {
        Lesson lesson = new Lesson();
        int columnsCount = cursor.getColumnCount();
        for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            switch (cursor.getColumnName(columnIndex)) {
                case ID:
                    lesson.setId(cursor.getLong(columnIndex));
                    break;
                case NAME:
                    lesson.setName(cursor.getString(columnIndex));
                    break;
                case NUMBER:
                    lesson.setNumber(cursor.getInt(columnIndex));
                    break;
                case SET_FK:
                    /*VocabularySet set = new VocabularySet(cursor.getLong(columnIndex));
                    lesson.setSet(set);*/
                    lesson.setSetId(cursor.getLong(columnIndex));
                    break;
                case GLOBAL_ID:
                    if(!cursor.isNull(columnIndex)){
                        lesson.setGlobalId(cursor.getLong(columnIndex));
                    }
                default: //tutaj trafi postęp
                    lesson.setProgress(cursor.getInt(columnIndex));
            }
        }
        return lesson;
    }


    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String NUMBER = "number";

    public static Lesson createFromJson(JsonNode node){
        if(node == null){
            return null;
        }
        Lesson lesson = new Lesson();
        lesson.setGlobalId(node.path(ID).asLong());
        lesson.setName(node.path(NAME).asText());
        lesson.setNumber(node.path(NUMBER).asInt());

        return lesson;
    }
}
