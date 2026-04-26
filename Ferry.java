import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
public class Ferry implements Runnable {
  private Semaphore carsBoarding;
  private Semaphore carsLeaving;
  private final Semaphore toIslandCapacity;
  private final Semaphore toMainlandCapacity;
  private final Semaphore islandLot;
  private final Semaphore mainland;
  private CarLocation inboundLocation;
  private int ferryCapacity;

  public Ferry(int ferryCap, int islandLotCap) {
    inboundLocation = CarLocation.MAINLAND;
    carsBoarding = new Semaphore(0, true);
    carsLeaving = new Semaphore(0, true);
    toIslandCapacity = new Semaphore(0, true);
    toMainlandCapacity = new Semaphore(0, true);
    islandLot = new Semaphore(islandLotCap, true);
    mainland = new Semaphore(0, true);
    ferryCapacity = ferryCap;
  }

  public CarLocation requestRide(CarLocation carLoc, int id){
    if(carLoc == CarLocation.MAINLAND){
      carLoc = requestToIsland(id);
    } else if(carLoc == CarLocation.ISLAND){
      carLoc = requestToMainland(id);
    }
    return carLoc;
  }

  private CarLocation requestToMainland(int id){
    try {
      toMainlandCapacity.acquire();
    }
    catch (InterruptedException e){
      e.printStackTrace();
      return CarLocation.ISLAND;
    }
    System.out.println("Car " + id + " boarded the ferry");
    carsBoarding.release();
    islandLot.release();
    return CarLocation.MAINLAND;
  }

  private CarLocation requestToIsland(int id){
    try {
      toIslandCapacity.acquire();
    }
    catch (InterruptedException e){
      e.printStackTrace();
      return CarLocation.MAINLAND;
    }
    System.out.println("Car " + id + " boarded the ferry");
    carsBoarding.release();
    return CarLocation.ISLAND;
  }


  public CarLocation endRide(CarLocation destination, int id) {
    if(destination == CarLocation.MAINLAND){
      try {
        mainland.acquire();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }else if(destination == CarLocation.ISLAND){
      try {
        islandLot.acquire();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }
    if(destination == CarLocation.MAINLAND)
      System.out.println("Car " + id + " arrived at the Mainland");
    if(destination == CarLocation.ISLAND)
      System.out.println("Car " + id + " arrived at the Island");
    carsLeaving.release();
    return destination;
  }

  public void run() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    int numCars = 0;
    int currentIslandLot = islandLot.availablePermits();
    while(true){
      try {
        Thread.sleep(rand.nextInt(2000,3001));
      } catch (Exception e) {
        e.printStackTrace();
      }
      if(inboundLocation == CarLocation.MAINLAND){
        System.out.println("Ferry arriving at the mainland");
        mainland.release(numCars);
        try {
          carsLeaving.acquire(numCars);
        } catch (InterruptedException e){
          e.printStackTrace();
        }
        currentIslandLot = islandLot.availablePermits();
        numCars = Math.min(ferryCapacity, Math.min(toIslandCapacity.getQueueLength(), currentIslandLot));
        try {
          islandLot.acquire(currentIslandLot);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        toIslandCapacity.release(numCars);
      } else if(inboundLocation == CarLocation.ISLAND){
        System.out.println("Ferry arriving at the island");
        islandLot.release(currentIslandLot);
        try {
          carsLeaving.acquire(numCars);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        numCars = Math.min(ferryCapacity, toMainlandCapacity.getQueueLength());
        toMainlandCapacity.release(numCars);
      }
      try {
        carsBoarding.acquire(numCars);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if(inboundLocation == CarLocation.MAINLAND){
        System.out.println("Ferry leaving the mainland, heading to the island");
        inboundLocation = CarLocation.ISLAND;
      } else if(inboundLocation == CarLocation.ISLAND){
        System.out.println("There are currently " + islandLot.availablePermits() + " spaces available on the island");
        System.out.println("Ferry leaving the island, heading to the mainland");
        inboundLocation = CarLocation.MAINLAND;
      }
    }
  }
}
