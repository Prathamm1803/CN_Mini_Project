public class task5 {

    static final double INF = Double.POSITIVE_INFINITY;

    public static void main(String[] args) {
        // Hop 1: Source (0) -> Relays (1, 2, 3)
        double x = 2.0; // 0 -> 1
        double y = 3.0; // 0 -> 2
        double z = 2.0; // 0 -> 3

        // Relay-level ETX tables: [Dest 4, Dest 5, Both {4,5}]
        double[] node1 = { 3.0, INF, INF };
        double[] node2 = { 2.0, 2.0, task1.ETX2(2.0, 2.0) }; // [2.0, 2.0, 2.667]
        double[] node3 = { INF, 3.0, INF };

        double etxOnly4 = calculateThreeRelaySingleDest(
                x, y, z, node1[0], node2[0], node3[0], y + node2[0]); // 4.200

        double etxOnly5 = calculateThreeRelaySingleDest(
                x, y, z, node1[1], node2[1], node3[1], y + node2[1]); // 4.200

        double etxBoth4And5 = calculateThreeRelayMulticast(
                x, y, z, node2[2], y + node2[2]);                     // 5.667

        System.out.printf("Case 1 - ETX ({1,2,3} -> 4)     : %.3f%n", etxOnly4);
        System.out.printf("Case 2 - ETX ({1,2,3} -> 5)     : %.3f%n", etxOnly5);
        System.out.printf("Case 3 - ETX ({1,2,3} -> {4,5}) : %.3f%n", etxBoth4And5);
    }

    // Matches your handwritten [4] and [5] equation for (1, 2, 3) -> 4.200
    public static double calculateThreeRelaySingleDest(
            double x, double y, double z,
            double a, double b, double c, double fallbackCost) {

        double p1 = 1.0 / x, p2 = 1.0 / y, p3 = 1.0 / z;
        double f1 = 1.0 - p1, f2 = 1.0 - p2, f3 = 1.0 - p3;

        double caseFFF = f1 * f2 * f3;
        double caseSFF = p1 * f2 * f3 * (1.0 + (Double.isInfinite(a) ? fallbackCost : a));
        double caseFSF = f1 * p2 * f3 * (1.0 + (Double.isInfinite(b) ? fallbackCost : b));
        double caseFFS = f1 * f2 * p3 * (1.0 + (Double.isInfinite(c) ? fallbackCost : c));

        double caseSSF = p1 * p2 * f3 * (1.0 + Math.min(a, b));
        double caseSFS = p1 * f2 * p3 * (1.0 + Math.min(a, c));
        double caseFSS = f1 * p2 * p3 * (1.0 + Math.min(b, c));
        double caseSSS = p1 * p2 * p3 * (1.0 + Math.min(a, Math.min(b, c)));

        return (caseFFF + caseSFF + caseFSF + caseFFS
              + caseSSF + caseSFS + caseFSS + caseSSS)
              / (1.0 - caseFFF);
    }

    // Matches your handwritten [4, 5] equation for (1, 2, 3) -> 5.667 (5.67)
    public static double calculateThreeRelayMulticast(
            double x, double y, double z,
            double node2BothETX, double etxVia2Both) {

        double p1 = 1.0 / x, p2 = 1.0 / y, p3 = 1.0 / z;
        double f1 = 1.0 - p1, f2 = 1.0 - p2, f3 = 1.0 - p3;

        double caseFFF = f1 * f2 * f3;

        // Relay 2 missed -> fallback via (2) costs 1 + 5.667 = 6.667
        double caseSFF = p1 * f2 * f3 * (1.0 + etxVia2Both);
        double caseFFS = f1 * f2 * p3 * (1.0 + etxVia2Both);
        double caseSFS = p1 * f2 * p3 * (1.0 + etxVia2Both);

        // Relay 2 received -> broadcasts to {4,5} with cost 1 + 2.667 = 3.667
        double caseFSF = f1 * p2 * f3 * (1.0 + node2BothETX);
        double caseSSF = p1 * p2 * f3 * (1.0 + node2BothETX);
        double caseFSS = f1 * p2 * p3 * (1.0 + node2BothETX);
        double caseSSS = p1 * p2 * p3 * (1.0 + node2BothETX);

        return (caseFFF + caseSFF + caseFSF + caseFFS
              + caseSSF + caseSFS + caseFSS + caseSSS)
              / (1.0 - caseFFF);
    }
}