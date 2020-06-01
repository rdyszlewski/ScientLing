SELECT W.*, D.content AS definition_content, D.translation AS definition_translation,
	POS.name AS part_of_speech_name, C.name AS category_name,
	L.name AS lesson_name, L.number AS lesson_number, L.set_fk AS lesson_set
FROM Words W LEFT OUTER JOIN Definitions D ON W.definition_fk = D.id
	LEFT OUTER JOIN PartsOfSpeech POS ON W.part_of_speech_fk = POS.id
	LEFT OUTER JOIN Categories C ON W.category_fk = C.id
	LEFT OUTER JOIN Lessons L ON W.lesson_fk=L.id