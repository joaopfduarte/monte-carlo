import java.util.Random;

public class RandomGenerator {

    static Random random = new Random();

    public static long numGenerator() {
        return random.nextLong(80001) + 10000;
    }

    public static int dimension() {
        return random.nextInt(100);
    }

    public static int diceNum() {
        return random.nextInt(6) + 1;
    }

}
