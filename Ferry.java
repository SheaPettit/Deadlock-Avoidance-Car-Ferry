import java.util.Scanner;
import java.util.concurrent.Semaphore;
public class Ferry {
  private final Semaphore teachSem = new Semaphore(1, true);
  private static int answerNum = 0;
  public void AnswerStart(Student student, String question){
    try {
      teachSem.acquire();
    }
    catch (InterruptedException e){
      e.printStackTrace();
    }
    student.Speak();
    Scanner scan = new Scanner(System.in);
    System.out.print("Teacher response (leave blank to quit): ");
    String answer = scan.nextLine();
    if(answer.compareTo("") == 0) System.exit(0);
    AnswerEnd(student, answer);
  }
  private void AnswerEnd(Student student, String answer){
    student.QuestionEnd(answer);
    teachSem.release();
  }
}
