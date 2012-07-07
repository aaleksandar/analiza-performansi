package analizaperformansi;

import java.util.ArrayList;

/**
 *
 * @author Aleksandar Abu-Samra
 */
abstract class Analiza {
	
	// kontrolne konstante
	protected static final long DEFAULT_TRAJANJE_SIMULACIJE = 18 * 60 * 60 * 1000; // [ms]
	protected static final int PROSEK_PROCESOR = 5; // [ms]
	protected static final int PROSEK_SDISK1 = 12; // [ms]
	protected static final int PROSEK_SDISK2 = 15; // [ms]
	protected static final int PROSEK_KDISK = 20; // [ms]
	protected static final int BROJ_SISTEMSKIH_DISKOVA = 2;
	protected int brojKorisnickihDiskova;
	
	// ulazni parametar
	protected int stepenMultiprogramiranja;
	
	// komponente	
	protected Komponenta procesor;
	protected ArrayList<Komponenta> sDiskArray;
	protected ArrayList<Komponenta> kDiskArray;
	
	// log
	protected LogFajl logFajl;
	
	public Analiza(int stepenMultiprogramiranja, String logFileName) {
		this.stepenMultiprogramiranja = stepenMultiprogramiranja;
				
		logFajl = new LogFajl(logFileName, this);
	}
	
	/**
	 * Resetuje podatke i priprema za novu simulaciju
	 * @param brojKorisnickihDiskova 
	 */
	public void novaAnaliza(int brojKorisnickihDiskova) {
		// startne vrednosti
		this.brojKorisnickihDiskova = brojKorisnickihDiskova;
		
		// napravi procesor
		procesor = new Komponenta(PROSEK_PROCESOR, stepenMultiprogramiranja);
		
		// napravi serverske diskove
		sDiskArray = new ArrayList<>();
		sDiskArray.add(new Komponenta(PROSEK_SDISK1));
		sDiskArray.add(new Komponenta(PROSEK_SDISK2));
		
		// napravi korisnicke diskove
		kDiskArray = new ArrayList<>();
		for (int i = 0; i < brojKorisnickihDiskova; i++) {
			kDiskArray.add(new Komponenta(PROSEK_KDISK));
		}
	}
	
	public abstract void izvrsiAnalizu();
	
	/**
	 * Vraća objekte svih komponenti analize
	 * @return 
	 */
	public ArrayList<Komponenta> getKomponente() {
		ArrayList<Komponenta> komponenteArray = new ArrayList<>();
		komponenteArray.add(procesor);
		komponenteArray.addAll(sDiskArray);
		komponenteArray.addAll(kDiskArray);
		
		return komponenteArray;
	}
	
	/**
	 * Postavlja dummy komponente
	 * @param komponenteArray 
	 */
	public void setDummies(ArrayList<Komponenta> komponenteArray) {
		int i = 0;
		procesor = komponenteArray.get(i++);
		
		sDiskArray = new ArrayList<>();
		sDiskArray.add(komponenteArray.get(i++));
		sDiskArray.add(komponenteArray.get(i++));
		
		kDiskArray = new ArrayList<>();
		for (int k = 0; k < brojKorisnickihDiskova; k++) {
			kDiskArray.add(komponenteArray.get(i++));
		}
	}
		
	/**
	 * Kreira log fajl na osnovu podataka iz simulacije
	 */
	public void snimiLog(int testId) {
		nadjiKriticniResurs();
		
		logFajl.noviTest(testId, brojKorisnickihDiskova);
		
		logFajl.upisiKomponentu("Procesor", procesor); // upisi procesor
		
		for (int i = 0; i < BROJ_SISTEMSKIH_DISKOVA; i++) { // upisi sistemske diskove
			logFajl.upisiKomponentu("Sistemski disk " + (i), sDiskArray.get(i));
		}
		
		for (int i = 0; i < brojKorisnickihDiskova; i++) { // upisi korisničke diskove
			logFajl.upisiKomponentu("Korisnički disk " + (i), kDiskArray.get(i));
		}
	}
	
	public void nadjiKriticniResurs() {
		double kriticnoIskoriscenje = 0;
		Komponenta kriticnaKomponenta = null;
		
		ArrayList<Komponenta> kriticniArray = new ArrayList<>();
		kriticniArray.add(procesor);
		for (int i = 0; i < BROJ_SISTEMSKIH_DISKOVA; i++) {
			kriticniArray.add(sDiskArray.get(i));
		}
		for (int i = 0; i < brojKorisnickihDiskova; i++) {
			kriticniArray.add(kDiskArray.get(i));
		}
		
		for (int i = 0; i < kriticniArray.size(); i++) {
			Komponenta k = kriticniArray.get(i);
			
			if (!k.isDummy()) {
				if (kriticnoIskoriscenje < k.getStatIskoriscenje(DEFAULT_TRAJANJE_SIMULACIJE)) {
					kriticnaKomponenta = k;
					kriticnoIskoriscenje = k.getStatIskoriscenje(DEFAULT_TRAJANJE_SIMULACIJE);
				}
			}
			else {
				if (kriticnoIskoriscenje < k.getDummyStatIskoriscenje()) {
					kriticnaKomponenta = k;
					kriticnoIskoriscenje = k.getDummyStatIskoriscenje();
				}
			}
		}
		
		if (kriticnaKomponenta != null) kriticnaKomponenta.setKriticniResurs(true);
	}
	
	public void zatvoriLog() {
		logFajl.zatvori();
	}
	
		
	/**
	 * Vraća ukupno vreme trajanja simulacije
	 * @return 
	 */
	public long getStatVremeTrajanjaSimulacije() {
		return DEFAULT_TRAJANJE_SIMULACIJE;
	}
	
	public int getStatStepenMultiprogramiranja() {
		return stepenMultiprogramiranja;
	}
	
	public double getStatVremeOdaziva() {
		if (!procesor.isDummy()) {
			return procesor.getStatVremeOdaziva();
		}
		else {
			return procesor.getDummyStatVremeOdaziva();
		}
	}
}
