import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Rabbit extends Animal {


    public Rabbit(boolean startWithRandomAge, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(BREEDING_AGE,MAX_AGE,BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        if(startWithRandomAge) {
            age = (int)(Math.random()*MAX_AGE);
        }
    }
    

    public void run(Field updatedField, List<Rabbit> babyRabbitStorage)
    {
        incrementAge();
        if(alive) {
            int births = breed();
            for(int b = 0; b < births; b++) {
                Rabbit newRabbit = new Rabbit(false, 5, 30, 0.6, 5);
                babyRabbitStorage.add(newRabbit);
                Location loc = updatedField.randomAdjacentLocation(location);
                newRabbit.setLocation(loc);
                updatedField.put(newRabbit, loc);
            }
            Location newLocation = updatedField.freeAdjacentLocation(location);
            // Only transfer to the updated field if there was a free location
            if(newLocation != null) {
                setLocation(newLocation);
                updatedField.put(this, newLocation);
            }
            else {
                // can neither move nor stay - overcrowding - all locations taken
                alive = false;
            }
        }
    }
    

    public void setEaten()
    {
        alive = false;
    }

}
