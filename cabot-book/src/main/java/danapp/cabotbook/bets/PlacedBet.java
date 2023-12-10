package danapp.cabotbook.bets;



public class PlacedBet {

    public PlacedBet(String key, Double odds, int wager, String uniqueDescription) {
        this.key = key;
        this.odds = odds;
        this.wager = wager;
        this.uniqueDescription = uniqueDescription;
    }

    private String key;

    private Double odds = 0.0;

    private int wager = 0;

    private String uniqueDescription;

    private boolean didWin = false;

    public int totalBookRisk() {
        return (int) (wager*odds);
    }

    public boolean isDidWin() {
        return didWin;
    }

    public void setDidWin(boolean didWin) {
        this.didWin = didWin;
    }

    public String getUniqueDescription() {
        return uniqueDescription;
    }

    public void setUniqueDescription(String uniqueDescription) {
        this.uniqueDescription = uniqueDescription;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getOdds() {
        return odds;
    }

    public void setOdds(Double odds) {
        this.odds = odds;
    }

    public int getWager() {
        return wager;
    }

    public void setWager(int wager) {
        this.wager = wager;
    }
}
