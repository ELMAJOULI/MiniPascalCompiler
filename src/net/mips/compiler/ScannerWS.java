package net.mips.compiler;

import java.io.IOException;
import java.util.ArrayList;

public class ScannerWS extends Scanner {

	private ArrayList<Symboles> tableSymb;
	private int placeSymb;
	private int offset;
	private int val;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public ArrayList<Symboles> getTableSymb() {
		return tableSymb;
	}
	public void setTableSymb(ArrayList<Symboles> tableSymb) {
		this.tableSymb = tableSymb;
	}
	public int getPlaceSymb() {
		return placeSymb;
	}
	public void setPlaceSymb(int placeSymb) {
		this.placeSymb = placeSymb;
	}
	public ScannerWS(String nomFich) throws IOException, ErreurCompilation {
		super(nomFich);
		tableSymb=new ArrayList<Symboles>();
		offset=-1;
	}
	
	public void initMotsCles() {
		tableSymb.add(new Symboles(Tokens.PROGRAM_TOKEN, "program"));
		tableSymb.add(new Symboles(Tokens.CONST_TOKEN, "const"));
		tableSymb.add(new Symboles(Tokens.VAR_TOKEN, "var"));
		tableSymb.add(new Symboles(Tokens.BEGIN_TOKEN, "begin"));
		tableSymb.add(new Symboles(Tokens.END_TOKEN, "end"));
		tableSymb.add(new Symboles(Tokens.IF_TOKEN, "if"));
		tableSymb.add(new Symboles(Tokens.THEN_TOKEN, "then"));
		tableSymb.add(new Symboles(Tokens.WHILE_TOKEN, "while"));
		tableSymb.add(new Symboles(Tokens.DO_TOKEN, "do"));
		tableSymb.add(new Symboles(Tokens.WRITE_TOKEN, "write"));
		tableSymb.add(new Symboles(Tokens.READ_TOKEN, "read"));
	}
	
	public void codageLex() {
		String nom1=getSymbCour().getNom();
		for(Symboles symb:tableSymb) {
			String nom2=symb.getNom();
			if(nom1.equalsIgnoreCase(nom2)) {
				getSymbCour().setToken(symb.getToken());
				return;
			}
		}
		getSymbCour().setToken(Tokens.ID_TOKEN);
	}
	
	/*
	 * j'ai adapté la fonction lireNombre  et cela grace au polymorphisme pour qu'elle 
	 * affect la valeur trouvé d'un numéro à la variable Val de la classe sacannerWS 
	 * on va utiliser cette dernier dans la génération de pcode 
	 * plus exactement avec la Mnémonique LDI 
	 *  
	 */
	
	public void lireNombre() throws IOException {
		super.lireNombre();
		val=Integer.parseInt(getSymbCour().getNom());
		
	}
	/*
	 * on mémorise l'id dans la table des symbole
	 * et on incrémente le nombre de l'offset 
	 */
	public void entrerSymb(ClasseIdf c) {
		Symboles s=new Symboles();
		s.setNom(getSymbCour().getNom());
		s.setToken(getSymbCour().getToken());
		s.setClasse(c);
		//Régle sémantique
		if (c==ClasseIdf.CONSTANTE || c==ClasseIdf.VARIABLE) {
			offset++;
			s.setAdresse(offset);
		}
		tableSymb.add(s);
	}
	/*
	 * cette méthod est dédié pour la recherche 
	 * d'un symbole si on le trouve placeSymb est different de -1
	 */
	public void chercherSymb() {
		String nom1=getSymbCour().getNom();
		for(int i=0;i<tableSymb.size();i++) {
			String nom2=tableSymb.get(i).getNom();
			if (nom1.equalsIgnoreCase(nom2)) {
				placeSymb=i;
				return;
			}
		}
		placeSymb=-1;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}
}





