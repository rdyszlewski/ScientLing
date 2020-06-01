package com.dyszlewskiR.edu.scientling.service.exercises;

import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.AnswersParams;
import com.dyszlewskiR.edu.scientling.models.params.QuestionsParams;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ExerciseManager {
    private final String LOG_TAG = "ExerciseManager";

    private int mNumQuestions;
    private int mCurrentQuestion;
    private List<Word> mQuestions;
    private Queue<Integer> mQuestionsQueue;
    private int mNumAnswers;
    private IExerciseType mExerciseType;
    private IExerciseDirection mExerciseDirection;
    private DataManager mDataManager;
    private VocabularySet mSet;
    private ExerciseParams mExerciseParams;

    private int mNumCorrectAnswers;
    private int mNumIncorrectAnswers;
    private Boolean[] mAnswersCorrectness;

    public ExerciseManager(ExerciseParams exerciseParams, DataManager dataManager){
        mExerciseParams = exerciseParams;
        mNumQuestions = exerciseParams.getNumberQuestion();
        mNumAnswers = exerciseParams.getNumberAnswers();
        long setId = exerciseParams.getSetId();
        mDataManager = dataManager;
        mSet = mDataManager.getSetById(setId);
        Language l1 = mDataManager.getLanguageById(mSet.getLanguageL1().getId());
        Language l2 = mDataManager.getLanguageById(mSet.getLanguageL2().getId());
        mSet.setLanguageL1(l1);
        mSet.setLanguageL2(l2);

        mNumCorrectAnswers = 0;
        mNumIncorrectAnswers = 0;
        //mIncorrectAnswers = new ArrayList<>();

        int repetitionData = exerciseParams.getRepetitionDate();
        setQuestions(setId, repetitionData, mNumQuestions);
    }

    private void setQuestions(long setId, int repetitionDate, int numQuestions){
        QuestionsParams questionsParams = new QuestionsParams(setId,repetitionDate, numQuestions);
        mQuestions = mDataManager.getQuestions(questionsParams);
        mNumQuestions = mQuestions.size();
        mQuestionsQueue = new LinkedList<>();
        fillQuestionQueue(mQuestionsQueue, mNumQuestions);
        //pobieramy pierwsze pytanie z kolejki
        mCurrentQuestion = mQuestionsQueue.poll();
        mAnswersCorrectness = new Boolean[mNumQuestions];
    }

    /** Metoda zapisująca w kolejce pozycje pytań na liście pytań. */
   private void fillQuestionQueue(Queue<Integer> queue, int numQuestions ){
       if(queue != null){
           for(int i = 0; i<numQuestions ; i++){
               queue.add(i);
           }
       }
   }

   public VocabularySet getSet(){return mSet;}

   /** Zwraca obence pytanie */
   public String getQuestion(){
       return mExerciseDirection.getQuestion(getCurrentQuestionWord());
   }

   public List<Word> getQuestions(){
        return mQuestions;
    }

   /**Metoda zwracająca obecne słówko*/
   private Word getCurrentQuestionWord(){
       return mQuestions.get(mCurrentQuestion);
   }

   public int getmNumCorrectAnswers(){
       return mNumCorrectAnswers;
   }

   public int getNumQuestions(){
       return mNumQuestions;
   }

   public int getNumAnswers(){
       return mNumAnswers;
   }

   public int getCurrentQuestionNumber(){
       return mCurrentQuestion;
   }

   public void setExerciseType(ExerciseType exerciseType){
       mExerciseType = ExerciseTypeFactory.create(exerciseType);
   }

   public void setExerciseDirection(ExerciseDirection exerciseDirection){
       mExerciseDirection = ExerciseDirectoryFactory.create(exerciseDirection);
   }

   public String getRecordName(){
       return getCurrentQuestionWord().getRecordName();
   }

   public boolean[] getAnswersCorrectness(){
        boolean[] correctnessArray = new boolean[mAnswersCorrectness.length];
       for(int i=0; i<mAnswersCorrectness.length; i++){
           if(mAnswersCorrectness[i] != null){
               correctnessArray[i] = mAnswersCorrectness[i];
           } else {
               correctnessArray[i] = false;
           }
       }
       return correctnessArray;
   }

    /** Metoda zwracająca słówka które zostały uruchomione podczas tego ćwiczenia
     * Jeżeli liczba pytań na które odpowiedział użytkownik jest większa lub równa
     * liczbie pytań w ćwiczeniu pobieramy wszystkie słówka.
     * Natomiast jeżeli ćwiczenie zostało przerwane i nie zostały udzielone odpowiedzi na wszystkie
     * pytania, zwracamy tylko tyle pytań na ile odpowiedział użytkownik
     * @return
     */
   public List<Word> getWordsToRepetition(){
       int doneQuestions = mNumCorrectAnswers + mNumIncorrectAnswers;
       if(doneQuestions >= mNumQuestions){
           return mQuestions;
       } else {
           List<Word> wordsToRepetition = new ArrayList<>();
           for(int i=0; i<doneQuestions; i++){
               wordsToRepetition.add(mQuestions.get(i));
           }
           return wordsToRepetition;
       }
   }

    /** Sprawdza czy odpowiedź udzielona przez użytkownika jest poprawna.
     * Sprawdzenie poprawności odpowiedzi odbywa się w dwóch etapach.
     * - pobranie poprawnej odpowiedzi przy pomocy metody getAnswer obiektu typu ExerciseDirection.
     *      Metoda ta zwraca zawartość słówka lub jego tłumaczenie w zależności od tego w jakim kierunku
     *      wykonywane jest ćwiczenie (tłumaczenie z ojczystego na język którego się uczy użytkowink
     *      lub odwrotnie)
     * - pobranie poprawności odpowiedzi przy pomocy metody checkAnswer klasy ExerciseType.
     *      Metoda pobiera odpowiedź użytkownika oraz poprawną odpowiedź zwróconą w poprzednim etapie.
     *      W zależności od typu wykonywanego ćwiczenia różne wartości mogą byc zaakceptowane
     *      (np. w przypadku ćwiczenia wpisywania mogą być uwzględniane literówki i nie trzeba podawać
     *      dokładnej odpowiedzi aby była uznawana za poprawną)
     * @param answer odpowiedź udzielona przez użytkownika
     * @return poprawność odpowiedzi
     */
   public boolean checkAnswer(String answer){
       String correctAnswer = mExerciseDirection.getAnswer(getCurrentQuestionWord());
       boolean correct = mExerciseType.checkAnswer(answer, correctAnswer);
       setAnswerResult(correct);
       return correct;
   }

    /** Metoda ustawia wartości związane z błędną lub poprawną odpowiedzią.
     * Jeżeli odpowiedź była zła:
     * - zwiększamy liczbę błędnych odpowiedzi
     * - dodajemy do listy błędnych odpowiedzi numer pytania
     * - numer pytania dodajemy na koniec kolejki, aby pytanie pojawiło się jeszcze raz na końcu
     * Następnie zapisujemy informacje w tabeli poprawności odpowiedzi
     * @param correct określa czy odpowiedź na pytanie była poprawna
     */
   private void setAnswerResult(boolean correct){
       if(!correct){
           mNumIncorrectAnswers++;
           //mIncorrectAnswers.add(mCurrentQuestion);
           mQuestionsQueue.add(mCurrentQuestion);
       } else {
           mNumCorrectAnswers++;
       }
       setCorrectness(mCurrentQuestion, correct);
   }

    /** Zapisuje poprawnośc odpowiedzi użytkownika. Jeżeli użytkownik odpowiada na to pytanie pierwszy
     * raz wartość tabeli mAnswerCorrectness jest null
     * - jeżeli użytkownik odpowie poprawnie zapisywana jest wartość true
     * - jeżeli użytkownik odpowie niepoprawnie zapisywana jest wartosć false
     * @param currentQuestion numer pytania dla którego ustawiamy poprawność
     * @param correct określa czy odpowiedź była poprawna
     */
   private void setCorrectness(int currentQuestion, boolean correct){
       if(mAnswersCorrectness[currentQuestion] == null){
           mAnswersCorrectness[currentQuestion] = correct;
       }
   }

    /** Metoda zwracająca dodatkowe odpowiedzi dla ćwiczenia wyboru.
     * - na początku nalezy stworzyć obiekt parametrów dla których będą pobrane odpowiedzi
     * - pobieramy listę pasujących do parametrów słówek. Wśród pobraneych słowek nie ma poprawnej odpowiedzi
     * - do pobranej listy dodajemy aktualne słówko, które zawiera poprawną odpowiedz
     * - mieszamy elementy na liście, aby występowały w losowej kolejności
     * - z listy tworzymy tablice która zawiera odpowiedzi. Tworzeniem tej tabeli zajmuje się metoda
     *      getAnswers z klasy ExerciseDierection. Metoda ta zwraca albo listę tłumaczeń albo listę
     *      treści słówek, w zależności w jakim kierunku odbywa się ćwiczenie
     * @return list odpowiedzi
     */
   public String[] getAnswers(){
       Word currentWord = getCurrentQuestionWord();
       AnswersParams answersParams = ExerciseParamsHelper.getAnswerParams(mExerciseParams, currentWord);
       List<Word> answersList = mDataManager.getAnswers(answersParams);
       answersList.add(getCurrentQuestionWord()); //dodajemy prawidłową odpowiedź
       Collections.shuffle(answersList); //mieszamy wartości na liście
       return mExerciseDirection.getAnswers(answersList);
   }

    /** Metoda zwracająca poprawną odpowiedź dla aktualnego pytania.
     * @return poprawna odpowiedź dla aktualnego pytania
     */
   public String getCorrectAnswer(){
       return mExerciseDirection.getAnswer(getCurrentQuestionWord());
   }

    /** Metoda zmieniająca pytanie na następne.
     * Jeżeli kolejka pytań nie jest pusta, pobieramy pierwszy element
     * @return określa czy znaleziono kolejne pytanie.
     */
    public boolean nextQuestion(){
        if(hasNextQuestion()){
            mCurrentQuestion = mQuestionsQueue.poll();
            return true;
        }
        return false;
    }

    /** Metoda sprawdza czy ćwiczenia ma jeszcze jakieś pytanie.
     * @return dostępność kolejnego pytania
     */
    public boolean hasNextQuestion(){
        return !mQuestionsQueue.isEmpty();
    }

    /** Metoda resetująca ćwicznie sprowadzając je do stanu początkowego */
    public void restart(){
        mNumCorrectAnswers = 0;
        mNumIncorrectAnswers = 0;
        mQuestionsQueue.clear();
        for(int i=0; i<mNumQuestions; i++){
            mQuestionsQueue.add(i);
        }
        mCurrentQuestion = mQuestionsQueue.poll();
    }

   public static class ExerciseParamsHelper{
       /** Metoda ustawiająca parametry potrzebne do pobrania odpowiedzi, które będą wykorzystywane
        * w ćwiczeniu wyboru.
        * - zestaw z jakiego pochodza odpowiedzi
        * - informacja czy odpowiedzi należy pobierać z konkretnej lekcji
        * - informacja czy odpowiedzi należy pobieraż z konkretnej kategorii
        * - słówko dla jakiego nie powinniśmy pobierać odpowiedzi, ponieważ to słówko jest aktualnym pytaniem
        * - tłumaczenia które nie powiiny zostać pobrane, ponieważ są tłumaczeniami pytania
        * - liczba odpowiedzi która powinna zostać pobrana(pobieramy o jedną odpowiedź mniej niż jest
        *       potrze4bna, ponieważ do pobranych odpowiedzi zostanie jeszcze dodana prawidłowa odpowiedź
        * @param exerciseParams parametry które okreslają aktualne ćwiczenie
        * @param word słówko dla którego pobieramy błędne odpowiedzi
        * @return parametry służące do pobrania odpowiedzi
        */
       public static AnswersParams getAnswerParams(ExerciseParams exerciseParams, Word word){
           AnswersParams params = new AnswersParams();
           params.setSetId(exerciseParams.getSetId());
           if(exerciseParams.isAnswerFromLesson()){
               params.setLessonId(word.getLessonId());
           }
           if(exerciseParams.isAnswerFromCategory()){
               if(word.getCategory() != null){
                   params.setCategoryId(word.getCategory().getId());
               }
           }
           params.setUsedContent(word.getContent());
           String[] usedTranslations = TranslationListConverter.toStringArray(word.getTranslations());
           params.setUsedTranslations(usedTranslations);
           params.setLimit(exerciseParams.getNumberAnswers() - 1);
           return params;
       }
   }

   private static class ExerciseTypeFactory{
       public static IExerciseType create(ExerciseType exerciseType){
           switch (exerciseType){
               case CHOOSING:
                   return new ChooseExercise();
               case KNOW:
                   return new KnowExercise();
               case WRITING:
                   return new WriteExercise();
               default:
                   return null;
           }
       }
   }

   private static class ExerciseDirectoryFactory{
       public static IExerciseDirection create(ExerciseDirection exerciseDirection){
           switch (exerciseDirection){
               case L1_TO_L2:
                   return new L1toL2();
               case L2_TO_L1:
                   return new L2toL1();
               default:
                   return null;
           }
       }
   }
}
