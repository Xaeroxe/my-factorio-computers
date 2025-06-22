package factorio

import chisel3._
import _root_.circt.stage.ChiselStage
import scala.sys.process._
import java.io.File

class ArbitraryRatioSplitting extends Module {
    val io = IO(new Bundle {
        val enterBeltCount = Input(SInt(32.W))
        val removedFromBeltCount = Input(SInt(32.W))
        val numerator = Input(SInt(32.W))
        val denominator = Input(SInt(32.W))
        val inserterStackSize = Output(SInt(32.W))
    })

    val itemsSinceLastIncrement = RegInit(0.S(32.W))
    val itemCountPendingGrab = RegInit(0.S)

    when(itemsSinceLastIncrement >= io.denominator) {
        itemCountPendingGrab := itemCountPendingGrab + io.numerator - io.removedFromBeltCount
        itemsSinceLastIncrement := itemsSinceLastIncrement + io.enterBeltCount - io.denominator
    }.otherwise {
        itemCountPendingGrab := itemCountPendingGrab - io.removedFromBeltCount
        itemsSinceLastIncrement := itemsSinceLastIncrement + io.enterBeltCount
    }
    io.inserterStackSize := itemCountPendingGrab
}

/**
 * Generate Verilog sources and save it in file ArbitraryRatioSplitting.sv, then run it through
 * verilog2factorio to get a blueprint file which is saved to ArbitraryRatioSplitting.blueprint.txt
 */
object ArbitraryRatioSplitting extends App {
  ChiselStage.emitSystemVerilogFile(
    new ArbitraryRatioSplitting,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "--lowering-options=disallowLocalVariables")
  )
  val oldFile = new File("ArbitraryRatioSplitting.blueprint.txt")
  if (oldFile.exists) {
    oldFile.delete()
  }
  print(Seq("v2f-adapter", "--output", "ArbitraryRatioSplitting.blueprint.txt", "--file", "ArbitraryRatioSplitting.sv").!!)
}