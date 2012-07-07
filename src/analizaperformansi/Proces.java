/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package analizaperformansi;

/**
 *
 * @author Aleksandar Abu-Samra
 */
public class Proces {
	private long vremeIzvrsavanja;
	
	public Proces() {
		reset();
	}
	
	public void reset() {
		vremeIzvrsavanja = 0;
	}
	
	public void tick() {
		vremeIzvrsavanja++;
	}
	
	public long getVremeIzvrsavanja() {
		return vremeIzvrsavanja;
	}
}
