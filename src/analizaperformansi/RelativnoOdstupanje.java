package analizaperformansi;

import java.util.ArrayList;

/**
 *
 * @author Aleksandar Abu-Samra
 */
public class RelativnoOdstupanje extends Analiza {
	
	public RelativnoOdstupanje(int stepenMultiprogramiranja) {
		super(stepenMultiprogramiranja, "analiza_odstupanje." + stepenMultiprogramiranja + ".log");
	}

	@Override
	public void izvrsiAnalizu() {
	}

}
