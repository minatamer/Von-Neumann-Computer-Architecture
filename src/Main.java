import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

public class Main {

	Register registerFile;
	Memory memory;
	static int clkCycle = 1;
	int numOfInstructions;
	ArrayList<Long> fetchedInstructions;
	ArrayList<Instruction> decodedInstructions;
	ArrayList<Integer> executedInstructions;
	ArrayList<Integer> memoryInstructions;
	ArrayList<Integer> wbInstructions;
	Hashtable<Integer, Integer> hazardControl = new Hashtable<>();


	public Main() {
		memory = new Memory();
		registerFile = new Register();
		numOfInstructions = 0;
		fetchedInstructions = new ArrayList<Long>();
		decodedInstructions = new ArrayList<Instruction>();
		executedInstructions = new ArrayList<Integer>();
		memoryInstructions = new ArrayList<Integer>();
		wbInstructions = new ArrayList<Integer>();
		hazardControl = new Hashtable<>();
		readFile();
		// start the pipeline process DONT FORGET THE OUTPUTS
		pipeline();
	}

	public void readFile() {

		try {
			// File file = new File("resources/Test.txt");
			// FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(new FileReader("E:/CA/Project/Test Files/Package 1/Program3.txt"));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				Instruction instruction = new Instruction();

				String[] array = line.split(" ");
				if (array[0].equalsIgnoreCase("ADD") || array[0].equalsIgnoreCase("SUB")
						|| array[0].equalsIgnoreCase("SRL") || array[0].equalsIgnoreCase("SLL")) {
					instruction = new Instruction();
					String num = array[1].charAt(1) + "";
					if (array[1].length() >2)
						num += array[1].charAt(2);
					instruction.R1 = Integer.parseInt(num);

					String num2 = array[2].charAt(1) + "";
					if (array[2].length() >2)
						num2 += array[2].charAt(2);
					instruction.R2 = Integer.parseInt(num2);

					if (array[0].equalsIgnoreCase("SRL") || array[0].equalsIgnoreCase("SLL")) {
						String num3 = array[3];
						instruction.shamt = Integer.parseInt(num3);
					} else {
						String num3 = array[3].charAt(1) + "";
						if (array[3].length() >2)
							num3 += array[3].charAt(2);
						instruction.R3 = Integer.parseInt(num3);
					}

					if (array[0].equalsIgnoreCase("ADD"))
						instruction.opcode = 0;
					else if (array[0].equalsIgnoreCase("SUB"))
						instruction.opcode = 1;
					else if (array[0].equalsIgnoreCase("SLL"))
						instruction.opcode = 8;
					else
						instruction.opcode = 9;

					String binary = String.format("%4s", Integer.toBinaryString(instruction.opcode)).replace(' ', '0')
							+ String.format("%5s", Integer.toBinaryString(instruction.R1)).replace(' ', '0')
							+ String.format("%5s", Integer.toBinaryString(instruction.R2)).replace(' ', '0')
							+ String.format("%5s", Integer.toBinaryString(instruction.R3)).replace(' ', '0')
							+ String.format("%13s", Integer.toBinaryString(instruction.shamt)).replace(' ', '0');

					memory.addInstruction(binary);
					numOfInstructions++;

				}

				else if (array[0].equalsIgnoreCase("MULI") || array[0].equalsIgnoreCase("ADDI")
						|| array[0].equalsIgnoreCase("ANDI") || array[0].equalsIgnoreCase("ORI")
						|| array[0].equalsIgnoreCase("LW") || array[0].equalsIgnoreCase("SW")
						|| array[0].equalsIgnoreCase("BNE")) {

					instruction = new Instruction();
					String num = array[1].charAt(1) + "";
					if (array[1].length() >2) {
						num += array[1].charAt(2);
					}
					instruction.R1 = Integer.parseInt(num);

					String num2 = array[2].charAt(1) + "";
					if (array[2].length() >2)
						num2 += array[2].charAt(2);
					instruction.R2 = Integer.parseInt(num2);

					String num3 = array[3];
					instruction.imm = Integer.parseInt(num3);

					if (array[0].equalsIgnoreCase("MULI"))
						instruction.opcode = 2;
					else if (array[0].equalsIgnoreCase("ADDI"))
						instruction.opcode = 3;
					else if (array[0].equalsIgnoreCase("BNE"))
						instruction.opcode = 4;
					else if (array[0].equalsIgnoreCase("ANDI"))
						instruction.opcode = 5;
					else if (array[0].equalsIgnoreCase("ORI"))
						instruction.opcode = 6;
					else if (array[0].equalsIgnoreCase("LW"))
						instruction.opcode = 10;
					else
						instruction.opcode = 11;

					String binary = String.format("%4s", Integer.toBinaryString(instruction.opcode)).replace(' ', '0')
							+ String.format("%5s", Integer.toBinaryString(instruction.R1)).replace(' ', '0')
							+ String.format("%5s", Integer.toBinaryString(instruction.R2)).replace(' ', '0')
							+ String.format("%18s", Integer.toBinaryString(instruction.imm)).replace(' ', '0');

					memory.addInstruction(binary);
					numOfInstructions++;

				} else {
					instruction = new Instruction();
					instruction.opcode = 7;
					instruction.address = Integer.parseInt(array[1]);

					String binary = String.format("%4s", Integer.toBinaryString(instruction.opcode)).replace(' ', '0')
							+ String.format("%28s", Integer.toBinaryString(instruction.address)).replace(' ', '0');
					memory.addInstruction(binary);
					numOfInstructions++;
				}

			}

			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public long fetch() {

		long instruction = memory.memory[registerFile.pc];
		// decode(instruction);
		registerFile.pc++;
		return instruction;

	}

	public Instruction decode(long instruction) {

		int opcode = 0; // bits31:28
		int rd = 0; // bits27:23
		int rs = 0; // bits22:18
		int rt = 0; // bits17:13
		int shamt = 0; // bits12:0
		int imm = 0; // bits17:0
		int address = 0; // bits27:0

		opcode = (int) (instruction >> 28);
		rd = (int) ((0b1111100000000000000000000000 & instruction) >> 23);
		rt = (int) ((0b11111000000000000000000 & instruction) >> 18);
		rs = (int) ((0b111110000000000000 & instruction) >> 13);
		shamt = (int) (0b111111111111 & instruction);
		imm = (int) (0b111111111111111111 & instruction);
		address = (int) (0b1111111111111111111111111111 & instruction);

		Instruction inst = new Instruction();
		inst.opcode = opcode;
		inst.R1 = rd;
		inst.R2 = rt;
		inst.R3 = rs;
		inst.shamt = shamt;
		inst.imm = imm;
		inst.address = address;
		return inst;

	}

	public int execute(Instruction instruction) {

		int result = 0;
		int myR2 = registerFile.registerFile[instruction.R2];;
		int myR3 = registerFile.registerFile[instruction.R3];
		if(hazardControl.containsKey(instruction.R2)) {
			myR2 = hazardControl.get(instruction.R2);
		}
		if(hazardControl.containsKey(instruction.R3)) {
			myR3 = hazardControl.get(instruction.R3);
		}

		switch (instruction.opcode) {

		case 0:
			result = myR2 + myR3;
			break;
		case 1:
			result = myR2 - myR3;
			break;
		case 2:
			result = myR2 * instruction.imm;
			break;
		case 3:
			result = myR2 + instruction.imm;
			break;
		case 4:
			if(hazardControl.containsKey(instruction.R1)) {
				myR3 = hazardControl.get(instruction.R1);
			}
			if (myR3 != myR2) {
				registerFile.pc += -1 + instruction.imm;
				result =1;
			}
				
			break;
		case 5:
			result = myR2 & instruction.imm;
			break;
		case 6:
			result = myR2 | instruction.imm;
			break;
		case 7:
			registerFile.pc =((registerFile.pc >>> 28) & 0xF << 28) | (instruction.address & 0xFFFFFFF);
			break;
		case 8:
			result = myR2 << instruction.shamt;
			break;
		case 9:
			result = myR2 >>> instruction.shamt;
			break;
		default:
			result = myR2 + instruction.imm;
			break;

		}
		return result;

	}

	public int memory(Instruction instruction, int value) {

		int result = 0;

		switch (instruction.opcode) {
		case 10:
			result = (int) memory.memory[value];
			break;
		case 11:
			memory.memory[value] = registerFile.registerFile[instruction.R1];
			System.out.println(
					"Value: " + registerFile.registerFile[instruction.R1] + " was put at Memory[" + value + "]");
			break;
		default:
			result = value;
			break;

		}

		return result;
	}

	public void writeBack(Instruction instruction, int value) {

		if (instruction.opcode != 4 && instruction.opcode != 7 && instruction.opcode != 11 && instruction.R1 !=0)
			registerFile.registerFile[instruction.R1] = value;
		System.out.println("The register that got changed: R" + instruction.R1 + " with value: " + value);

	}

	public void pipeline() {
		int maxCycles = 7 + ((numOfInstructions - 1) * 2);
		int value = 0;
		int fetchBoundaries = numOfInstructions *2;
		ArrayList skippedInstruction = new ArrayList<Object>();
		while (clkCycle <= maxCycles) {
			System.out.println("Clock cycle: " + clkCycle);
			

			if (clkCycle % 2 != 0 && clkCycle < fetchBoundaries) { // fetch
				long fetchedInstruction = fetch();
				fetchedInstructions.add(fetchedInstruction);
				System.out.println("Fetched Instruction : " + fetchedInstruction);
			}
			if (clkCycle % 2 == 0 && clkCycle < fetchBoundaries+2 ) { // decode
				int index = decodedInstructions.size();
				long instruction = fetchedInstructions.get(index);
				Instruction inst = decode(instruction);
				decodedInstructions.add(inst);
				System.out.println("Decoded Output : " + inst);
				System.out.println("The decoding parameters: " + instruction);
			}

			if (clkCycle % 2 == 0 && clkCycle >= 4 && clkCycle < fetchBoundaries + 4 ) { // execute	
				int index = executedInstructions.size();
				Instruction inst = decodedInstructions.get(index);
				if (skippedInstruction.contains(inst) ) {
					executedInstructions.add(0);
					System.out.println("Executed Output: null, skipped instruction");
				}
				else {
					int v = execute(inst);
					executedInstructions.add(v);
					//HAZARD CONTROL BONUS
					//HERE, THE "V" IS THE RESULT FROM AN ALU OPERATION WHICH IS STORED ON A DESTINATION REGISTER
					// USING HASHTABLE LETS HAVE A TABLE THAT KEEPS REGISTER INDEX WITH ITS UPDATED LATEST VALUE 
				
					if (inst.opcode !=4 && inst.opcode!=7 && inst.R1 != 0)
						hazardControl.put(inst.R1, v);
	
					if ((inst.opcode == 4 && v == 1) || inst.opcode == 7) {
						skippedInstruction.add(decodedInstructions.get(index+1));
					}
					
						
					System.out.println("Executed Output: " + v);
					System.out.println("The execute parameters: " + inst);
				}
				
			}

			if (clkCycle % 2 == 0 && clkCycle >= 6 && clkCycle <= fetchBoundaries + 5 ) { // memory
				int index = memoryInstructions.size();
				int v = executedInstructions.get(index);
				Instruction inst = decodedInstructions.get(index);
				if (skippedInstruction.contains(inst) ) {
					memoryInstructions.add(0);
					System.out.println("Memory Output: null, skipped instruction");
				}
				else {
					int t = memory(inst, v);
					memoryInstructions.add(t);
					System.out.println("Memory Output: " + t);
					System.out.println("The memory parameters: " + inst + " AND " + v);
				}
				

			}

			if (clkCycle % 2 != 0 && clkCycle >= 7 ) { // writeback
				int index = wbInstructions.size();
				int v = memoryInstructions.get(index);
				Instruction inst = decodedInstructions.get(index);
				if (skippedInstruction.contains(inst) ) {
					wbInstructions.add(0);
					System.out.println("Write back Parameters: null, skipped instruction");
				}
				else {
					writeBack(inst, v);
					wbInstructions.add(index);
					System.out.println("Write back Parameters : " + inst + " AND " + v);
				}
				
			}

			clkCycle++;
		}
		for (int i = 0; i < 32; i++) {
			System.out.println("R" + i + " = " + registerFile.registerFile[i]);
		}
		System.out.println("PC = " + registerFile.pc);

		
		for(int i=0; i<2048 ; i++) {
			System.out.println("Memory[" + i + "] = " +memory.memory[i] ); 
		  }
		
		//System.out.println(hazardControl);
		 
	}

	public static void main(String[] args) {

		Main main = new Main();

	}
}
