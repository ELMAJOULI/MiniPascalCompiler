package net.mips.compiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.mips.interpreter.Instruction;
import net.mips.interpreter.Mnemonique;

public class ParserWS extends Parser {
	
	private ArrayList<Instruction> pcode;
	private PrintWriter fluxCible;
	
	public ParserWS(String nomFich) 
			throws IOException, ErreurCompilation {
		super(nomFich);
		setScan(new ScannerWS(nomFich));
		pcode=new ArrayList<Instruction>();
	
	}
	
	
	public void generer1(Mnemonique m) {
		Instruction ins=new Instruction();
		ins.setMne(m);
		pcode.add(ins);
	}
	
	public void generer2(Mnemonique m, int a) {
		Instruction ins=new Instruction(m,a);
		pcode.add(ins);
	}
	
	public void savePcode() throws IOException {
		fluxCible=new PrintWriter("sortie.pc");
		for (Instruction ins:pcode) {
			Mnemonique m=ins.getMne();
			if (m==Mnemonique.INT || m==Mnemonique.LDA ||
					m==Mnemonique.LDI || m==Mnemonique.BRN ||
					m==Mnemonique.BZE) 
				fluxCible.println(m+"\t"+ins.getSuite());
			else
				fluxCible.println(m);
		}
		fluxCible.close();
	}
	public void testInsere(Tokens t, ClasseIdf cls, CodesErr c) 
			throws IOException, ErreurCompilation {
		if (getScan().getSymbCour().getToken()==t) {
			((ScannerWS)getScan()).chercherSymb();
			if (((ScannerWS)getScan()).getPlaceSymb()!=-1)
				throw new ErreurSemantique(CodesErr.DBL_DECL_ERR);
			((ScannerWS)getScan()).entrerSymb(cls);
			getScan().symbSuiv();
		}
		else
			throw new ErreurSyntaxique(c);
	}
	
	public void testCherche(Tokens t, CodesErr c) 
			throws IOException, ErreurCompilation {
		if (getScan().getSymbCour().getToken()==t) {
			((ScannerWS)getScan()).chercherSymb();
			int p=((ScannerWS)getScan()).getPlaceSymb();
			if (p==-1)
				throw new ErreurSemantique(CodesErr.NON_DECL_ERR);
			Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
			if (s.getClasse()==ClasseIdf.PROGRAMME)
				throw new ErreurSemantique(CodesErr.PROG_USE_ERR);
			
			getScan().symbSuiv();
		}
		else
			throw new ErreurSyntaxique(c);
	}
	
	public void program() throws IOException, ErreurCompilation {
		testAccept(Tokens.PROGRAM_TOKEN, CodesErr.PROGRAM_ERR);
		
		//testAccept(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		testInsere(Tokens.ID_TOKEN, ClasseIdf.PROGRAMME, CodesErr.ID_ERR);
		
		generer2(Mnemonique.INT,((ScannerWS)getScan()).getOffset());
		
		testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
		block();
		testAccept(Tokens.PNT_TOKEN, CodesErr.PNT_ERR);
		generer1(Mnemonique.HLT);
		Instruction e = new Instruction(Mnemonique.INT, ((ScannerWS)getScan()).getOffset());
		pcode.set(0, e);
	}
	
	public void block() throws IOException, ErreurCompilation {
		consts();
		vars();
		
		insts();
	}
	
	public void consts() throws IOException, ErreurCompilation {
		switch(getScan().getSymbCour().getToken()) {
		case CONST_TOKEN:
			getScan().symbSuiv();
			testInsere(Tokens.ID_TOKEN,ClasseIdf.CONSTANTE,  CodesErr.ID_ERR);
			//(a)
			generer2(Mnemonique.LDA, ((ScannerWS)getScan()).getOffset());
			
			testAccept(Tokens.EG_TOKEN, CodesErr.EG_ERR);
			testAccept(Tokens.NUM_TOKEN, CodesErr.NUM_ERR);
			
			generer2(Mnemonique.LDI, ((ScannerWS)getScan()).getVal());

			//(b)
			testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
			
			generer1(Mnemonique.STO);
			
			while (getScan().getSymbCour().getToken()==Tokens.ID_TOKEN) {
				testInsere(Tokens.ID_TOKEN,ClasseIdf.CONSTANTE,  CodesErr.ID_ERR);
				//(a)
				generer2(Mnemonique.LDA, ((ScannerWS)getScan()).getOffset());
				
				testAccept(Tokens.EG_TOKEN, CodesErr.EG_ERR);
				System.out.println(getScan().getSymbCour().getNom());
				testAccept(Tokens.NUM_TOKEN, CodesErr.NUM_ERR);
				
				generer2(Mnemonique.LDI, ((ScannerWS)getScan()).getVal());
				
				testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
				
				generer1(Mnemonique.STO);
			}
			break;
		case VAR_TOKEN:
			break;
		case BEGIN_TOKEN:
			break;
		default:
			throw new ErreurSyntaxique(CodesErr.CONSTS_ERR);
		}
	}

	

	public void vars() throws IOException, ErreurCompilation {
		switch (getScan().getSymbCour().getToken()) {
		case VAR_TOKEN:
			//var ID { , ID } ;
			getScan().symbSuiv();
			testInsere(Tokens.ID_TOKEN,ClasseIdf.VARIABLE,  CodesErr.ID_ERR);
			while (getScan().getSymbCour().getToken()==Tokens.VIR_TOKEN){
				getScan().symbSuiv();
				testInsere(Tokens.ID_TOKEN,ClasseIdf.VARIABLE,  CodesErr.ID_ERR);
			}
			testAccept(Tokens.PVIR_TOKEN, CodesErr.PVIR_ERR);
			break;
		case BEGIN_TOKEN:
			//epsilon
			break;
		default:
			throw new ErreurSyntaxique(CodesErr.VARS_ERR);
		}
	}
	
	public void affec() throws ErreurCompilation, IOException {
		testCherche(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		int p=((ScannerWS)getScan()).getPlaceSymb();
		Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
		if (s.getClasse()==ClasseIdf.CONSTANTE)
			throw new ErreurSemantique(CodesErr.CONST_MODIF_ERR);
		
		generer2(Mnemonique.LDA, s.getAdresse());
		
		testAccept(Tokens.AFFEC_TOKEN, CodesErr.AFFEC_ERR);
		expr();
		generer1(Mnemonique.STO);
	}

	public void lire() throws ErreurCompilation, IOException {
		testAccept(Tokens.READ_TOKEN, CodesErr.READ_ERR);
		testAccept(Tokens.PARG_TOKEN, CodesErr.PARG_ERR);
		testCherche(Tokens.ID_TOKEN, CodesErr.ID_ERR);
		
		int p=((ScannerWS)getScan()).getPlaceSymb();
		Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
		if (s.getClasse()==ClasseIdf.CONSTANTE)
			throw new ErreurSemantique(CodesErr.CONST_MODIF_ERR);
		generer2(Mnemonique.LDA, s.getAdresse());
		generer1(Mnemonique.INN);
		while(getScan().getSymbCour().getToken()==Tokens.VIR_TOKEN) {
			getScan().symbSuiv();
			testCherche(Tokens.ID_TOKEN, CodesErr.ID_ERR);
			
			p=((ScannerWS)getScan()).getPlaceSymb();
			s=((ScannerWS)getScan()).getTableSymb().get(p);
			if (s.getClasse()==ClasseIdf.CONSTANTE)
				throw new ErreurSemantique(CodesErr.CONST_MODIF_ERR);
			generer2(Mnemonique.LDA, s.getAdresse());
			generer1(Mnemonique.INN);
		}
		testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
	}
	
	public void ecrire() throws ErreurCompilation, IOException {
		testAccept(Tokens.WRITE_TOKEN, CodesErr.WRITE_ERR);
		testAccept(Tokens.PARG_TOKEN, CodesErr.PARG_ERR);
		expr();
		generer1(Mnemonique.PRN);
		while(getScan().getSymbCour().getToken()==Tokens.VIR_TOKEN) {
			getScan().symbSuiv();
			expr();
			generer1(Mnemonique.PRN);
		}
		testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
	}
	
	
	public void expr() throws IOException, ErreurCompilation {
		term();
		
		while(getScan().getSymbCour().getToken()==Tokens.PLUS_TOKEN || getScan().getSymbCour().getToken()==Tokens.MOINS_TOKEN) {
			Tokens t = getScan().getSymbCour().getToken();
			getScan().symbSuiv();
			term();
			if(t==Tokens.PLUS_TOKEN)
				generer1(Mnemonique.ADD);
			else
			generer1(Mnemonique.SUB);
		}
	}
	
	public void term() throws IOException, ErreurCompilation {
		fact();
		
		while(getScan().getSymbCour().getToken()==Tokens.MUL_TOKEN || getScan().getSymbCour().getToken()==Tokens.DIV_TOKEN) {
			Tokens t = getScan().getSymbCour().getToken();
			getScan().symbSuiv();
			fact();
			if(t==Tokens.MUL_TOKEN)
				generer1(Mnemonique.MUL);
			else
				generer1(Mnemonique.DIV);
		}
	}
	public void fact() throws IOException, ErreurCompilation {
		switch(getScan().getSymbCour().getToken()) {
		case ID_TOKEN:
			testCherche(Tokens.ID_TOKEN,  CodesErr.ID_ERR);
			int p=((ScannerWS)getScan()).getPlaceSymb();
			Symboles s=((ScannerWS)getScan()).getTableSymb().get(p);
			generer2(Mnemonique.LDA, s.getAdresse());
			generer1(Mnemonique.LDV);
			break;
		case NUM_TOKEN:
			generer2(Mnemonique.LDI,((ScannerWS)getScan()).getVal() );
			getScan().symbSuiv();
			break;
		case PARG_TOKEN:
			getScan().symbSuiv();
			expr();
			testAccept(Tokens.PARD_TOKEN, CodesErr.PARD_ERR);
		default:
			throw new ErreurSyntaxique(CodesErr.FACT_ERR);
		}
	}
	public void tantque() throws IOException, ErreurCompilation{
		testAccept(Tokens.WHILE_TOKEN, CodesErr.WHILE);
		int p0 = pcode.size();
		cond();
		int p1 = pcode.size();
		generer2(Mnemonique.BZE, p1);
		testAccept(Tokens.DO_TOKEN, CodesErr.DO_ERR);
		inst();
		generer2(Mnemonique.BRN, p0);
		int p2 = pcode.size();
		pcode.get(p1).setSuite(p2);
	}
	
	public static void main(String [] args) 
			 throws IOException, ErreurCompilation {
		ParserWS parse=new ParserWS("exemp6.p");
		parse.getScan().initMotsCles();
		parse.getScan().lireCar();
		parse.getScan().symbSuiv();
		parse.program();
		if (parse.getScan().getSymbCour().getToken()==Tokens.EOF_TOKEN) { 
			parse.savePcode();
			System.out.println("Analyse syntaxique reussie");
			
		}
		else
			throw new ErreurSyntaxique(CodesErr.EOF_ERR);
	}



	public ArrayList<Instruction> getPcode() {
		return pcode;
	}



	public void setPcode(ArrayList<Instruction> pcode) {
		this.pcode = pcode;
	}



	public PrintWriter getFluxCible() {
		return fluxCible;
	}



	public void setFluxCible(PrintWriter fluxCible) {
		this.fluxCible = fluxCible;
	}

}






