package net.mips.interpreter;

import java.util.ArrayList;

public class InterpreterPcode 
{
	public ArrayList<Integer> getPile() {
		return pile;
	}
	public void setPile(ArrayList<Integer> pile) {
		this.pile = pile;
	}
	public ArrayList<Instruction> getPcode() {
		return pcode;
	}
	public void setPcode(ArrayList<Instruction> pcode) {
		this.pcode = pcode;
	}
	public int getSp() {
		return sp;
	}
	public void setSp(int sp) {
		this.sp = sp;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	private ArrayList<Integer> pile;
	private ArrayList<Instruction> pcode;
	private int sp ;
	private int pc;
	private int offset = -1;
	public void loadMnemonic(Mnemonique mne) throws Exception
	{
		if (this.pc == pcode.size())
		{
			throw new Exception("Erreur");
		}
		else 
		{
			++pc;
			pcode.add(new Instruction(mne));
		}
	}
	public void loadMnemonic(Mnemonique mne,int suite) throws Exception
	{
		if (this.pc == pcode.size())
		{
			throw new Exception("Erreur");
		}
		else 
		{
			++pc;
			pcode.add(new Instruction(mne,suite));
		}
	}
	public void interinst(Instruction inst)
	{
		int val1, adr, val2;
		switch (inst.getMne())
		{
			case INT :
				offset = sp = inst.getSuite();
				pc ++;
				break;
			case LDI :
				pile.set(++sp, inst.getSuite()) ;
				pc++;
				break;
			case LDA :
				pile.set(++sp, inst.getSuite()) ;
				pc++;
				break;
			case STO :
				val1 = pile.get(sp--);
				adr = pile.get(sp--);
				pile.set(adr, val1);
				pc++;
				break;
			case LDV :			
				adr = pile.get(sp--);
				pile.set(++sp, pile.get(adr));
				pc++;
				break;
			case EQL :	
				val1 = pile.get(sp--);
				val2 = pile.get(sp--);
				pile.set(++sp,(val1==val2)?1:0);			
				pc++;
				break;
			case LEQ :				
				val2 = pile.get(sp--);
				val1 = pile.get(sp--);
				pile.set(++sp,(val1<=val2)?1:0);			
				pc++;
				break;
			case BZE:
				if(pile.get(sp--)==0)
					pc=inst.getSuite();
				else {pc++;}
				break;
			case BRN:
				pc=inst.getSuite();
				break;
			default :
				System.out.println("???");
		}
	}
	public void interPcode()
	{
		pc=0;
		while(pcode.get(pc).getMne()!=Mnemonique.HLT)
		{
			interinst(pcode.get(pc));
		}
	}
}