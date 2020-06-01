SELECT R.date, S.id, S.name, COUNT(1)
FROM Repetitions R LEFT OUTER JOIN Words W ON R.word_fk = W.id
LEFT OUTER JOIN Lessons L ON W.lesson_fk = L.id JOIN Sets S ON L.set_fk = S.id
WHERE S.id = ?
GROUP BY R.date, S.id