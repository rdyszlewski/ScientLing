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
       translation TEXT NOT NULL UNIQUE
);