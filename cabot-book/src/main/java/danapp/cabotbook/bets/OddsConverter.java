package danapp.cabotbook.bets;

public class OddsConverter {
    public static double decimalToAmerican(double decimalOdds) {
        if (decimalOdds >= 2.0) {
            return (int) Math.round((decimalOdds - 1) * 100);
        } else if (decimalOdds < 2.0 && decimalOdds > 1.0) {
            return (int) Math.round(-100 / (decimalOdds - 1));
        } else {
            return 0;
        }
    }

    public static double americanToDecimal(double americanOdds) {
        if (americanOdds > 0) {
            return (americanOdds / 100) + 1;
        } else if (americanOdds < 0) {
            return (100 / Math.abs(americanOdds)) + 1;
        } else {
            throw new IllegalArgumentException("Invalid American odds format");
        }
    }
}