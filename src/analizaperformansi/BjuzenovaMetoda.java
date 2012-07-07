package analizaperformansi;

/**
 *
 * @author Aleksandar Abu-Samra
 */
public class BjuzenovaMetoda extends Analiza {
	
	public BjuzenovaMetoda(int stepenMultiprogramiranja) {
		super(stepenMultiprogramiranja, "analiza_bjuzen." + stepenMultiprogramiranja + ".log");
	}
	
	@Override
	public void izvrsiAnalizu() {
		// konstante u skladu sa formulama
		int n = stepenMultiprogramiranja;
		int k = brojKorisnickihDiskova + BROJ_SISTEMSKIH_DISKOVA + 1;
		
		// izračunato Gordon-Njuelovom metodom i hardkodovano za ovaj konkretan zadatak
		double[] x = { 1,	0.36,	0.45,	3.4 / brojKorisnickihDiskova };
        double[] s = { 5,	12,		15,		20 };
		
		
		// programska realizacija Bjuzenovog algoritma, optimizovana
		double[] G = new double[n+1];
		G[0] = 1;
		
		// programska realizacija Bjuzenovog algoritma, optimizovana
		for (int j = 0; j < k; j++) {
			for (int i = 1; i <= n; i++) {
				G[i] = G[i] + x[j<3?j:3] * G[i-1];
			}
		}
		
		// iskorišćenje
		double[] U = new double[4];
		U[0] = G[n-1] / G[n];
		for (int i=0; i<4; i++) U[i] = U[0] * x[i];
		
		// protok
		double[] X = new double[4];
		for (int i=0; i<4; i++) X[i] = U[i] / s[i];
		
		// prosečan broj poslova
		double[] J = new double[4];
		for (int i = 0; i < 4; i++)
			for (int m = 1; m <= n; m++)
				J[i] += (G[n-m] / G[n]) * Math.pow(x[i], m);
		
		// vreme odaziva (relevantno samo za procesor)
		//double R = J[0] / X[0];
		double R = n / X[0];
		
		
		/*
		 * Kada je sve izračunato, podaci se ubacuju u "dummy" komponente, 
		 * da bi ih LogFajl klasa lakše pročitala
		 */
		
		// procesor
		procesor.setDummy(true);
		procesor.setDummyStatIskoriscenje(U[0]);
		procesor.setDummyStatProtok(X[0]);
		procesor.setDummyStatProsecanBrojPoslova(J[0]);
		procesor.setDummyStatVremeOdaziva(R);
				
		// sistemski diskovi
		int i = 1;
		for (Komponenta sDisk : sDiskArray) {
			sDisk.setDummy(true);
			sDisk.setDummyStatIskoriscenje(U[i]);
			sDisk.setDummyStatProtok(X[i]);
			sDisk.setDummyStatProsecanBrojPoslova(J[i]);
			i++;
		}
		
		// korisnički diskovi
		for (Komponenta kDisk : kDiskArray) {
			kDisk.setDummy(true);
			kDisk.setDummyStatIskoriscenje(U[3]);
			kDisk.setDummyStatProtok(X[3]);
			kDisk.setDummyStatProsecanBrojPoslova(J[3]);
		}
		
	}
}
