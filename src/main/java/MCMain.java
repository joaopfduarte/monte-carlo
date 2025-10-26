import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class MCMain {

    private static final Logger log = Logger.getLogger(MCMain.class.getName());

    private static final int n = RandomGenerator.dimension();

    public static void main(String[] args) {

        log.info("Inicialização do algorítmo de Monte Carlo");

        log.info("Geração da tabela de probabilidades");
        long[][] matrix = generateMatrix(n, n);

        log.info("Lista de quantidade de rodadas");
        List<Integer> numberOfTurns = new ArrayList<>();
        numberOfTurns.add(100);
        numberOfTurns.add(1000);
        numberOfTurns.add(10000);
        numberOfTurns.add(100000);

        for (Integer numberOfTurn : numberOfTurns) {
            log.info("Numero de rodadas: " + numberOfTurn);

            int totalSum = IntStream.range(0, numberOfTurn)
                    .map(i -> diceSumRandomizer())
                    .sum();

            double simulAverage = (double) totalSum / numberOfTurn;
            double realAverage = 7.0;
            double error = Math.abs(simulAverage - realAverage);

            log.info(String.format(
                    "Resultado para %d rodadas -> Média simulada: %.4f | Média real: %.2f | Erro: %.4f",
                    numberOfTurn, simulAverage, realAverage, error
            ));
        }

    }

    private static long[][] generateMatrix(int n, int m) {
        long[][] matrix = new long[n][m];

        IntStream.range(0, n).forEach(i ->
                IntStream.range(0, m).forEach(j ->
                        matrix[i][j] = RandomGenerator.numGenerator()
                )
        );

        return matrix;
    }

    private static int diceSumRandomizer() {
        int diceOne = RandomGenerator.diceNum();
        int diceTwo = RandomGenerator.diceNum();

        return diceOne + diceTwo;
    }

}
