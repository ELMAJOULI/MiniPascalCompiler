package net.mips.interpreter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import net.mips.compiler.CodesErr;
import net.mips.compiler.ErreurCompilation;
import net.mips.compiler.ErreurLexicale;
/*
 * Dans cette classe on réalise un 
 */



public class ScannerPcode
{
	public char getCarCour() {
		return carCour;
	}
	public void setCarCour(char carCour) {
		this.carCour = carCour;
	}
	public Symboles getSymbCour() {
		return symbCour;
	}
	public void setSymbCour(Symboles symbCour) {
		this.symbCour = symbCour;
	}
	public FileReader getFluxSour() {
		return fluxSour;
	}
	public void setFluxSour(FileReader fluxSour) {
		this.fluxSour = fluxSour;
	}
	public ArrayList<Symboles> getMotsCles() {
		return motsCles;
	}
	public void setMotsCles(ArrayList<Symboles> motsCles) {
		this.motsCles = motsCles;
	}
	public static int getEof() {
		return EOF;
	}
	private char carCour;
	private Symboles symbCour;
	private FileReader fluxSour;
	private ArrayList<Symboles> motsCles;	
	public static final int EOF='\0';
	public ScannerPcode(String nomFich) throws 
			IOException, ErreurCompilation 
	{
		File file=new File(nomFich);
		if(!file.exists())
			throw new ErreurLexicale(CodesErr.CAR_INC_ERR);
		fluxSour=new FileReader(file);
		motsCles=new ArrayList<Symboles>(
		Arrays.asList(
				new Symboles("ADD",Mnemonique.ADD),
				new Symboles("SUB",Mnemonique.SUB),
				new Symboles("MUL",Mnemonique.MUL),
				new Symboles("DIV",Mnemonique.DIV),
				new Symboles("EQL",Mnemonique.EQL),
				new Symboles("NEQ",Mnemonique.NEQ),
				new Symboles("GTR",Mnemonique.GTR),
				new Symboles("LSS",Mnemonique.LSS),
				new Symboles("GEQ",Mnemonique.GEQ),
				new Symboles("LEQ",Mnemonique.LEQ),
				new Symboles("PRN",Mnemonique.PRN),
				new Symboles("INN",Mnemonique.INN),
				new Symboles("INT",Mnemonique.INT),
				new Symboles("LDI",Mnemonique.LDI),
				new Symboles("LDA",Mnemonique.LDA),
				new Symboles("LDV",Mnemonique.LDV),
				new Symboles("STO",Mnemonique.STO),
				new Symboles("BRN",Mnemonique.BRN),
				new Symboles("BZE",Mnemonique.BZE),
				new Symboles("HLT",Mnemonique.HLT)						
				) 
		);	
	}
	public void lireCar() throws IOException {
		if (fluxSour.ready())
			carCour=(char)fluxSour.read();
		else
			carCour=EOF;
			symbCour.setToken(Mnemonique.EOF);
	}
	public void codageLex()
	{
		for (Symboles s : this.motsCles)
		{
			if (s.getNom().equalsIgnoreCase(symbCour.getNom()))
			{
				symbCour.setToken(s.getToken());
				return;
			}
		}
		symbCour.setToken(Mnemonique.NUM);
	}
	public void lireinst() throws IOException
	{
		symbCour.setNom(symbCour.getNom()+carCour);
		lireCar();
		while(Character.isLetter(carCour))
		{
			symbCour.setNom(symbCour.getNom()+carCour);
			lireCar();
		}
		codageLex();
	}
	public void lireNombre() throws IOException {
		symbCour.setNom(symbCour.getNom()+carCour);
		lireCar();
		while(Character.isDigit(carCour)) {
			symbCour.setNom(symbCour.getNom()+carCour);
			lireCar();
		}
		symbCour.setToken(Mnemonique.NUM);
	}
	public void symbSuiv() throws IOException
	{
		symbCour=new Symboles();
		while(Character.isWhitespace(carCour))
			lireCar();
		if (Character.isLetter(carCour)) {
			lireinst();
			return;
		}
		if(Character.isDigit(carCour)) {
			lireNombre();
			return;
		}
	}
}	
