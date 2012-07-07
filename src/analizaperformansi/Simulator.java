package analizaperformansi;

import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Aleksandar Abu-Samra
 */
public class Simulator extends Analiza {
	
	public Simulator(int stepenMultiprogramiranja) {
		super(stepenMultiprogramiranja, "analiza_simulator." + stepenMultiprogramiranja + ".log");
	}
		
	/**
	 * Pokreće simulaciju
	 */
	@Override
	public void izvrsiAnalizu() {
		LinkedList<Proces> PQueue = new LinkedList<>(); // procesi iz procesora
		LinkedList<Proces> SDQueue = new LinkedList<>(); // procesi iz sistemskih diskova
		LinkedList<Proces> KDQueue = new LinkedList<>(); // procesi iz korisničkih diskova
		
		// Random generator
		Random randomGenerator = new Random(System.currentTimeMillis());
		
		long protekloVreme = 0;
		while (protekloVreme < DEFAULT_TRAJANJE_SIMULACIJE) {
			// [step] procesor
			procesor.tick();
			if (procesor.isZavrsioObradu()) {
				PQueue.add(procesor.getProces());
			}
			
			// [step] sistemski diskovi
			for (Komponenta sDisk: sDiskArray) {
				sDisk.tick();
				if (sDisk.isZavrsioObradu()) {
					SDQueue.add(sDisk.getProces());
				}
			}
			
			// [step] korisnicki diskovi
			for (Komponenta kDisk: kDiskArray) {
				kDisk.tick();
				if (kDisk.isZavrsioObradu()) {
					KDQueue.add(kDisk.getProces());
				}
			}
			
			/*
			 * Korak se završio, sada možemo da rasporedimo završene procese 
			 * u odgovarajuće redove
			 */
			
			double procenat;
			Proces p;
			
			// [raspodela] procesor
			while (!PQueue.isEmpty()) {
				p = PQueue.pop();
				procenat = randomGenerator.nextDouble();
				
				if (procenat < 0.15) { // zahteva se pristup prvom sistemskom disku
					sDiskArray.get(0).putProces(p);
				}
				else if (procenat < 0.3) { // zahteva se pristup drugom sistemskom disku
					sDiskArray.get(1).putProces(p);
				}
				else { // zahteva se pristup nekom od K korisničkih diskova
					int randomKorisnickiDisk = randomGenerator.nextInt(kDiskArray.size());
					kDiskArray.get(randomKorisnickiDisk).putProces(p);
				}
			}
			
			// [raspodela] sistemski diskovi
			while (!SDQueue.isEmpty()) {
				p = SDQueue.removeFirst();
				procenat = randomGenerator.nextDouble();
				
				if (procenat < 0.5) { // vraćanje u procesorski red
					procesor.putAndTrackProces(p);
				}
				else { // pristup nekom od K korisničkih diskova
					int randomKorisnickiDisk = randomGenerator.nextInt(kDiskArray.size());
					kDiskArray.get(randomKorisnickiDisk).putProces(p);
				}
			}
			
			// [raspodela] korisnički diskovi
			while (!KDQueue.isEmpty()) {
				p = KDQueue.removeFirst();
				procesor.putAndTrackProces(p);
			}
			
			protekloVreme++; // vreme teče
		}
	}

}
