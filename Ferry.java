import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
public class Ferry implements Runnable {
  private Semaphore carsBoarding;
  private Semaphore carsLeaving;
  private final Semaphore toIslandCapacity;
  private final Semaphore toMainlandCapacity;
  private final Semaphore islandLot;
  private CarLocation inboundLocation;

  public Ferry() {
    inboundLocation = CarLocation.MAINLAND;
    carsBoarding = new Semaphore(0, true);
    carsLeaving = new Semaphore(0, true);
    toIslandCapactiy = new Semaphore(0, true);
    toMainlandCapacity = new Semaphore(0, true);
    islandLot = new Semaphore(50, true);
    mainland = new Semaphore(0, true);
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
    if(inboundLocation == CarLocation.ISLAND){
      try {
        islandLot.acquire();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }else if(inboundLocation == CarLocation.MAINLAND){
      try {
        mainland.acquire();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }
    location = inboundLocation;
    carsLeaving.release();
    return location;
  }

  public void run() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    int numCars = 0;
    int currentIslandLot;
    while(true){
      Thread.sleep(rand.nextInt(2000,3001));
      if(inboundLocation == CarLocation.MAINLAND){
        mainland.release(numCars);
        carsLeaving.acquire(numCars);
        currentIslandLot = islandLot.availablePermits();
        numCars = Math.min(20, Math.min(toIslandCapacity.getQueueLength(), currentIslandLot));
        islandLot.acquire(currentIslandLot);
        toIslandCapacity.release(numCars);
      } else if(inboundLocation == CarLocation.ISLAND){
        islandLot.release(currentIslandLot);
        carsLeaving.acquire(numCars);
        numCars = Math.min(20, toMainlandCapacity.getQueueLength());
        toMainlandCapacity.release(numCars);
      }
      carsBoarding.acquire(numCars);
      if(inboundLocation == CarLocation.MAINLAND){
        inboundLocation = CarLocation.ISLAND;
      } else if(inboundLocation == CarLocation.ISLAND){
        inboundLocation = CarLocation.MAINLAND;
      }
    }
  }
}
