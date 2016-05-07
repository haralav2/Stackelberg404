package comp34120.ex2;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class ReactionFunctionTest {

    public static void main(String[] args) {
        Record[] records = new Record[]{
                new Record(1, 3, 2, 1),
                new Record(1, 4, 3, 1),
                new Record(1, 5, 3, 1),
                new Record(1, 6, 4, 1),
                new Record(1, 7, 6, 1)
        };

        ReactionFunction reaction = new ReactionFunction(records);

        System.out.println(reaction);
        System.out.println(reaction.getPt());


    }


}
