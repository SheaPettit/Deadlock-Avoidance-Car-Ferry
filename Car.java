import java.util.concurrent.ThreadLocalRandom;
public class Car implements Runnable {
  private static int count = 0;
  private int id;
  private CarLocation location;
  private Ferry ferry;

  public Car(Ferry ferry) {
    this.ferry = ferry;
    location = CarLocation.MAINLAND;
    id = count++;
  }

  public void run() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    while(location != CarLocation.ERROR){
      try {
        Thread.sleep(rand.nextInt(1000,15001));
      } catch (Exception e) {
        e.printStackTrace();
      }
      location = ferry.requestRide(location);
      printAtSea();
      location = ferry.endRide(location);
      printAtLocation();
    }
    if(location == CarLocation.ERROR){
      System.err.println("Error has occured");
      System.exit(1);
    }
  }

  private void printAtSea() {
    if(location == CarLocation.AT_SEA)
      System.out.println("Car " + id + " is at sea");
  }

  private void printAtLocation() {
    if(location == CarLocation.AT_SEA || location == CarLocation.ERROR)
      return;
    System.out.print("Car " + id + " is at ");
    if(location == CarLocation.MAINLAND)
      System.out.println("the Mainland");
    if(location == CarLocation.ISLAND)
      System.out.println("the Island");
  }
}
