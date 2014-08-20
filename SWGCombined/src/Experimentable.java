import java.io.IOException;


public interface Experimentable {
	public abstract int experiment(int[] iExperimentalIndex, int[] numExperimentationPointsUsed, Player thePlayer) throws IOException;
}
