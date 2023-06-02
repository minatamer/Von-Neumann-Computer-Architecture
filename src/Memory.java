public class Memory {

	long[] memory;
	static int count =0; //next empty space
	
	public Memory() {
		memory = new long[2048];  //0 to 1023 is instructions and 1024 to 2048 is data
	}
	
	public void addInstruction (String binaryString) {
		long number = Long.parseLong(binaryString,2);
		memory[count] = number;
		count++;
	}
	
}
