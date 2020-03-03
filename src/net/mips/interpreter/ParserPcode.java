package net.mips.interpreter;

import java.io.IOException;

import net.mips.compiler.ErreurCompilation;

public class ParserPcode 
{
	public ParserPcode(ScannerPcode scan) {
		
		this.scan = scan;
	}

	private ScannerPcode scan;

	public ScannerPcode getScan() {
		return scan;
	}

	public void setScan(String f) throws IOException, ErreurCompilation 
	{
		this.scan = new ScannerPcode(f);
	}
	public void testAccept(Mnemonique mn) throws Exception
	{
		if(scan.getSymbCour().getToken() == mn)
		{
			scan.symbSuiv();
		}else {
			throw new Exception("Err Pcode");
		}
	}
	public void Pcode() throws Exception
	{
		testAccept(Mnemonique.INT);
		testAccept(Mnemonique.NUM);
		intPcode();
		testAccept(Mnemonique.HLT);
	}
	public void intPcode() throws Exception
	{
		switch(scan.getSymbCour().getToken())
		{
			case LDA :
				scan.symbSuiv();
				testAccept(Mnemonique.NUM);
				break;
			case BZE :
				scan.symbSuiv();
				testAccept(Mnemonique.NUM);
				break;
			case BRN :
				scan.symbSuiv();
				testAccept(Mnemonique.NUM);
				break;
			case LDI :
				scan.symbSuiv();
				testAccept(Mnemonique.NUM);
				break;
			case ADD:
				scan.symbSuiv();
				break;
			case LDV:
				scan.symbSuiv();
				break;
			case SUB:
				scan.symbSuiv();
				break;
			case MUL:
				scan.symbSuiv();
				break;
			case DIV:
				scan.symbSuiv();
				break;
			case LEQ:
				scan.symbSuiv();
				break;
			case GEQ:
				scan.symbSuiv();
				break;
			case NEQ:
				scan.symbSuiv();
				break;
			case LSS:
				scan.symbSuiv();
				break;
			case GTR:
				scan.symbSuiv();
				break;
			case HLT:				
				break;
			case STO:
				scan.symbSuiv();
				break;
			case INN:
				scan.symbSuiv();
				break;
			case PRN:
				scan.symbSuiv();
				break;
				
			default : 
				throw new Exception("Err instPcode");
		}
	}
}
