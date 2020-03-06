import java.io.Serializable;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

public class Fox extends Animal {

	private static final int RABBIT_FOOD_VALUE = 6;
	// The fox's food level, which is increased by eating rabbits.
	private int foodLevel;

	public Fox(boolean startWithRandomAge, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
		super(BREEDING_AGE,MAX_AGE,BREEDING_PROBABILITY, MAX_LITTER_SIZE);
		if (startWithRandomAge) {
			age = (int)(Math.random()*MAX_AGE);
			foodLevel = (int)(Math.random()*RABBIT_FOOD_VALUE);
		} else {
			// leave age at 0
			foodLevel = RABBIT_FOOD_VALUE;
		}
	}

	public void hunt(Field currentField, Field updatedField, List<Fox> babyFoxStorage) {
		incrementAge();
		incrementHunger();
		if (alive) {
			// New foxes are born into adjacent locations.
			int births = breed();
			for (int b = 0; b < births; b++) {
				Fox newFox = new Fox(false,3, 50, 0.15, 10);
				newFox.setFoodLevel(this.foodLevel);
				babyFoxStorage.add(newFox);
				Location loc = updatedField.randomAdjacentLocation(location);
				newFox.setLocation(loc);
				updatedField.put(newFox, loc);
			}
			// Move towards the source of food if found.
			Location newLocation = findFood(currentField, location);
			if (newLocation == null) { // no food found - move randomly
				newLocation = updatedField.freeAdjacentLocation(location);
			}
			if (newLocation != null) {
				setLocation(newLocation);
				updatedField.put(this, newLocation);
			} else {
				// can neither move nor stay - overcrowding - all locations
				// taken
				alive = false;
			}
		}
	}

	private void incrementHunger() {
		foodLevel--;
		if (foodLevel <= 0) {
			alive = false;
		}
	}


	private Location findFood(Field field, Location location) {
		List<Location> adjacentLocations = field.adjacentLocations(location);

		for (Location where : adjacentLocations) {
			Object animal = field.getObjectAt(where);
			if (animal instanceof Rabbit) {
				Rabbit rabbit = (Rabbit) animal;
				if (rabbit.isAlive()) {
					rabbit.setEaten();
					foodLevel = RABBIT_FOOD_VALUE;
					return where;
				}
			}
		}

		return null;
	}


	public void setFoodLevel(int fl) {
		this.foodLevel = fl;
	}

	public void setEaten()
	{
		alive = false;
	}
}
