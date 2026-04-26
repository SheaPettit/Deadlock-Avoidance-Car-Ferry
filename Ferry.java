import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
public class Ferry implements Runnable {
  private Semaphore carsBoarding;
  private final Semaphore toIslandCapacity;
  private final Semaphore toMainlandCapacity;
  private final Semaphore islandLot;
  private CarLocation inboundLocation;

  public Ferry() {
    inboundLocation = CarLocation.MAINLAND;
    carsBoarding = new Semaphore(0, true);
    toIslandCapactiy = new Semaphore(0, true);
    toMainlandCapacity = new Semaphore(0, true);
    islandLot = new Semaphore(50, true);
  }

  public CarLocation requestRide(CarLocation carLoc){
    if(carLoc == CarLocation.MAINLAND){
      carLoc = requestToIsland();
    } else if(carLoc == CarLocation.ISLAND){
      carLoc = requestToMainland();
    }
    return carLoc;
  }

  private CarLocation requestToMainland(){
    try {
      toMainlandCapacity.acquire();
    }
    catch (InterruptedException e){
      e.printStackTrace();
      return CarLocation.ISLAND;
    }
    carsBoarding.release();
    islandLot.release();
    return CarLocation.AT_SEA;
  }

  private CarLocation requestToIsland(){
    try {
      toIslandCapacity.acquire();
    }
    catch (InterruptedException e){
      e.printStackTrace();
      return CarLocation.MAINLAND;
    }
    carsBoarding.release();
    return CarLocation.AT_SEA;
  }


  public CarLocation endRide(CarLocation location) {
    if(location != CarLocation.AT_SEA)
      return CarLocation.ERROR;
    try {
      islandLot.acquire();
    } catch (InterruptedException e){
      e.printStackTrace();
    }
    return inboundLocation;
  }

  public void run() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    while(true){
      Thread.sleep(rand.nextInt(2000,3001));
    }
  }
}
