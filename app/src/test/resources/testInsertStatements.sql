INSERT INTO Languages (name, abbreviation, code)
VALUES ('Angielski', 'ENG','en_EN');
INSERT INTO Languages (name, abbreviation, code)
VALUES ('Hiszpański', 'ESP', 'sp_SP');
INSERT INTO Languages (name, abbreviation, code)
VALUES ('Polski','POL','pl_PL');


INSERT INTO Categories(name, language_fk)
VALUES ('Zwierzęta', 3);
INSERT INTO Categories(name, language_fk)
VALUES ('Animmals',1);
INSERT INTO Categories(name, language_fk)
VALUES ("Czynności", 3);
INSERT INTO Categories(name, language_fk)
VALUES ('Work', 1);
INSERT INTO Categories(name, language_fk)
VALUES  ('War',1);

INSERT INTO Translations(content)
VALUES ('Pies'); --1
INSERT INTO Translations(content)
VALUES ('Piesek'); --2
INSERT INTO Translations(content)
VALUES ('Diabeł'); --3
INSERT INTO Translations(content)
VALUES ('Czort'); --4
INSERT INTO Translations(content)
VALUES ('Demon'); --5
INSERT INTO Translations(content)
VALUES ('Kaczka'); --6
INSERT INTO Translations(content)
VALUES ('Laptop'); --7
INSERT INTO Translations(content)
VALUES ('Lampa'); --8

INSERT INTO Sentences(content, translation)
VALUES ('This is very polite dog', 'To jest bardzo grzeczny piesek');
INSERT INTO Sentences(content, translation)
VALUES ('This is devil Tomek', 'To jest diabeł Tomek');

INSERT INTO Sets(name, language_fk)
VALUES ('Angielski 1', 1);
INSERT INTO Sets(name, language_fk)
VALUES ('English for cgildren', 1);
INSERT INTO Sets(name, language_fk)
VALUES ('Espanol', 2);
INSERT INTO Sets(name, language_fk)
VALUES ('Polski dla obcych', 3);

INSERT INTO Lessons(name, number, set_fk)
VALUES ('1', 1, 1);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('2', 2, 1);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('3', 3, 1);

INSERT INTO PartsOfSpeech(name)
VALUES ('noun');
INSERT INTO PartsOfSpeech(name)
VALUES ('verb');
INSERT INTO PartsOfSpeech(name)
VALUES ('adjective');

INSERT INTO Definitions(content)
VALUES ('the most powerful spirit of evil in Christianity, Judaism, and Islam who is often represented as the ruler of hell');
INSERT INTO Definitions(content, translation)
VALUES ('domestic mammal closely related with gray wold','udomowiony ssak blisko powiązany z szarym wilkiem');

INSERT INTO Words(content, transcription, definition_fk, lesson_fk, part_of_speech_fk, category_fk, difficult, master_level, selected)
VALUES ('dog', 'dog', 2,1,1,1,5,-1,0); --1
INSERT INTO Words(content, transcription, definition_fk, lesson_fk, part_of_speech_fk, category_fk, difficult, master_level, selected)
VALUES ('devil', 'dada', 1,1,2,2,1,50,1); --2
INSERT INTO Words(content, transcription,  lesson_fk, part_of_speech_fk, category_fk, difficult, master_level, selected)
VALUES ('laptop', 'laptop',1,2,2,3,-1,1); --3
INSERT INTO Words(content, transcription, definition_fk, lesson_fk, part_of_speech_fk, category_fk, difficult, master_level, selected)
VALUES ('lamp', 'lamp', 1,1,1,1,3,-1,1); --4

INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (1,1);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (1,2);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (2,3);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (2,4);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (1,3);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (3,7);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (4,8);




