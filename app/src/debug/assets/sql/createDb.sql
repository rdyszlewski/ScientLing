CREATE TABLE Languages (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name TEXT NOT NULL,
       abbreviation TEXT CHECK (length(abbreviation)<=4) NOT NULL,
       code TEXT CHECK(length(code) = 5)NOT NULL UNIQUE,
       flag BLOB NULL
);
CREATE TABLE Categories(
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name  TEXT NOT NULL,
       UNIQUE (name)
);
CREATE TABLE Translations (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       content TEXT NOT NULL UNIQUE
);
CREATE TABLE Sentences(
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       content TEXT NOT NULL UNIQUE,
       translation TEXT
);
CREATE TABLE Hints(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        content TEXT NOT NULL UNIQUE
);
CREATE TABLE Sets (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        language_l2_fk INTEGER,
        language_l1_fk INTEGER,
        catalog TEXT NULL,
        global_id INTEGER,
        uploading_user TEXT NULL,
        images_uploaded INTEGER DEFAULT 0 CHECK (images_uploaded = 0 OR images_uploaded = 1),
        records_uploaded INTEGER DEFAULT 0 CHECK (records_uploaded = 0 OR records_uploaded = 1),

        FOREIGN KEY (language_l2_fk) REFERENCES Languages(id),
        FOREIGN KEY (language_l1_fk) REFERENCES Languages(id)
);
CREATE TABLE Lessons (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        number INTEGER NOT NULL CHECK(number >= 0) DEFAULT 1,
        set_fk INTEGER NOT NULL,
        global_id INTEGER,
        FOREIGN KEY (set_fk) REFERENCES Sets(id) ON DELETE CASCADE
);
CREATE TABLE PartsOfSpeech (
        id INTEGER PRIMARY KEY,
        name TEXT NOT NULL UNIQUE
);
CREATE TABLE Definitions (
        id INTEGER PRIMARY KEY,
        content TEXT NOT NULL,
        translation NULL
);
CREATE TABLE Words (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        content TEXT NOT NULL,
        transcription TEXT NULL,
        definition_fk INTEGER NULL,
        lesson_fk INTEGER NOT NULL,
        part_of_speech_fk INTEGER NULL,
        category_fk INTEGER NULL,
        difficult INTEGER NOT NULL CHECK(difficult >=0 AND difficult <=5) DEFAULT 0,
        master_level INTEGER NOT NULL CHECK(master_level <= 100) DEFAULT 0,
        selected INTEGER NOT NULL CHECK(selected = 0 OR selected = 1) DEFAULT 0,
        own INTEGER NOT NULL CHECK(own = 0 OR own = 1) DEFAULT 1,
        learning_date INTEGER NULL,
        image_name TEXT NULL,
        record_name TEXT NULL,
        FOREIGN KEY (definition_fk) REFERENCES Definitions(id) ON DELETE CASCADE,
        FOREIGN KEY (lesson_fk) REFERENCES Lessons(id),
        FOREIGN KEY (part_of_speech_fk) REFERENCES PartsOfSpeech(id),
        FOREIGN KEY (category_fk) REFERENCES Categories(id)
);
CREATE TABLE WordsTranslations (
       word_fk INTEGER NOT NULL,
       translation_fk INTEGER NOT NULL,
       PRIMARY KEY(word_fk, translation_fk),
       FOREIGN KEY(word_fk) REFERENCES Words(id) ON DELETE CASCADE,
       FOREIGN KEY (translation_fk) REFERENCES Translations(id) ON DELETE CASCADE
);
CREATE TABLE ExampleSentences (
        word_fk INTEGER NOT NULL,
        sentence_fk INTEGER NOT NULL,
        FOREIGN KEY (word_fk) REFERENCES Words(id) ON DELETE CASCADE,
        FOREIGN KEY (sentence_fk) REFERENCES Sentences(id) ON DELETE CASCADE
);

CREATE TABLE WordsHints(
        word_fk INTEGER NOT NULL,
        hint_fk INTEGER NOT NULL,
        FOREIGN KEY (word_fk) REFERENCES Words(id) ON DELETE CASCADE,
        FOREIGN KEY (hint_fk) REFERENCES Hints(id) ON DELETE CASCADE
);

CREATE TABLE Repetitions (
        word_fk INTEGER PRIMARY KEY,
        date INTEGER NOT NULL,
        FOREIGN KEY(word_fk) REFERENCES Words(id) ON DELETE CASCADE
);
