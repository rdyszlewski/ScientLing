INSERT INTO Languages (name, abbreviation, code)
VALUES ('english', 'ENG','en_US');
INSERT INTO Languages (name, abbreviation, code)
VALUES ('spanish', 'ESP', 'es_ES');
INSERT INTO Languages (name, abbreviation, code)
VALUES ('polish','POL','pl_PL');



INSERT INTO Categories(name)
VALUES ('animals');
INSERT INTO Categories(name)
VALUES ('activities');
INSERT INTO Categories(name)
VALUES ("work");
INSERT INTO Categories(name)
VALUES ('house');
INSERT INTO Categories(name)
VALUES  ('war');


INSERT INTO Sentences(content, translation)
VALUES ('This is very polite dog', 'To jest bardzo grzeczny piesek');
INSERT INTO Sentences(content, translation)
VALUES ('This is devil Tomek', 'To jest diabeł Tomek');

INSERT INTO Sets(name, language_l2_fk, language_l1_fk, catalog)
VALUES ('Angielski 1', 1, 3, "Angielski");
INSERT INTO Sets(name, language_l2_fk, language_l1_fk, catalog)
VALUES ('English for cgildren', 1,3, "English");
INSERT INTO Sets(name, language_l2_fk, language_l1_fk, catalog)
VALUES ('Espanol', 2,3, "Espanol");
INSERT INTO Sets(name, language_l2_fk, language_l1_fk, catalog)
VALUES ('Polski dla obcych', 3,1, "Polski");

INSERT INTO Lessons(name, number, set_fk)
VALUES ('', 0, 1);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('Lekcja 1', 1, 1);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('Lekcja 2', 2, 1);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('Lekcja 3', 3, 1);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('', 0, 2);
INSERT INTO Lessons(name, number, set_fk)
VALUES ('Lekcja o niczym', 1, 2);

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


INSERT INTO Words(content, transcription, definition_fk, lesson_fk, part_of_speech_fk, category_fk, difficult, selected)
VALUES ('dog', 'dog', 2,2,1,1,5,0); --1
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('great', 'grejt',2,1,5,0); --2
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('cause', 'kous',2,1,5,0); --3
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('old', 'old',3,1,5,0); --4
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult , selected)
VALUES ('time', 'tajm',2,1,5,0); --5
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult , selected)
VALUES ('men', 'men',2,1,5,0); --6
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult , selected)
VALUES ('need', 'nid',3,1,5,0); --7
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('father', 'fater',2,1,5,0); --8
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('head', 'hed',2,1,5,0); --9
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('point', 'pojnt',2,1,5,0); --10
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('world', 'łerld',3,1,5,0); --10
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('run', 'ran',2,1,5,0); --12
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('left', 'left',2,1,5,0); --13
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('car', 'kar',2,1,5,0); --14
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('book', 'buk',3,1,5,0); --15
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('room', 'rum',2,1,5,0); --16
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('dark', 'dark',3,1,5,0); --17
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('short', 'szort',3,1,5,0); --18
INSERT INTO Words(content, transcription, lesson_fk,  category_fk, difficult, selected)
VALUES ('blue', 'blu',3,1,5,0); --19

INSERT INTO Translations(content)
VALUES ('pies'); --1
INSERT INTO Translations(content)
VALUES ('świetnie'); --2
INSERT INTO Translations(content)
VALUES ('przyczyna'); --3
INSERT INTO Translations(content)
VALUES ('powód'); --4
INSERT INTO Translations(content)
VALUES ('stary'); --5
INSERT INTO Translations(content)
VALUES ('czas'); --6
INSERT INTO Translations(content)
VALUES ('mężczyzna'); --7
INSERT INTO Translations(content)
VALUES ('człowiek'); --8
INSERT INTO Translations(content)
VALUES ('potrzebować'); --9
INSERT INTO Translations(content)
VALUES ('potrzeba'); --10
INSERT INTO Translations(content)
VALUES ('ojciec'); --11
INSERT INTO Translations(content)
VALUES ('głowa'); --12
INSERT INTO Translations(content)
VALUES ('główny'); --13
INSERT INTO Translations(content)
VALUES ('wskazywać'); --14
INSERT INTO Translations(content)
VALUES ('punkt'); --15
INSERT INTO Translations(content)
VALUES ('świat'); --16
INSERT INTO Translations(content)
VALUES ('biegać'); --17
INSERT INTO Translations(content)
VALUES ('lewy'); --18
INSERT INTO Translations(content)
VALUES ('pozostawiony'); --19
INSERT INTO Translations(content)
VALUES ('samochód'); --20
INSERT INTO Translations(content)
VALUES ('książka'); --21
INSERT INTO Translations(content)
VALUES ('pokój'); --22
INSERT INTO Translations(content)
VALUES ('ciemny'); --23
INSERT INTO Translations(content)
VALUES ('mroczny'); --24
INSERT INTO Translations(content)
VALUES ('krótki'); --25
INSERT INTO Translations(content)
VALUES ('niebieski'); --26




INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (1,1);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (2,2);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (3,3);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (3,4);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (4,5);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (5,6);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (6,7);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (6,8);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (7,9);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (7,10);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (8,11);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (9,12);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (9,13);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (10,14);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (10,15);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (11,16);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (12,17);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (13,18);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (13,19);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (14,20);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (15,21);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (16,22);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (17,23);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (17,24);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (18,25);
INSERT INTO WordsTranslations (word_fk, translation_fk)
VALUES (19,26);

INSERT INTO ExampleSentences (word_fk, sentence_fk)
VALUES (1, 1);
INSERT INTO ExampleSentences (word_fk, sentence_fk)
VALUES (2, 2);
INSERT INTO ExampleSentences (word_fk, sentence_fk)
VALUES (1, 2);

INSERT INTO Hints(content)
VALUES ("pinata");
INSERT INTO Hints(content)
VALUES ("W języku brytyjskim używamy hound");
INSERT INTO Hints(content)
VALUES ("just cause - tylko powód");

INSERT INTO WordsHints(word_fk, hint_fk)
VALUES (1,2);
INSERT INTO WordsHints(word_fk, hint_fk)
VALUES (5,1);
INSERT INTO WordsHints(word_fk, hint_fk)
VALUES (3,3);







