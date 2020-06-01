package com.dyszlewskiR.edu.scientling.data.database.tables;

public class WordsTable {

    public static final String TABLE_NAME = "Words";
    public static final String ALIAS = "W";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static String[] getColumns() {
        String[] columns = new String[WordsColumns.COLUMNS_COUNT];
        columns[WordsColumns.ID_POSITION] = WordsColumns.ID;
        columns[WordsColumns.CONTENT_POSITION] = WordsColumns.CONTENT;
        columns[WordsColumns.TRANSCRIPTION_POSITION] = WordsColumns.TRANSCRIPTION;
        columns[WordsColumns.DEFINITION_FK_POSITION] = WordsColumns.DEFINITION_FK;
        columns[WordsColumns.LESSON_FK_POSITION] = WordsColumns.LESSON_FK;
        columns[WordsColumns.PART_OF_SPEECH_FK_POSITION] = WordsColumns.PART_OF_SPEECH_FK;
        columns[WordsColumns.CATEGORY_FK_POSITION] = WordsColumns.CATEGORY_FK;
        columns[WordsColumns.DIFFICULT_POSITION] = WordsColumns.DIFFICULT;
        columns[WordsColumns.MASTER_LEVEL_POSITION] = WordsColumns.MASTER_LEVEL;
        columns[WordsColumns.SELECTED_POSITION] = WordsColumns.SELECTED;
        columns[WordsColumns.OWN_POSITION] = WordsColumns.OWN;
        columns[WordsColumns.LEARNING_DATE_POSITION] = WordsColumns.LEARNING_DATE;
        columns[WordsColumns.IMAGE_NAME_POSITION] = WordsColumns.IMAGE_NAME;
        columns[WordsColumns.RECORD_NAME_POSITION] = WordsColumns.RECORD_NAME;
        return columns;
    }

    public static class WordsColumns {
        public static final String ID = "id";
        public static final String CONTENT = "content";
        public static final String TRANSCRIPTION = "transcription";
        public static final String DEFINITION_FK = "definition_fk";
        public static final String LESSON_FK = "lesson_fk";
        public static final String PART_OF_SPEECH_FK = "part_of_speech_fk";
        public static final String CATEGORY_FK = "category_fk";
        public static final String DIFFICULT = "difficult";
        public static final String MASTER_LEVEL = "master_level";
        public static final String SELECTED = "selected";
        public static final String OWN = "own";
        public static final String LEARNING_DATE = "learning_date";
        public static final String IMAGE_NAME = "image_name";
        public static final String RECORD_NAME = "record_name";

        public static final int ID_POSITION = 0;
        public static final int CONTENT_POSITION = 1;
        public static final int TRANSCRIPTION_POSITION = 2;
        public static final int DEFINITION_FK_POSITION = 3;
        public static final int LESSON_FK_POSITION = 4;
        public static final int PART_OF_SPEECH_FK_POSITION = 5;
        public static final int CATEGORY_FK_POSITION = 6;
        public static final int DIFFICULT_POSITION = 7;
        public static final int MASTER_LEVEL_POSITION = 8;
        public static final int SELECTED_POSITION = 9;
        public static final int OWN_POSITION = 10;
        public static final int LEARNING_DATE_POSITION = 11;
        public static final int IMAGE_NAME_POSITION = 12;
        public static final int RECORD_NAME_POSITION = 13;

        public static final int COLUMNS_COUNT = 14;
    }

}
