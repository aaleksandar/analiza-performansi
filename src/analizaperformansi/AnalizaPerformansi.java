package analizaperformansi;

import java.util.ArrayList;

/**
 *
 * @author Aleksandar Abu-Samra
 */
public class AnalizaPerformansi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Greška: stepen multiprogramiranja nije prosleđen!");
			return;
		} 
		if (args.length > 1) {
			System.err.println("Greška: prosleđeno više od jednog parametra!");
			return;
		}
		
		int stepenMultiprogramiranja;
		
		try {
			stepenMultiprogramiranja = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e) {
			stepenMultiprogramiranja = 10; // default
		}
		 
		// banka procesiranih komponenti
		ArrayList<Komponenta>[] komponenteSimulacijeMatrix = new ArrayList[7];
		ArrayList<Komponenta>[] komponenteBjuzenaMatrix = new ArrayList[7];
		ArrayList<Komponenta>[] komponenteOdstupanjaMatrix = new ArrayList[7]; 
		for (int i=0; i<7; i++) {
			komponenteSimulacijeMatrix[i] = new ArrayList<>();
			komponenteBjuzenaMatrix[i] = new ArrayList<>();
			komponenteOdstupanjaMatrix[i] = new ArrayList<>();
		}
		
		/*
		 * Simulacija (1a)
		 */
		Simulator sim = new Simulator(stepenMultiprogramiranja);
		
		// loop za K (broj korisničkih diskova) od 2 do 8
		for (int k = 2; k <= 8; k++) {
			sim.novaAnaliza(k);
			sim.izvrsiAnalizu();
			komponenteSimulacijeMatrix[k-2].addAll(sim.getKomponente());
			sim.snimiLog(k-1);
		}	
		
		sim.zatvoriLog();
		
		
		/*
		 * Bjuzenova metoda (1b)
		 */
		BjuzenovaMetoda bjuzen = new BjuzenovaMetoda(stepenMultiprogramiranja);
		
		for (int k = 2; k <= 8; k++) {
			bjuzen.novaAnaliza(k);
			bjuzen.izvrsiAnalizu();
			komponenteBjuzenaMatrix[k-2].addAll(bjuzen.getKomponente());
			bjuzen.snimiLog(k-1);
		}
		
		bjuzen.zatvoriLog();
		
			
		/*
		 * Relativno odstupanje (1c)
		 * TODO računanje treba spakovati u klasu... ali je glomazno i ružno i mrzi me i deadline je blizu...
		 */
		
		// računanje pojedinačnih odstupanja i stavljanje u matricu odstupanja
		for (int k = 2; k <= 8; k++) {
			ArrayList<Komponenta> simArray = komponenteSimulacijeMatrix[k-2];
			ArrayList<Komponenta> bjuzArray = komponenteBjuzenaMatrix[k-2];
			
			for (int i = 0; i < k+3; i++) {
				double statIskoriscenje = 
						bjuzArray.get(i).getDummyStatIskoriscenje() / simArray.get(i).getStatIskoriscenje(sim.getStatVremeTrajanjaSimulacije());
				double statProsecanBrojPoslova =
						bjuzArray.get(i).getDummyStatProsecanBrojPoslova() / simArray.get(i).getStatProsecanBrojPoslova(sim.getStatVremeTrajanjaSimulacije());
				double statProtok = 
						bjuzArray.get(i).getDummyStatProtok() / ((double)simArray.get(i).getStatProtok() / (sim.getStatVremeTrajanjaSimulacije()/1000) );
				double statVremeOdaziva =
						bjuzArray.get(i).getDummyStatVremeOdaziva() / simArray.get(i).getStatVremeOdaziva();
				
				statIskoriscenje = Math.abs(1 - statIskoriscenje);
				statProsecanBrojPoslova = Math.abs(1 - statProsecanBrojPoslova);
				statProtok = Math.abs(1 - statProtok);
				statVremeOdaziva = Math.abs(1 - statVremeOdaziva);
					
				Komponenta komponentaOdstupanja = bjuzArray.get(i);
				komponentaOdstupanja.setKriticniResurs(false);
				komponentaOdstupanja.setDummy(true);
				
				komponentaOdstupanja.setDummyStatIskoriscenje(statIskoriscenje);
				komponentaOdstupanja.setDummyStatProsecanBrojPoslova(statProsecanBrojPoslova);
				komponentaOdstupanja.setDummyStatProtok(statProtok/1000);
				komponentaOdstupanja.setDummyStatVremeOdaziva(statVremeOdaziva);
				
				komponenteOdstupanjaMatrix[k-2].add(komponentaOdstupanja);
			}
		}
		
		// logovanje svih odstupanja
		RelativnoOdstupanje odstupanje = new RelativnoOdstupanje(stepenMultiprogramiranja);
		
		for (int k = 2; k <= 8; k++) {
			odstupanje.novaAnaliza(k);			
			odstupanje.setDummies(komponenteOdstupanjaMatrix[k-2]);
			odstupanje.snimiLog(k-1);
		}
		
		odstupanje.zatvoriLog();
    }
	
	
}
