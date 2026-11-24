public class task6 {

    static final double INF = Double.POSITIVE_INFINITY;

    public static void main(String[] args) {
        // Test with Forwarding Set {1, 2} from your diagram
        double[] node1 = { 3.0, INF, INF };
        double[] node2 = { 2.0, 2.0, task1.ETX2(2.0, 2.0) }; // [2, 2, 2.667]

        double etx12 = calculateTwoRelayTwoDest(2.0, 3.0, node1, node2);
        System.out.printf("ETX ({1, 2} -> {4, 5}) = %.3f%n", etx12); // 6.833 (6.835)
    }

    public static double calculateTwoRelayTwoDest(
            double x, double y,
            double[] relayA, double[] relayB) {

        double pA = 1.0 / x;
        double pB = 1.0 / y;
        double fA = 1.0 - pA;
        double fB = 1.0 - pB;

        // Case FF: Neither relay receives -> Source retransmits
        double caseFF = fA * fB;

        // Case SF: Only Relay A receives
        // Best way to finish both: either A handles both, or A handles one & Source
        // sends to B for the other
        double costOnlyA = Math.min(
                relayA[2],
                Math.min(relayA[0] + y + relayB[1], relayA[1] + y + relayB[0]));
        double caseSF = pA * fB * (1.0 + costOnlyA);

        // Case FS: Only Relay B receives
        double costOnlyB = Math.min(
                relayB[2],
                Math.min(relayB[0] + x + relayA[1], relayB[1] + x + relayA[0]));
        double caseFS = fA * pB * (1.0 + costOnlyB);

        // Case SS: Both Relay A and Relay B receive
        // Choose minimum among: A handles both, B handles both, or split (A->4 & B->5 /
        // A->5 & B->4)
        double costBoth = Math.min(
                Math.min(relayA[2], relayB[2]),
                Math.min(relayA[0] + relayB[1], relayA[1] + relayB[0]));
        double caseSS = pA * pB * (1.0 + costBoth);

        return (caseFF + caseSF + caseFS + caseSS) / (1.0 - caseFF);
    }
}