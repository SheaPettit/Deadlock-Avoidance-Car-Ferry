import java.util.concurrent.ThreadLocalRandom;
public class Car implements Runnable {
  private static String[] questions = {"What is the meaning of life?", "Who is Java?", "What is your purpose", "Why not?", "What is 1 + 1?", "What is a linux?", "Can you recreate a computer from scratch?"};
  private static int count = 0;
  private int num;
  private Teacher teacher;
  private String previousAnswer;
  private String currentQuestion;
  public Student(Teacher teach){
    teacher = teach;
    num = ++count;
  }
  private void QuestionStart(String question) {
    currentQuestion = question;
    teacher.AnswerStart(this, question);
  }
  public String QuestionEnd(String answer) {
    System.out.println("Teacher answer to Student " + num + ": " + answer);
    previousAnswer = answer;
    return answer;
  }
  public String Speak(){
    System.out.println("Student " + num + " asks: " + currentQuestion);
    return currentQuestion;
  }
  public void run() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    for(int i = 0; i < 100; i++) {
      QuestionStart(questions[rand.nextInt(questions.length)]);
    }
  }
}
