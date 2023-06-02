public class Instruction {

	int opcode = 0;
	int R1 = 0;
	int R2 = 0;
	int R3 = 0;
	int shamt = 0;
	int imm = 0;
	int address = 0;
	
	public Instruction() {
		
	}

	@Override
	public String toString() {
		String str = "Opcode : " + this.opcode + " R1: " + this.R1 + " R2: " + this.R2 + 
				" R3: " + this.R3 + " shamt: " + this.shamt + " imm: "  + this.imm + " address: " + this.address;
		return str;
	}
	
	
}
