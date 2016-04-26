package comp34120.ex2;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class ReactionFunction {
    private double aStar, bStar;

    public ReactionFunction(double aStar, double bStar) {
        this.aStar = aStar;
        this.bStar = bStar;
    }

    public double getbStar() {
        return bStar;
    }

    public double getaStar() {
        return aStar;
    }

    public static ReactionFunction getFollowersReactionFunction(Record[] records){
        double lSum = getLeaderSum(records);
        double fSum = getFollowerSum(records);
        double lfpSum = getLeaderFollowerProductSum(records);
        double lSumSquared = getLeaderSumSquared(records);

        double aStarDenominator = (lSumSquared * fSum) - (lSum * lfpSum);
        double nominator = (records.length * lSumSquared) - (lSum * lSum);

        double aStar = aStarDenominator / nominator;

        double bStarDenominator = (records.length * lfpSum) - (lSum * fSum);

        double bStar = bStarDenominator / nominator;

        return new ReactionFunction(aStar, bStar);
    }

    private static double getLeaderSum(Record[] records){
        double d = 0;

        for(Record r : records){
            d += r.m_leaderPrice;
        }

        return d;
    }

    private static double getFollowerSum(Record[] records){
        double d = 0;

        for(Record r : records){
            d += r.m_followerPrice;
        }

        return d;
    }

    private static double getLeaderFollowerProductSum(Record[] records){
        double d = 0;

        for(Record r : records){
            d += (r.m_leaderPrice * r.m_followerPrice);
        }

        return d;
    }


    private static double getLeaderSumSquared(Record[] records){
        double d = 0;

        for(Record r : records){
            d += (r.m_leaderPrice * r.m_leaderPrice);
        }

        return d;
    }

    @Override
    public String toString() {
        return "R^ = " + aStar + " + " + bStar + "Ul";
    }
}
