import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.ReactionFunction;
import comp34120.ex2.Record;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A very simple leader implementation that only generates random prices
 * @author Xin
 */
final class SimpleLeader
	extends PlayerImpl
{
	/* The randomizer used to generate random price */
	private final Random m_randomizer = new Random(System.currentTimeMillis());

	private SimpleLeader()
		throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Simple Leader");
	}

	@Override
	public void goodbye()
		throws RemoteException
	{
		ExitTask.exit(500);
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException
	 */
	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		Record[] records = getPreviousRecords(m_type);

		m_platformStub.log(PlayerType.LEADER, "Records is: " + Arrays.toString(records));

		ReactionFunction followerReactionFunction = ReactionFunction.getFollowersReactionFunction(records);

		m_platformStub.log(PlayerType.LEADER, "Reaction function is: " + followerReactionFunction);

		double ourPrice = calculateBestStrategy(followerReactionFunction);

		m_platformStub.log(PlayerType.LEADER, "Our price is: " + ourPrice);

		m_platformStub.publishPrice(m_type, (float)ourPrice);

		m_platformStub.log(m_type, "" +calculateProfitOneDay(m_type,p_date-1));
	}


	@Override
	public void startSimulation(int p_steps)
			throws RemoteException
	{
		Record[] records = getPreviousRecords(m_type);

		double Pt = ReactionFunction.initializePt(records);
		ReactionFunction theta = ReactionFunction.initializeTheta(records);

		m_platformStub.log(m_type, "Initialized Pt = " + Pt);
		m_platformStub.log(m_type, "Initialized theta = " + theta);
	}

	@Override
	public void endSimulation()
			throws RemoteException
	{
		m_platformStub.log(m_type, "Bye!");
	}

	private double calculateProfitOneDay(PlayerType type, int day) throws RemoteException{
		Record record = m_platformStub.query(type,day);
		return (record.m_leaderPrice - record.m_cost)*(2 - record.m_leaderPrice + 0.3*record.m_followerPrice);
	}

	private Record[] getPreviousRecords(PlayerType type) throws RemoteException{
		Record[] records = new Record[100]; //todo

		for(int i = 0; i < 100; i++){
			records[i] = m_platformStub.query(type,i+1);
		}

		return records;
	}

	private static double calculateBestStrategy(ReactionFunction f){ //f is the opponent's reaction function
		return ((0.3 * f.getbStar()) - (0.3 * f.getaStar()) - 3) / ((0.6 * f.getbStar()) - 2);
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new SimpleLeader();
	}

	/**
	 * The task used to automatically exit the leader process
	 * @author Xin
	 */
	private static class ExitTask
		extends TimerTask
	{
		static void exit(final long p_delay)
		{
			(new Timer()).schedule(new ExitTask(), p_delay);
		}
		
		@Override
		public void run()
		{
			System.exit(0);
		}
	}
}
