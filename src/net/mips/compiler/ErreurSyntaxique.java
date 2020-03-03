package net.mips.compiler;

public class ErreurSyntaxique extends ErreurCompilation {
	
	private static final long serialVersionUID = 115174704820761429L;

	public ErreurSyntaxique(CodesErr code) {
		super(code.getMessage());
	}
}
