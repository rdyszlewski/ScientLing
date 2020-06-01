CREATE TABLE Languages (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name TEXT NOT NULL,
       abbreviation TEXT CHECK (length(abbreviation)<=4) NOT NULL,
       code TEXT CHECK(length(code) = 5)NOT NULL UNIQUE
);
CREATE TABLE Categories(
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name  TEXT NOT NULL,
       language_fk INTEGER NOT NULL,
       FOREIGN KEY (language_fk) REFERENCES Languages(id),
       UNIQUE (name, language_fk)
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
CREATE TABLE Tips(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        content TEXT NOT NULL UNIQUE
);
CREATE TABLE Exercises (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL UNIQUE
);
CREATE TABLE Sets (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        language_fk INTEGER,
        FOREIGN KEY (language_fk) REFERENCES Languages(id)
);
CREATE TABLE Lessons (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        number INTEGER NOT NULL CHECK(number > 0) DEFAULT 1,
        set_fk INTEGER NOT NULL,
        FOREIGN KEY (set_fk) REFERENCES Sets(id)
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
        master_level INTEGER NOT NULL CHECK(master_level <= 100) DEFAULT -1,
        selected INTEGER NOT NULL CHECK(selected = 0 OR selected = 1) DEFAULT 0,
        FOREIGN KEY (definition_fk) REFERENCES Definitions(id),
        FOREIGN KEY (lesson_fk) REFERENCES Lessons(id),
        FOREIGN KEY (part_of_speech_fk) REFERENCES PartsOfSpeech(id),
        FOREIGN KEY (category_fk) REFERENCES Categories(id)
);
CREATE TABLE WordsTranslations (
       word_fk INTEGER NOT NULL,
       translation_fk INTEGER NOT NULL,
       PRIMARY KEY(word_fk, translation_fk),
       FOREIGN KEY(word_FK) REFERENCES Words(id),
       FOREIGN KEY (translation_fk) REFERENCES Translations(id)
);
CREATE TABLE ExampleSentences (
        word_fk INTEGER NOT NULL,
        sentence_fk INTEGER NOT NULL,
        FOREIGN KEY (word_fk) REFERENCES Words(id),
        FOREIGN KEY (sentence_fk) REFERENCES Sentences(id)
);

CREATE TABLE Repetitions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        word_fk INTEGER NOT NULL,
        month INTEGER NOT NULL CHECK(month >=1 AND month <=12),
        day INTEGER NOT NULL CHECK(day >= 1 AND day <=31),
        FOREIGN KEY(word_fk) REFERENCES Words(id)
);

CREATE TABLE Images (
      word_fk INTEGER PRIMARY KEY,
      bitmap BLOB NULL,
      path TEXT NULL,
      FOREIGN KEY (word_fk) REFERENCES Words(id)
  );