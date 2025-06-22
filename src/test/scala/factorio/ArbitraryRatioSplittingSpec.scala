package factorio

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
// *****************************************************
// THIS TEST IS INCOMPLETE AND DOESN'T VALIDATE ANYTHING
// *****************************************************

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly factorio.ArbitraryRatioSplittingSpec
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly factorio.ArbitraryRatioSplittingSpec'
  * }}}
  * Testing from mill:
  * {{{
  * mill my-factorio-computers.test.testOnly factorio.ArbitraryRatioSplittingSpec
  * }}}
  */
class ArbitraryRatioSplittingSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "Should drive an inserter to only pull out the correct amount of items from the belt" in {
    simulate(new ArbitraryRatioSplitting()) { dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.clock.step()
      dut.input.numerator.poke(1.S)
      dut.input.denominator.poke(6.S)
      dut.input.enterBeltCount(6.S)
    }
  }
}