This program is ran by the IslandCarLot file.
java IslandCarLot <k> <m> <n>

Where: 
k is the number of cars there are on the mainland
m is the capacity of the ferry
n is the capacity of the Island Parking Lot


Algorithm:
All semaphores start empty apart from the islandLot being at n

Main Loop:

The ferry starts by traveling to the mainland over a 2-3 second trip. While waiting for the arrival,
cars will indicate that they want to board by calling requestRide with their location and id.
Cars indicate if they are ready after 1-15 seconds.

On arrival the ferry will release to the mainland semaphore the number of cars on board (starts at 0)
Then the ferry will wait until that same amount of permits are in the leaving semaphore

The released permits wake up the car threads waiting to leave to continue execution
The resumed cars will then release the leaving semaphore

Once all cars leave, the number of available spaces on the island are stored
The ferry acquires all of those spaces from the islandLot semaphore

Then the ferry accepts whichever is least: n, m, or the number of cars requesting a ride
To do so, the ferry releases permits on the toIsland semaphore equal to that ammount
Then the ferry waits until that same amount of permits are in the boarding semaphore

The released permits wake up the car threads to continue execution
The resumed cars will then release the boarding semaphore

Upon finishing the requestRide execution, the car will execute endRide
This will attempt to acquire from the islandLot semaphore

Once the # of accepted cars have released the boarding semaphore, the ferry resumes execution
The ferry leaves for the island.

Upon reaching the island, the ferry will release to the island semaphore the stored value
It will then wait until all cars have left the ferry through the leaving semaphore

Cars aboard will acquire the island lot semaphore, and release the leaving semaphore

Once all cars leave, the ferry will continue, accepting whichever is less, m or the number of cars on the island (starts at 0)
This is done by releasing that amount of the toMainland semaphore.
The ferry will wait until that number of cars release the boarding semaphore

Any cars boarding will in turn release the boarding, and islandLot semaphore
Once the car boards, it will attempt to acquire the mainland semaphore in endRide

The ferry continues execution and loops back to the start.
