import java.io.Serializable;

public class Animal implements Serializable {
    protected int BREEDING_AGE;
    protected int MAX_AGE;
    protected double BREEDING_PROBABILITY; //0.06
    
    public Animal(int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY){
        this.BREEDING_AGE = BREEDING_AGE;
        this.BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        this.MAX_AGE = MAX_AGE;
    }


}
