/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analizaperformansi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

			
/**
 *
 * @author Aleksandar Abu-Samra
 */
public class LogFajl {
	private FileWriter log;
	private PrintWriter writeLog;
	private Analiza analiza;
	//private String fileName;
			
	public LogFajl(String fileName, Analiza analiza) {
		this.analiza = analiza;
		//this.fileName = fileName;
		
		// pravi fajl
		try {
			log = new FileWriter(fileName);
			writeLog = new PrintWriter(log);
		} catch (IOException ex) {
			Logger.getLogger(LogFajl.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * Ispisuje header novog testa
	 * @param testId
	 * @param K 
	 */
	public void noviTest(int testId, int K) {		
		if (testId == 1) {	// header
			writeLog.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
			if (analiza instanceof Simulator) {
				writeLog.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤   SIMULATOR   ¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
			}
			else if (analiza instanceof BjuzenovaMetoda) {
				writeLog.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤   BJUZENOV ALGORITAM   ¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
			}
			else if (analiza instanceof RelativnoOdstupanje) {
				writeLog.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤   RELATIVNO ODSTUPANJE   ¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
			}
			writeLog.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
		}
		
		writeLog.println();
		writeLog.println();
		
		writeLog.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤ TEST " + testId + " (K = " + K + ") ¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
		
		// [parametar] stepen multiprogramiranja
		writeLog.println("stepen multiprogramiranja: " + analiza.getStatStepenMultiprogramiranja());
		
		if (analiza instanceof Simulator) {
			// [parametar] vreme trajanja simulacije
			writeLog.println("trajanja simulacije: " + analiza.getStatVremeTrajanjaSimulacije() / 1000 + " sec");
		}
		
		// vreme odaziva
		writeLog.printf("vreme odaziva sitema: %.4f ms\n", analiza.getStatVremeOdaziva());
		writeLog.println();
		
		writeLog.format("%24s | %15s | %15s | %15s\n", "komponenta", "iskorišćenje", "protok [proc/s]", "prosek poslova");
		writeLog.println("==============================================================================");
	}
	
	/**
	 * Upisuje informacije o komponenti u fajl
	 * @param nazivKomponente
	 * @param k 
	 */
	public void upisiKomponentu(String nazivKomponente, Komponenta k) {
		String kritican = " ";
		if (k.isKriticniResurs()) {
			kritican = "*";
		}
		
		if (!k.isDummy()) {
			long vremeTrajanjaSimulacije = analiza.getStatVremeTrajanjaSimulacije();

			writeLog.format("%s %22s | %15.4f | %15.4f | %15.4f\n", 
					kritican,
					nazivKomponente,												// naziv komponente
					k.getStatIskoriscenje(vremeTrajanjaSimulacije),	// iskorišćenje
					(double)k.getStatProtok() / (vremeTrajanjaSimulacije/1000),								// protok
					k.getStatProsecanBrojPoslova(vremeTrajanjaSimulacije)									// prosek poslova na čekanju
					);
		}
		else {				// ako je manualno setovano (Bjuzenova metoda i odstupanje)
			writeLog.format("%s %22s | %15.4f | %15.4f | %15.4f\n", 
					kritican,
					nazivKomponente,
					k.getDummyStatIskoriscenje(),
					k.getDummyStatProtok(),
					k.getDummyStatProsecanBrojPoslova()
					);
		}
	}
	
	public void zatvori() {
		// zatvara fajl
		try {
			log.close();
			writeLog.close();
		} catch (IOException ex) {
			Logger.getLogger(LogFajl.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}