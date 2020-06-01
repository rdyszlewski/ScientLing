SELECT W.id, W.content, W.selected, C.name AS category_name, POS.name AS part_of_speech_name
FROM Words W LEFT OUTER JOIN Categories C ON W.category_fk=C.id
    LEFT OUTER JOIN PartsOfSpeech POS ON W.part_of_speech_fk=POS.id;