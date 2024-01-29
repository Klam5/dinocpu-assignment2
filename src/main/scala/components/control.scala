// Control logic for the processor

package dinocpu.components

import chisel3._
import chisel3.util.{BitPat, ListLookup}

/**
 * Main control logic for our simple processor
 *
 * Input: opcode:                Opcode from instruction
 *
 * Output: aluop                Specifying the type of instruction using ALU
 *                                   . 0 for none of the below
 *                                   . 1 for arithmetic instruction types (R-type or I-type)
 *                                   . 2 for non-arithmetic instruction types that uses ALU (auipc/jal/jarl/Load/Store)
 * Output: arth_type            The type of instruction (0 for R-type, 1 for I-type)
 * Output: int_length           The integer length (0 for 64-bit, 1 for 32-bit)
 * Output: jumpop               Specifying the type of jump instruction (J-type/B-type)
 *                                   . 0 for none of the below
 *                                   . 1 for jal
 *                                   . 2 for jalr
 *                                   . 3 for branch instructions (B-type)
 * Output: memop                 Specifying the type of memory instruction (Load/Store)
 *                                   . 0 for none of the below
 *                                   . 1 for Load
 *                                   . 2 for Store
 * Output: op1_src               Specifying the source of operand1 of ALU/ControlTransferUnit
 *                                   . 0 if source is register file's readdata1
 *                                   . 1 if source is pc
 * Output: op2_src               Specifying the source of operand2 of ALU/ControlTransferUnit
 *                                   . 0 if source is register file's readdata2
 *                                   . 1 if source is immediate
 *                                   . 2 if source is a hardwired value 4
 * Output: writeback_src         Specifying the source of value written back to the register file
 *                                   . 0 if writeback is invalid
 *                                   . 1 to select alu result
 *                                   . 2 to select immediate generator result
 *                                   . 3 to select data memory result
 * Output: validinst             0 if the instruction is invalid, 1 otherwise
 *
 * For more information, see section 4.4 of Patterson and Hennessy.
 * This follows figure 4.22.
 */

class Control extends Module {
  val io = IO(new Bundle {
    val opcode            = Input(UInt(7.W))

    val aluop             = Output(UInt(2.W))
    val arth_type         = Output(UInt(1.W))
    val int_length        = Output(UInt(1.W))
    val jumpop            = Output(UInt(2.W))
    val memop             = Output(UInt(2.W))
    val op1_src           = Output(UInt(1.W))
    val op2_src           = Output(UInt(2.W))
    val writeback_src     = Output(UInt(2.W))
    val validinst         = Output(UInt(1.W))
  })


  val signals =
    ListLookup(io.opcode,
      /*default*/           List(     0.U,       0.U,         0.U,     0.U,   0.U,     0.U,     0.U,           0.U,       0.U),
      Array(              /*        aluop, arth_type,     int_length,   jumpop, memop, op1_src, op2_src, writeback_src, validinst*/
      // R-format 64-bit
      BitPat("b0110011") -> List(     1.U,       0.U,         0.U,     0.U,   0.U,     0.U,     0.U,           1.U,       1.U),
      

      //i type
      BitPat("b0010011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 1.U, 1.U), // ADDI
      BitPat("b0000011") -> List(0.U, 1.U, 0.U, 0.U, 1.U, 0.U, 0.U, 1.U, 1.U), // LW

      // I-format 32-bit
      BitPat("b0011011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 1.U, 1.U), // ADDI (same as 64-bit)
      BitPat("b0000011") -> List(0.U, 1.U, 0.U, 0.U, 1.U, 0.U, 0.U, 1.U, 1.U), // LW (same as 64-bit)
      
      // Additional I-type instructions
      BitPat("b0010011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U), // SLLI
      BitPat("b0010011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U), // SRLI
      BitPat("b0010011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U), // SRAI
      BitPat("b0011011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 1.U, 1.U), // ADDIW
      BitPat("b0011011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U), // SLLIW
      BitPat("b0011011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U), // SRLIW
      BitPat("b0011011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U)  // SRAIW

      ) // Array
    ) // ListLookup

  io.aluop             := signals(0)
  io.arth_type         := signals(1)
  io.int_length        := signals(2)
  io.jumpop            := signals(3)
  io.memop             := signals(4)
  io.op1_src           := signals(5)
  io.op2_src           := signals(6)
  io.writeback_src     := signals(7)
  io.validinst         := signals(8)
}
