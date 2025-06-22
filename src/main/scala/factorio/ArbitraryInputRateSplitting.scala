package factorio

import chisel3._
import _root_.circt.stage.ChiselStage
import scala.sys.process._
import java.io.File

// This hardware assumes that one second is 30 clock cycles, because 30 Hz is the fastest
// clock you can make in Factorio.
class ArbitraryInputRateSplitting extends Module {
    val io = IO(new Bundle {
        val removedFromBeltCount = Input(SInt(32.W))
        val desiredItemsPerTenSeconds = Input(SInt(32.W))
        val inserterStackSize = Output(SInt(32.W))
        val inserterNotKeepingPace = Output(SInt(32.W))
    })

    val itemsSinceLastTargetReset = RegInit(0.S(32.W))
    val timeSinceLastTargetReset = RegInit(0.S(32.W))
    when(timeSinceLastTargetReset >= 299.S && itemsSinceLastTargetReset < io.desiredItemsPerTenSeconds) {
      io.inserterNotKeepingPace := 1.S
    }.otherwise {
      io.inserterNotKeepingPace := 0.S
    }
    when(timeSinceLastTargetReset >= 299.S) {
      timeSinceLastTargetReset := 0.S
      itemsSinceLastTargetReset := 0.S
    }.otherwise {
      timeSinceLastTargetReset := timeSinceLastTargetReset + 1.S
      itemsSinceLastTargetReset := itemsSinceLastTargetReset + io.removedFromBeltCount
    }
    io.inserterStackSize := io.desiredItemsPerTenSeconds - itemsSinceLastTargetReset
}

/**
 * Generate Verilog sources and save it in file ArbitraryRatioSplitting.sv, then run it through
 * verilog2factorio to get a blueprint file which is saved to ArbitraryRatioSplitting.blueprint.txt
 */
object ArbitraryInputRateSplitting extends App {
  ChiselStage.emitSystemVerilogFile(
    new ArbitraryInputRateSplitting,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info", "--lowering-options=disallowLocalVariables")
  )
  val oldFile = new File("ArbitraryInputRateSplitting.blueprint.txt")
  if (oldFile.exists) {
    oldFile.delete()
  }
  print(Seq("v2f-adapter", "--output", "ArbitraryInputRateSplitting.blueprint.txt", "--file", "ArbitraryInputRateSplitting.sv").!!)
}