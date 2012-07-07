package analizaperformansi;

import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Aleksandar Abu-Samra
 */
public class Komponenta {	
	private int prosecnoVremeObrade; // ms
	//private int brojProcesaNaCekanju;
	private LinkedList<Proces> procesQueue; // FIFO red procesa
	
	private boolean zavrsioObradu;
	private long protekloVremeTekuceObrade; // proteklo
	private long potrebnoVremeTekuceObrade; // potrebno
	private long ukupnoVremeObrade;
	private long protok;
	
	private long prosecanBrojProcesaNaCekanju;
	
	private long statUkupnoVremeZaOdaziv;
	private long statUkupnoProcesaZaOdaziv;
	
	// određivanje kritične komponente
	private boolean kriticniResurs;
	
	// dummy opcija - manualno setovanje statistika na komponenti
	private boolean dummy;
	private double dummyStatIskoriscenje;
	private double dummyStatProtok;
	private double dummyStatProsecanBrojPoslova;
	private double dummyStatVremeOdaziva;
	
	// random generator
	Random randomGenerator;
	

	public Komponenta(int prosecnoVremeObrade, int brojProcesaNaCekanju) {
		this.prosecnoVremeObrade = prosecnoVremeObrade;
		procesQueue = new LinkedList<>();
		
		for (int i = 0; i < brojProcesaNaCekanju; i++) {
			procesQueue.add(new Proces());
		}
		
		randomGenerator = new Random(System.currentTimeMillis());
		
		zavrsioObradu = false;
		ukupnoVremeObrade = 0;
		protekloVremeTekuceObrade = 0;
		potrebnoVremeTekuceObrade = getVremeObrade();
		protok = 0;
		dummy = false;
		kriticniResurs = false;
	}
		
	public Komponenta(int prosecnoVremeObrade) {
		this(prosecnoVremeObrade, 0);
	}
	
	/**
	 * Random vreme obrade, blisko prosečnom
	 * @return 
	 */
	public int getVremeObrade() {
		// random vreme obrade u rasponu = PROSEK +- 50%
		int randomVremeObrade = prosecnoVremeObrade / 2 + randomGenerator.nextInt(prosecnoVremeObrade);
		return randomVremeObrade;
	}
	
	/**
	 * Reguliše vreme od prethodne obrade
	 */
	public void tick() {
		// obavesti sve procese o vremenu
		for (Proces p : procesQueue) {
			p.tick();
		}
		
		// prati broj procesa na čekanju
		prosecanBrojProcesaNaCekanju += getBrojProcesaNaCekanju();
		
		// ima da se radi
		if (!procesQueue.isEmpty()) {
			protekloVremeTekuceObrade++;
			ukupnoVremeObrade++;

			if (protekloVremeTekuceObrade >= potrebnoVremeTekuceObrade) {
				zavrsioObradu = true;
				potrebnoVremeTekuceObrade = getVremeObrade();
				protekloVremeTekuceObrade = 0;
			}
		}
	}
	
	/**
	 * Ispituje da li je završena obrada i setuje podatak na false.
	 * @return zavrsioObradu
	 */
	public boolean isZavrsioObradu() {
		boolean ret = zavrsioObradu;
		if (zavrsioObradu) {
			protok++;
		}
		zavrsioObradu = false;
		return ret;
	}
	
	public int getBrojProcesaNaCekanju() {
		return procesQueue.size();
	}
	
	/**
	 * Stavlja proces u red
	 * @param p 
	 */
	public void putProces(Proces p) {
		procesQueue.add(p);
	}
	
	/**
	 * Stavlja proces u red i računa vreme odaziva sitema, tj prosečno trajanje ciklusa
	 * Poziva se na komponentama kroz koje proces mora da prođe, u ovom slučaju samo na procesoru
	 * @param p 
	 */
	public void putAndTrackProces(Proces p) {
		statUkupnoVremeZaOdaziv += p.getVremeIzvrsavanja();
		statUkupnoProcesaZaOdaziv ++;
		p.reset();
		
		procesQueue.add(p);
	}
	
	/**
	 * Vraća prvi proces u FIFO redu i izbacuje ga sa te pozicije
	 * @return 
	 */
	public Proces getProces() {
		return procesQueue.removeFirst();
	}

	/**
	 * [Statistika]
	 * Vraća informaciju o iskorišćenju komponente
	 * @return 
	 */
	public double getStatIskoriscenje(long vremeTrajanjaSimulacije) {
		return (double)ukupnoVremeObrade/vremeTrajanjaSimulacije;
	}
	
	/**
	 * [Statistika]
	 * Vraća informaciju o ukupnom protoku na komponenti [proc/sec]
	 * @return 
	 */
	public long getStatProtok() {
		return protok;
	}
	
	/**
	 * [Statistika]
	 * Vraća informaciju o prosečnom broju poslova na čekanju
	 * @return 
	 */
	public double getStatProsecanBrojPoslova(long vremeTrajanjaSimulacije) {
		return (double)prosecanBrojProcesaNaCekanju / vremeTrajanjaSimulacije;
	}
	
	/**
	 * [Statistika]
	 * Vraća informaciju o vremenu odaziva sistema, tj prosečnom trajanju ciklusa
	 * @return 
	 */
	public double getStatVremeOdaziva() {
		return (double)statUkupnoVremeZaOdaziv / statUkupnoProcesaZaOdaziv;
	}

	/**
	 * @return the dummyStatIskoriscenje
	 */
	public double getDummyStatIskoriscenje() {
		return dummyStatIskoriscenje;
	}

	/**
	 * @param dummyStatIskoriscenje the dummyStatIskoriscenje to set
	 */
	public void setDummyStatIskoriscenje(double dummyStatIskoriscenje) {
		this.dummyStatIskoriscenje = dummyStatIskoriscenje;
	}
	
	/**
	 * @return the dummyStatProtok
	 */
	public double getDummyStatProtok() {
		return dummyStatProtok * 1000;
	}

	/**
	 * @param dummyStatProtok the dummyStatProtok to set
	 */
	public void setDummyStatProtok(double dummyStatProtok) {
		this.dummyStatProtok = dummyStatProtok;
	}

	/**
	 * @return the dummyStatProsecanBrojPoslova
	 */
	public double getDummyStatProsecanBrojPoslova() {
		return dummyStatProsecanBrojPoslova;
	}

	/**
	 * @param dummyStatProsecanBrojPoslova the dummyStatProsecanBrojPoslova to set
	 */
	public void setDummyStatProsecanBrojPoslova(double dummyStatProsecanBrojPoslova) {
		this.dummyStatProsecanBrojPoslova = dummyStatProsecanBrojPoslova;
	}

	/**
	 * @return the dummyStatVremeOdaziva
	 */
	public double getDummyStatVremeOdaziva() {
		return dummyStatVremeOdaziva;
	}

	/**
	 * @param dummyStatVremeOdaziva the dummyStatVremeOdaziva to set
	 */
	public void setDummyStatVremeOdaziva(double dummyStatVremeOdaziva) {
		this.dummyStatVremeOdaziva = dummyStatVremeOdaziva;
	}

	/**
	 * @return the dummy
	 */
	public boolean isDummy() {
		return dummy;
	}

	/**
	 * @param dummy the dummy to set
	 */
	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	/**
	 * @return the kriticniResurs
	 */
	public boolean isKriticniResurs() {
		return kriticniResurs;
	}

	/**
	 * @param kriticniResurs the kriticniResurs to set
	 */
	public void setKriticniResurs(boolean kriticniResurs) {
		this.kriticniResurs = kriticniResurs;
	}
}
