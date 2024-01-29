// This file contains ALU control logic.

package dinocpu.components

import chisel3._
import chisel3.util._

/**
 * The ALU control unit
 *
 * Input:  aluop        Specifying the type of instruction using ALU
 *                          . 0 for none of the below
 *                          . 1 for arithmetic instruction types (R-type or I-type)
 *                          . 2 for non-arithmetic instruction types that uses ALU (auipc/jal/jarl/Load/Store)
 * Input:  arth_type    The type of instruction (0 for R-type, 1 for I-type)
 * Input:  int_length   The integer length (0 for 64-bit, 1 for 32-bit)
 * Input:  funct7       The most significant bits of the instruction.
 * Input:  funct3       The middle three bits of the instruction (12-14).
 *
 * Output: operation    What we want the ALU to do.
 *
 * For more information, see Section 4.4 and A.5 of Patterson and Hennessy.
 * This is loosely based on figure 4.12
 */
class ALUControl extends Module {
  val io = IO(new Bundle {
    val aluop       = Input(UInt(2.W))
    val arth_type   = Input(UInt(1.W))
    val int_length  = Input(UInt(1.W))
    val funct7      = Input(UInt(7.W))
    val funct3      = Input(UInt(3.W))

    val operation   = Output(UInt(5.W))
  })

  io.operation := "b11111".U // Invalid

  // Your code goes here

when(io.aluop === 1.U) {
  //R types
  when(io.arth_type === 0.U){
      //RV64I
      when(io.int_length === 0.U) {
        //64 bit

        when(io.funct7 === "b0000000".U){
          when(io.funct3 === "b000".U) {
            io.operation := "b00000".U    //ADD
          }
          when(io.funct3 === "b001".U) {
            io.operation := "b01010".U    //SLL
          }
          when(io.funct3 === "b010".U) {
            io.operation := "b01100".U    //SLT
          }
          when(io.funct3 === "b011".U) {
            io.operation := "b01111".U    //SLTU
          }
          when(io.funct3 === "b100".U) {
            io.operation := "b01000".U    //XOR
          }
          when(io.funct3 === "b101".U) {
            io.operation := "b01011".U    //SRL
          }
          when(io.funct3 === "b110".U) {
            io.operation := "b00111".U    //OR
          }
          when(io.funct3 === "b111".U) {
            io.operation := "b00101".U    //AND
          }
        }
        when(io.funct7 === "b0100000".U) {
          when(io.funct3 === "b000".U) {
            io.operation := "b00001".U    //SUB
          }
          when(io.funct3 === "b101".U) {
            io.operation := "b01001".U    //SRA
          }
        }
        when(io.funct7 === "b0000001".U) {
          when(io.funct3 === "b000".U){
            io.operation := "b00010".U    //MUL
          }
          when(io.funct3 === "b001".U) {
            io.operation := "b10101".U    //MULH
          }
          when(io.funct3 === "b010".U) {
            io.operation := "b11000".U    //MULHSU
          }
          when(io.funct3 === "b011".U) {
            io.operation := "b10111".U    //MULHU
          }
          when(io.funct3 === "b100".U){
            io.operation := "b00011".U    //DIV
          }
          when(io.funct3 === "b101".U) {
            io.operation := "b01101".U    //DIVU
          }
          when(io.funct3 === "b110".U) {
            io.operation := "b00100".U    //REM
          }
          when(io.funct3 === "b111".U) {
            io.operation := "b01110".U    //REMU
          }

        }
      }

      //32 bit
      when(io.int_length === 1.U) {
        when(io.funct7 === "b0000000".U){
          when(io.funct3 === "b000".U) {
            io.operation := "b10000".U   //ADDW
          }
          when(io.funct3 === "b001".U) {
            io.operation := "b11010".U    //SSLW
          }
          when(io.funct3 === "b101".U) {
            io.operation := "b11011".U    //SRLW
          }
        }

        when(io.funct7 === "b0100000".U) {
          when(io.funct3 === "b000".U){
            io.operation := "b10001".U    //SUBW
          }
          when(io.funct3 === "b101".U) {
            io.operation := "b11001".U    //SRAW
          }
        }

        when(io.funct7 === "b0000001".U) {
          when(io.funct3 === "b000".U) {
            io.operation := "b10010".U    //MULW
          }
          when(io.funct3 === "b100".U) {
            io.operation := "b10011".U    //DIVW
          }
          when(io.funct3 === "b101".U) {
            io.operation := "b11101".U    //DIVUW
          }
          when(io.funct3 === "b110".U) {
            io.operation := "b10100".U    //REMW
          }
          when(io.funct3 === "b111".U) {
            io.operation := "b11110".U    //REMUW
          }
        }
      }

    }

  
  
  when(io.arth_type === 1.U) { // I-type instruction
    //64 bit
    when(io.int_length === 0.U){ 

      when(io.funct3 === "b000".U) {
        io.operation := "b00000".U // ADDI
      }

      when(io.funct3 === "b010".U) {
        io.operation := "b01100".U // SLTI
      }

      when(io.funct3 === "b011".U) {
        io.operation := "b01111".U // SLTIU
      }

      when(io.funct3 === "b100".U) {
        io.operation := "b00101".U // ANDI
      }

      when(io.funct3 === "b110".U) {
        io.operation := "b00111".U // ORI
      }

      when(io.funct3 === "b111".U) {
        io.operation := "b01000".U // XORI
      }

    }

  }
  
 }
}

/*
        when(io.funct3 === "b000".U) {
          io.operation := "b00000".U // ADDI
        }

        when(io.funct3 === "b001".U) {
          io.operation := "b01010".U // SLLI
        }

        when(io.funct3 === "b010".U) {
          io.operation := "b01100".U // SLTI
        }

        when(io.funct3 === "b011".U) {
          io.operation := "b01111".U // SLTIU
        }

        when(io.funct3 === "b100".U) {
          io.operation := "b00101".U // ANDI
        }

        when(io.funct3 === "b110".U) {
          io.operation := "b00111".U // ORI
        }

        when(io.funct3 === "b111".U) {
          io.operation := "b01000".U // XORI
        }
      }

      // 32 bit
      when(io.int_length === 1.U) {
        when(io.funct3 === "b001".U) {
          io.operation := "b01010".U // SLLI
        }

        when(io.funct3 === "b010".U) {
          io.operation := "b01100".U // SLTI
        }

        when(io.funct3 === "b011".U) {
          io.operation := "b01111".U // SLTIU
        }

        when(io.funct3 === "b100".U) {
          io.operation := "b00101".U // ANDI
        }

        when(io.funct3 === "b110".U) {
          io.operation := "b00111".U // ORI
        }

        when(io.funct3 === "b111".U) {
          io.operation := "b01000".U // XORI
        }
*/