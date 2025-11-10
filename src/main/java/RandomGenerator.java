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

    public static int sample(int[] values, double[] probs, double u) {
        double cum = 0.0;
        for (int i = 0; i < values.length; i++) {
            cum += probs[i];
            if (u <= cum) return values[i];
        }
        return values[values.length - 1];
    }

    public static int sampleWithSameU(int[] values, double[] probs, double u) {
        return sample(values, probs, u);
    }
}
