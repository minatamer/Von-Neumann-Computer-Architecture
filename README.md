# Von-Neumann-Computer-Architecture

Von Neumann Architecture is a digital computer architecture whose design is based on the
concept of stored program computers where program data and instruction data are stored
in the same memory.

This project simulated the pipelining of a processor for the 5 stages that a process goes through. All instructions regardless of their type must pass through all 5 stages even if they do not need to access a particular stage.
The 5 Stages are:
1) Instruction Fetch (IF): Fetches the next instruction from the main memory using the
address in the PC (Program Counter), and increments the PC.
2) Instruction Decode (ID): Decodes the instruction and reads any operands required from
the register file.
3) Execute (EX): Executes the instruction. In fact, all ALU operations are done in this stage.
4) Memory (MEM): Performs any memory access required by the current instruction. For
loads, it would load an operand from the main memory, while for stores, it would store an
operand into the main memory.
5) Write Back (WB): For instructions that have a result (a destination register), the Write
Back writes this result back to the register file.
