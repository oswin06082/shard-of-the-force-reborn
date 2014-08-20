
public class UnhandledOpcodeException extends Exception {
	public final static long serialVersionUID = 1l;
	private byte opcode = -1;
	public UnhandledOpcodeException(String s, byte opcode) {
		super(s);
		this.opcode = opcode;
	}
	
	public byte getOpcode() {
		return opcode;
	}
	
}
