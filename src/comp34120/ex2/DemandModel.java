package comp34120.ex2;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class DemandModel {

    public static double calculateDemandModel(ReactionFunction f){
        return ((0.3 * f.getbStar()) - (0.3 * f.getaStar()) - 3) / ((0.6 * f.getbStar()) - 2);
    }

}
