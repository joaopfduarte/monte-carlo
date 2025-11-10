import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.logging.Logger;

public class MCMain {

    private static final Logger log = Logger.getLogger(MCMain.class.getName());

    public static void main(String[] args) {
        log.info("==== Início: Algoritmo de Monte Carlo (forma base) ====\n");

        final int numberOfTrials = parseTrialsOrDefault(args, 100_000);
        final int sidesPerDie = 6; // exemplo clássico

        log.info("Rodadas: " + numberOfTrials);

        // Forma base: estimativa de média por Monte Carlo de uma variável aleatória qualquer
        double estimatedMean = monteCarloMean(numberOfTrials, () -> {
            int d1 = RandomGenerator.diceNum();
            int d2 = RandomGenerator.diceNum();
            return d1 + d2;
        });

        // Específico do exemplo de dois dados (6 faces)
        int[][] countMatrix = simulateDicePairCounts(numberOfTrials, sidesPerDie);
        double[][] probabilityMatrix = toProbabilities(countMatrix, numberOfTrials);
        double[] sumProbabilities = sumDistribution(probabilityMatrix, sidesPerDie);

        double theoreticalMean = 7.0; // E[sum of 2 fair dice]
        double absError = Math.abs(estimatedMean - theoreticalMean);

        log.info(String.format("Resultado -> Média simulada: %.4f | Média teórica: %.2f | Erro abs.: %.4f",
                estimatedMean, theoreticalMean, absError));
        log.info("Matriz de probabilidades (linhas=dado1, colunas=dado2), em %:");
        log.info(formatMatrixPercent(probabilityMatrix, sidesPerDie));
        log.info("Distribuição da soma (2..12), em %:");
        log.info(formatSumDistributionPercent(sumProbabilities, 2, 2 * sidesPerDie));

        log.info("\n==== Fim ====\n");
    }

    // ================= Forma base =================
    public static double monteCarloMean(int trials, DoubleSupplier experiment) {
        double sum = 0.0;
        for (int i = 0; i < trials; i++) {
            sum += experiment.getAsDouble();
        }
        return sum / Math.max(1, trials);
    }

    private static int parseTrialsOrDefault(String[] args, int fallback) {
        if (args != null && args.length > 0) {
            try {
                int v = Integer.parseInt(args[0]);
                if (v > 0) return v;
            } catch (NumberFormatException ignored) {
                log.warning("Argumento inválido para número de rodadas. Usando padrão: " + fallback);
            }
        }
        return fallback;
    }

    // ================= Implementação específica do exercício ===============
    private static int[][] simulateDicePairCounts(int trials, int sidesPerDie) {
        int[][] counts = new int[sidesPerDie][sidesPerDie];
        for (int t = 0; t < trials; t++) {
            int d1 = RandomGenerator.diceNum(); // 1..6
            int d2 = RandomGenerator.diceNum(); // 1..6
            counts[d1 - 1][d2 - 1]++;
        }
        return counts;
    }

    // =============== Implementação específica da prova 01 =========
    private static int[][] simulateCopierRuns(int rounds, Random rng) {
        int[][] counts = new int[rounds][rounds];
        for (int r = 0; r < rounds; r++) {
            for (int i = 0; i < rounds; i++) {
                if (i == r) continue;
                counts[r][i] = rng.nextInt(rounds);
            }
        }
        return counts;
    }

    private static double[][] toProbabilities(int[][] counts, int total) {
        int rows = counts.length;
        int cols = counts[0].length;
        double[][] probabilities = new double[rows][cols];
        double denom = (double) total;
        if (denom <= 0) return probabilities;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                probabilities[i][j] = counts[i][j] / denom;
            }
        }
        return probabilities;
    }

    private static double[] sumDistribution(double[][] probabilities, int sidesPerDie) {
        int minSum = 2;
        int maxSum = 2 * sidesPerDie;
        double[] sumProb = new double[maxSum + 1]; // índices 0..maxSum, usamos 2..maxSum
        for (int i = 0; i < sidesPerDie; i++) {
            for (int j = 0; j < sidesPerDie; j++) {
                int s = (i + 1) + (j + 1);
                sumProb[s] += probabilities[i][j];
            }
        }
        // normalização defensiva por possíveis arredondamentos
        double total = 0.0;
        for (int s = minSum; s <= maxSum; s++) total += sumProb[s];
        if (total > 0) {
            for (int s = minSum; s <= maxSum; s++) sumProb[s] /= total;
        }
        return sumProb;
    }

    private static String formatMatrixPercent(double[][] probabilities, int sidesPerDie) {
        StringBuilder sb = new StringBuilder();
        sb.append("     ");
        for (int j = 1; j <= sidesPerDie; j++) sb.append(String.format("  %2d  ", j));
        sb.append(" | Linha%\n");
        sb.append("-----");
        for (int j = 0; j < sidesPerDie; j++) sb.append("-----");
        sb.append("-|------%\n");

        for (int i = 0; i < sidesPerDie; i++) {
            double rowSum = 0.0;
            for (int j = 0; j < sidesPerDie; j++) rowSum += probabilities[i][j];
            sb.append(String.format(" %2d |", i + 1));
            for (int j = 0; j < sidesPerDie; j++) {
                sb.append(String.format(" %4.1f", probabilities[i][j] * 100.0)).append('%').append(' ');
            }
            sb.append("| ").append(String.format("%5.1f", rowSum * 100.0)).append('%').append('\n');
        }

        sb.append("-----");
        for (int j = 0; j < sidesPerDie; j++) sb.append("-----");
        sb.append("-|------%\n");

        sb.append(" Tot:");
        for (int j = 0; j < sidesPerDie; j++) {
            double colTotal = 0.0;
            for (int i = 0; i < sidesPerDie; i++) colTotal += probabilities[i][j];
            sb.append(String.format(" %4.1f", colTotal * 100.0)).append('%').append(' ');
        }
        sb.append("| 100.0%\n");
        return sb.toString();
    }

    private static String formatSumDistributionPercent(double[] sumProb, int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append("Soma:   ");
        for (int s = start; s <= end; s++) sb.append(String.format(" %2d  ", s));
        sb.append('\n');
        sb.append("Prob%:  ");
        for (int s = start; s <= end; s++) sb.append(String.format(" %4.1f", sumProb[s] * 100.0)).append('%').append(' ');
        return sb.toString();
    }
}
