package net.mips.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner {
	private char carCour;
	private Symboles symbCour;
	private FileReader fluxSour;
	
	private ArrayList<Symboles> motsCles;
	// EOF variable pour indiquer la fin du fichier
	public static final int EOF='\0';

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
	/*
	 * le constructeur du Tokenizer prend en parametre le nom de 
	 * fichier qui va etre analyser
	 */
	public Scanner(String nomFich) throws 
			IOException, ErreurCompilation {
		File file=new File(nomFich);
		if(!file.exists())
			throw new ErreurLexicale(CodesErr.CAR_INC_ERR);
		fluxSour=new FileReader(file);
		motsCles=new ArrayList<Symboles>();	
	}
	
	/*
	 * la m�thode pour initialiser la table dynamique des mots cl�s
	 */
	public void initMotsCles() {
		motsCles.add(new Symboles(Tokens.PROGRAM_TOKEN, "program"));
		motsCles.add(new Symboles(Tokens.CONST_TOKEN, "const"));
		motsCles.add(new Symboles(Tokens.VAR_TOKEN, "var"));
		motsCles.add(new Symboles(Tokens.BEGIN_TOKEN, "begin"));
		motsCles.add(new Symboles(Tokens.END_TOKEN, "end"));
		motsCles.add(new Symboles(Tokens.IF_TOKEN, "if"));
		motsCles.add(new Symboles(Tokens.THEN_TOKEN, "then"));
		motsCles.add(new Symboles(Tokens.WHILE_TOKEN, "while"));
		motsCles.add(new Symboles(Tokens.DO_TOKEN, "do"));
		motsCles.add(new Symboles(Tokens.WRITE_TOKEN, "write"));
		motsCles.add(new Symboles(Tokens.READ_TOKEN, "read"));
	}
	/*
	 * codageLex est la m�thode qui permet de boucler dans 
	 * la table des mots cl�s pour associer pour chaque symbole lu 
	 * son token.
	 * si elle ne trouve pas elle considere que le mot lu est un 
	 * identificateur
	 */
	public void codageLex() {
		String nom1=symbCour.getNom();
		for(Symboles symb:motsCles) {
			String nom2=symb.getNom();
			if(nom1.equalsIgnoreCase(nom2)) {
				symbCour.setToken(symb.getToken());
				return;
			}
		}
		symbCour.setToken(Tokens.ID_TOKEN);
	}
	 /*
	  * tester si le flux est pret pour lire un caract�re du fichier
	  * d'entr�
	  */
	public void lireCar() throws IOException {
		if (fluxSour.ready())
			carCour=(char)fluxSour.read();
		else
			carCour=EOF;
	}
	/*
	 * si le caract�re lu respecte l'expression reguliere 
	 * 		mot ::= lettre{lettre|chiffre}*
	 * on utilise la m�thod lireMot() qui embarque la procedure lireCar
	 * et codageLex
	 */
	public void lireMot() throws IOException {
		symbCour.setNom(symbCour.getNom()+carCour);
		lireCar();
		while(Character.isLetterOrDigit(carCour)) {
			symbCour.setNom(symbCour.getNom()+carCour);
			lireCar();
		}
		codageLex();
	}
	/*
	 * lire un nombre si l'expression reguliere 
	 * 	chiffre ::= chiffre+
	 * 
	 * est trouv�
	 */
	public void lireNombre() throws IOException {
		symbCour.setNom(symbCour.getNom()+carCour);
		lireCar();
		while(Character.isDigit(carCour)) {
			symbCour.setNom(symbCour.getNom()+carCour);
			lireCar();
		
		}
		symbCour.setToken(Tokens.NUM_TOKEN);
	}
	/*
	 * cette procedure permet de lire un symbole par appel
	 */
	public void symbSuiv() throws IOException, ErreurCompilation {
		symbCour=new Symboles();
		while(Character.isWhitespace(carCour))
			lireCar();
		if (Character.isLetter(carCour)) {
			lireMot();
			return;
		}
		if(Character.isDigit(carCour)) {
			lireNombre();
			return;
		}
		switch(carCour) {
		case '+':
			symbCour.setToken(Tokens.PLUS_TOKEN);
			lireCar();
			break;
		case '-':
			symbCour.setToken(Tokens.MOINS_TOKEN);
			lireCar();
			break;
		case '*':
			symbCour.setToken(Tokens.MUL_TOKEN);
			lireCar();
			break;
		case '/':
			symbCour.setToken(Tokens.DIV_TOKEN);
			lireCar();
			break;
		case '(':
			symbCour.setToken(Tokens.PARG_TOKEN);
			lireCar();
			break;
		case ')':
			symbCour.setToken(Tokens.PARD_TOKEN);
			lireCar();
			break;
		case '.':
			symbCour.setToken(Tokens.PNT_TOKEN);
			lireCar();
			break;
		case ',':
			symbCour.setToken(Tokens.VIR_TOKEN);
			lireCar();
			break;
		case ';':
			symbCour.setToken(Tokens.PVIR_TOKEN);
			lireCar();
			break;
		case '=':
			symbCour.setToken(Tokens.EG_TOKEN);
			lireCar();
			break;
		case EOF:
			symbCour.setToken(Tokens.EOF_TOKEN);
			break;
		case ':':
			lireCar();
			switch(carCour) {
			case '=':
				symbCour.setToken(Tokens.AFFEC_TOKEN);
				lireCar();
				break;
			default:
				symbCour.setToken(Tokens.ERR_TOKEN);
				throw new ErreurLexicale(CodesErr.CAR_INC_ERR);
			}
			break;
		case '>':
			lireCar();
			switch(carCour) {
			case '=':
				symbCour.setToken(Tokens.SUPEG_TOKEN);
				lireCar();
				break;
			default:
				symbCour.setToken(Tokens.SUP_TOKEN);
				break;
			}
			break;
		case '<':
			lireCar();
			switch(carCour) {
			case '=':
				symbCour.setToken(Tokens.INFEG_TOKEN);
				lireCar();
				break;
			case '>':
				symbCour.setToken(Tokens.DIFF_TOKEN);
				lireCar();
				break;
			default:
				symbCour.setToken(Tokens.INF_TOKEN);
				break;
			}
			break;
		default:
			throw new ErreurLexicale(CodesErr.CAR_INC_ERR);
		}
		
			
	}
	
	
	public static void main(String args[]) 
		throws IOException, ErreurCompilation {
		Scanner scanner=new Scanner("exemp1.p");
		scanner.initMotsCles();
		scanner.lireCar(); 
		while(scanner.getCarCour()!=EOF) {
			scanner.symbSuiv();
			System.out.println(scanner.getSymbCour().getToken());
		}
	}
	
	
	
	
}
