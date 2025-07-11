// Generated by CIRCT firtool-1.114.1
module RatioMaestro(
  input         clock,
                reset,
  input  signed [31:0] io_siteATarget,
                io_siteBTarget,
                io_siteAProduction,
                io_siteBProduction,
  output signed [31:0] io_siteAEnabled,
                io_siteBEnabled,
                io_siteAProductionBudgetOut,
                io_siteBProductionBudgetOut,
                io_deadlockResetUsed
);

  reg  signed [31:0] siteAProductionBudget;
  reg  signed [31:0] siteBProductionBudget;
  wire signed _GEN = siteAProductionBudget < 32'sh1 & siteBProductionBudget < 32'sh1;
  always @(posedge clock) begin
    if (reset) begin
      siteAProductionBudget <= 32'h0;
      siteBProductionBudget <= 32'h0;
    end
    else begin
      siteAProductionBudget <=
        _GEN
          ? siteAProductionBudget + io_siteATarget * 32'sd100000
          : siteAProductionBudget + io_siteBProduction * io_siteATarget * 32'sd100000 / io_siteBTarget - io_siteAProduction
            * 32'sd100000;
      siteBProductionBudget <=
        _GEN
          ? siteBProductionBudget + io_siteBTarget * 32'sd100000
          : siteBProductionBudget + io_siteAProduction * io_siteBTarget * 32'sd100000 / io_siteATarget - io_siteBProduction
            * 32'sd100000;
    end
  end // always @(posedge)
  assign io_siteAEnabled = {31'h0, siteAProductionBudget > 32'sh0};
  assign io_siteBEnabled = {31'h0, siteBProductionBudget > 32'sh0};
  assign io_siteAProductionBudgetOut = siteAProductionBudget;
  assign io_siteBProductionBudgetOut = siteBProductionBudget;
  assign io_deadlockResetUsed = {31'h0, _GEN};
endmodule

