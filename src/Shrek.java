import java.io.Serializable;
import java.util.List;

/**
 * A simple model of a shrek. Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kolling.  Modified by David Dobervich 2007-2013.
 * @version 2006.03.30
 */
public class Shrek extends Animal {
	private static final int RABBIT_FOOD_VALUE = 6;
	private static final int FOX_FOOD_VALUE = 2;
	private int foodLevel;

	public Shrek(boolean startWithRandomAge, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
		super(BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
		if (startWithRandomAge) {
			age = (int)(Math.random()*MAX_AGE);
			foodLevel = (int)(Math.random()*RABBIT_FOOD_VALUE);
		} else {
			// leave age at 0
			foodLevel = RABBIT_FOOD_VALUE;
		}
	}

	public void hunt(Field currentField, Field updatedField, List<Shrek> babyShrekStorage) {
		incrementAge();
		incrementHunger();
		if (alive) {
			// New foxes are born into adjacent locations.
			int births = breed();
			for (int b = 0; b < births; b++) {
				Shrek newShrek = new Shrek(false, 3, 100, 0.05,10);
				newShrek.setFoodLevel(this.foodLevel);
				babyShrekStorage.add(newShrek);
				Location loc = updatedField.randomAdjacentLocation(location);
				newShrek.setLocation(loc);
				updatedField.put(newShrek, loc);
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
			}else if (animal instanceof Fox) {
				Fox fox = (Fox) animal;
				if (fox.isAlive()) {
					fox.setEaten();
					foodLevel = FOX_FOOD_VALUE;
					return where;
				}
			}
		}

		return null;
	}


	public void setFoodLevel(int fl) {
		this.foodLevel = fl;
	}
}
