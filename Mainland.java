import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Mainland {
  public static void main(String[] args) {
    final int numStudents = 12;
    final ExecutorService exService = Executors.newFixedThreadPool(numStudents);
    final Teacher teach = new Teacher();
    for(int i = 0; i < numStudents; i++){
      Student s = new Student(teach);
      exService.execute(s);
    }
    exService.shutdown();
  }
}
