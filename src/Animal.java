import java.io.Serializable;

public class Animal implements Serializable {
    protected int BREEDING_AGE;
    protected int MAX_AGE;
    protected double BREEDING_PROBABILITY; //0.06
    protected int age;
    protected boolean alive;
    protected int MAX_LITTER_SIZE;
    protected Location location;
    
    public Animal(int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE){
        this.BREEDING_AGE = BREEDING_AGE;
        this.BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        this.MAX_AGE = MAX_AGE;
        this.MAX_LITTER_SIZE = MAX_LITTER_SIZE;
        age = 0;
        alive = true;
    }

    protected void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            alive = false;
        }
    }

    protected int breed() {
        int births = 0;
        if(canBreed() && Math.random() <= BREEDING_PROBABILITY) {
            births = (int)(Math.random()*MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    protected boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    protected boolean isAlive() {
        return alive;
    }

    protected void setLocation(int row, int col) {
        this.location = new Location(row, col);
    }

    protected void setLocation(Location location) {
        this.location = location;
    }

}
