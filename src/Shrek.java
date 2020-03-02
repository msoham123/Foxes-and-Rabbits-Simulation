import java.io.Serializable;
import java.util.List;

/**
 * A simple model of a shrek. Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kolling.  Modified by David Dobervich 2007-2013.
 * @version 2006.03.30
 */
public class Shrek implements Serializable {
	// Characteristics shared by all foxes (static fields).
	private static final int BREEDING_AGE = 3;
	// The age to which a shrek can live.
	private static final int MAX_AGE = 50;
	// The likelihood of a shrek breeding.
	private static final double BREEDING_PROBABILITY = 0.15;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 6;
	// The food value of a single rabbit. In effect, this is the
	// number of steps a shrek can go before it has to eat again.
	private static final int RABBIT_FOOD_VALUE = 6;
	private static final int FOX_FOOD_VALUE = 2;
	// A shared random number generator to control breeding.

	// Individual characteristics (instance fields).

	// The shrek's age.
	private int age;
	// Whether the shrek is alive or not.
	private boolean alive;
	// The shrek's position
	private Location location;
	// The shrek's food level, which is increased by eating rabbits.
	private int foodLevel;

	/**
	 * Create a shrek. A shrek can be created as a new born (age zero and not
	 * hungry) or with random age.
	 *
	 * @param startWithRandomAge
	 *            If true, the shrek will have random age and hunger level.
	 */
	public Shrek(boolean startWithRandomAge) {
		age = 0;
		alive = true;
		if (startWithRandomAge) {
			age = (int)(Math.random()*MAX_AGE);
			foodLevel = (int)(Math.random()*RABBIT_FOOD_VALUE);
		} else {
			// leave age at 0
			foodLevel = RABBIT_FOOD_VALUE;
		}
	}

	/**
	 * This is what the shrek does most of the time: it hunts for rabbits. In the
	 * process, it might breed, die of hunger, or die of old age.
	 * 
	 * @param currentField
	 *            The field currently occupied.
	 * @param updatedField
	 *            The field to transfer to.
	 * @param babyShrekStorage
	 *            A list to add newly born foxes to.
	 */
	public void hunt(Field currentField, Field updatedField, List<Shrek> babyShrekStorage) {
		incrementAge();
		incrementHunger();
		if (alive) {
			// New foxes are born into adjacent locations.
			int births = breed();
			for (int b = 0; b < births; b++) {
				Shrek newShrek = new Shrek(false);
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

	/**
	 * Increase the age. This could result in the shrek's death.
	 */
	private void incrementAge() {
		age++;
		if (age > MAX_AGE) {
			alive = false;
		}
	}

	/**
	 * Make this shrek more hungry. This could result in the shrek's death.
	 */
	private void incrementHunger() {
		foodLevel--;
		if (foodLevel <= 0) {
			alive = false;
		}
	}

	/**
	 * Tell the shrek to look for rabbits adjacent to its current location. Only
	 * the first live rabbit is eaten.
	 * 
	 * @param field
	 *            The field in which it must look.
	 * @param location
	 *            Where in the field it is located.
	 * @return Where food was found, or null if it wasn't.
	 */
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

	/**
	 * Generate a number representing the number of births, if it can breed.
	 * 
	 * @return The number of births (may be zero).
	 */
	private int breed() {
		int numBirths = 0;
		if (canBreed() && Math.random() <= BREEDING_PROBABILITY) {
			numBirths = (int)(Math.random()*MAX_LITTER_SIZE) + 1;
		}
		return numBirths;
	}

	/**
	 * A shrek can breed if it has reached the breeding age.
	 */
	private boolean canBreed() {
		return age >= BREEDING_AGE;
	}

	/**
	 * Check whether the shrek is alive or not.
	 * 
	 * @return True if the shrek is still alive.
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Set the animal's location.
	 * 
	 * @param row
	 *            The vertical coordinate of the location.
	 * @param col
	 *            The horizontal coordinate of the location.
	 */
	public void setLocation(int row, int col) {
		this.location = new Location(row, col);
	}

	/**
	 * Set the shrek's location.
	 * 
	 * @param location
	 *            The shrek's location.
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	public void setFoodLevel(int fl) {
		this.foodLevel = fl;
	}
}
