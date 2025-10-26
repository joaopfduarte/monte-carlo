import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MCMain {

    private static final Logger log = Logger.getLogger(MCMain.class.getName());

    public static void main(String[] args) {
        log.info("==== Início: Algoritmo de Monte Carlo para 2 dados ====\n");

        List<Integer> numberOfTurns = new ArrayList<>();
        numberOfTurns.add(100);
        numberOfTurns.add(1000);
        numberOfTurns.add(10000);
        numberOfTurns.add(100000);

        for (Integer trials : numberOfTurns) {
            log.info("-- Rodadas: " + trials);

            int[][] counts = simulateCounts(trials);

            double[][] probs = toProbabilities(counts, trials);

            double simulatedAvg = expectedValueFromProbs(probs);
            double theoreticalAvg = 7.0;
            double error = Math.abs(simulatedAvg - theoreticalAvg);

            double[] sumDist = sumDistribution(probs);

            log.info(String.format("Resultado -> Média simulada: %.4f | Média teórica: %.2f | Erro: %.4f", simulatedAvg, theoreticalAvg, error));
            log.info("Matriz de probabilidades (linhas=dado1, colunas=dado2), em %:");
            log.info(formatMatrixPercent(probs));
            log.info("Distribuição da soma (2..12), em %:");
            log.info(formatSumDistributionPercent(sumDist));
            log.info("");
        }

        log.info("==== Fim ====\n");
    }

    private static int[][] simulateCounts(int trials) {
        int[][] counts = new int[6][6];
        for (int t = 0; t < trials; t++) {
            int d1 = RandomGenerator.diceNum();
            int d2 = RandomGenerator.diceNum();
            counts[d1 - 1][d2 - 1]++;
        }
        return counts;
    }

    private static double[][] toProbabilities(int[][] counts, int total) {
        double[][] probs = new double[6][6];
        double denom = (double) total;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                probs[i][j] = counts[i][j] / denom;
            }
        }
        return probs;
    }

    private static double expectedValueFromProbs(double[][] probs) {
        double ev = 0.0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                int sum = (i + 1) + (j + 1);
                ev += sum * probs[i][j];
            }
        }
        return ev;
    }

    private static double[] sumDistribution(double[][] probs) {
        double[] dist = new double[13]; // índices 2..12 usados
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                int s = (i + 1) + (j + 1);
                dist[s] += probs[i][j];
            }
        }
        return dist;
    }

    private static String formatMatrixPercent(double[][] probs) {
        StringBuilder sb = new StringBuilder();
        sb.append("     ");
        for (int j = 1; j <= 6; j++) sb.append(String.format("  %2d  ", j));
        sb.append(" | Linha%\n");
        sb.append("-----");
        for (int j = 0; j < 6; j++) sb.append("-----");
        sb.append("-|------%\n");

        for (int i = 0; i < 6; i++) {
            double rowSum = 0.0;
            for (int j = 0; j < 6; j++) rowSum += probs[i][j];
            sb.append(String.format(" %2d |", i + 1));
            for (int j = 0; j < 6; j++) {
                sb.append(String.format(" %4.1f", probs[i][j] * 100.0)).append('%').append(' ');
            }
            sb.append("| ").append(String.format("%5.1f", rowSum * 100.0)).append('%').append('\n');
        }

        sb.append("-----");
        for (int j = 0; j < 6; j++) sb.append("-----");
        sb.append("-|------%\n");

        double colTotal;
        sb.append(" Tot:");
        for (int j = 0; j < 6; j++) {
            colTotal = 0.0;
            for (int i = 0; i < 6; i++) colTotal += probs[i][j];
            sb.append(String.format(" %4.1f", colTotal * 100.0)).append('%').append(' ');
        }
        sb.append("| 100.0%\n");
        return sb.toString();
    }

    private static String formatSumDistributionPercent(double[] dist) {
        StringBuilder sb = new StringBuilder();

        sb.append("Soma:   ");
        for (int s = 2; s <= 12; s++) {
            sb.append(String.format(" %2d  ", s));
        }
        sb.append('\n');
        sb.append("Prob%:  ");
        for (int s = 2; s <= 12; s++) {
            sb.append(String.format(" %4.1f", dist[s] * 100.0)).append('%').append(' ');
        }
        return sb.toString();
    }
}
