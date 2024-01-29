// This file is where all of the CPU components are assembled into the whole CPU

package dinocpu

import chisel3._
import chisel3.util._
import dinocpu.components._

/**
 * The main CPU definition that hooks up all of the other components.
 *
 * For more information, see section 4.4 of Patterson and Hennessy
 * This follows figure 4.21
 */
class SingleCycleCPU(implicit val conf: CPUConfig) extends BaseCPU {
  // All of the structures required
  val pc              = dontTouch(RegInit(0.U(64.W)))
  val control         = Module(new Control())
  val registers       = Module(new RegisterFile())
  val aluControl      = Module(new ALUControl())
  val alu             = Module(new ALU())
  val immGen          = Module(new ImmediateGenerator())
  val jumpDetection   = Module(new JumpDetectionUnit())
  val jumpPcGen       = Module(new JumpPcGeneratorUnit())
  val (cycleCount, _) = Counter(true.B, 1 << 30)

  control.io := DontCare
  registers.io := DontCare
  aluControl.io := DontCare
  alu.io := DontCare
  immGen.io := DontCare
  jumpDetection.io := DontCare
  jumpPcGen.io := DontCare
  io.dmem <> DontCare

  //FETCH
  io.imem.address := pc
  io.imem.valid := true.B

  val instruction = Wire(UInt(32.W))
  when ((pc % 8.U) === 4.U) {
    instruction := io.imem.instruction(63, 32)
  } .otherwise {
    instruction := io.imem.instruction(31, 0)
  }

  // Your code goes here
  
  //controll ins and outs
  control.io.opcode := instruction(6,0)

  aluControl.io.arth_type := control.io.arth_type
  aluControl.io.int_length := control.io.int_length
  aluControl.io.aluop := control.io.aluop


  //aluControl unit ins and outs
  aluControl.io.funct7 := instruction(31,25)
  aluControl.io.funct3 := instruction(14, 12)

  alu.io.operation := aluControl.io.operation

  //alu ins and outs
  alu.io.operand1 := registers.io.readdata1
  alu.io.operand2 := registers.io.readdata2

  registers.io.writedata := alu.io.result

  //register files
  registers.io.readreg1 := instruction(19,15)
  registers.io.readreg2 := instruction(24,20)
  registers.io.writereg := instruction(11,7)
  registers.io.wen := (registers.io.writereg =/= 0.U) && (control.io.writeback_src =/= 0.U )
  pc := pc + 4.U
  // **** add mux(es) ****

}

/*
 * Object to make it easier to print information about the CPU
 */
object SingleCycleCPUInfo {
  def getModules(): List[String] = {
    List(
      "dmem",
      "imem",
      "control",
      "registers",
      "csr",
      "aluControl",
      "alu",
      "immGen",
      "jumpDetection",
      "jumpPcGen"
    )
  }
}


/*
// R-format 32-bit
      BitPat("b0111011") -> List(     1.U,       0.U,         1.U,     0.U,   0.U,     0.U,     0.U,           1.U,       1.U),
      


      // I-format 64 bit

      BitPat("b0010011") -> List(     1.U,       1.U,         0.U,     0.U,   0.U,     0.U,     1.U,           1.U,       1.U),   //ADDI
      
      
      
      // I-format 32 bit
       BitPat("b0010011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 0.U, 1.U), // SLLI
       BitPat("b0011011") -> List(1.U, 1.U, 0.U, 0.U, 0.U, 0.U, 1.U, 1.U, 1.U) // ADDIW
       */