import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class IslandCarLot {
  public static void main(String[] args) {
    final int numCars = Integer.parseInt(args[0]);
    final ExecutorService exService = Executors.newFixedThreadPool(numCars);
    final Ferry ferry = new Ferry(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    final Thread ferryThread = new Thread(ferry);
    ferryThread.start();

    for(int i = 0; i < numCars; i++){
      Car c = new Car(ferry);
      exService.execute(c);
    }
    exService.shutdown();
  }
}
