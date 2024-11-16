package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.app.response.fpt.FPTAccountSummaryResponse;
import com.wiinvent.lotusmile.domain.entity.Config;
import com.wiinvent.lotusmile.domain.entity.types.fpt.TierCode;
import com.wiinvent.lotusmile.domain.util.Helper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LevelResponse {

  public LevelResponse(FPTAccountSummaryResponse accountSummaryResponse, Config.RankingLevel currentRankConfig, Config.RankingLevel upgradeRankConfig) {
    this.currentTier = accountSummaryResponse.getData().getCurrentTier();
    this.currentTierCode = accountSummaryResponse.getData().getCurrentTierCode();
    this.currentTierData = new CurrentTierData(accountSummaryResponse, currentRankConfig);
    this.upgradeTierData = new UpgradeTierData(accountSummaryResponse, upgradeRankConfig);
    this.millionMiller = accountSummaryResponse.getData().getMillionMiller();
  }

  @Schema(description = "Hạng thẻ hiện tại", example = "Vàng")
  private String currentTier;

  @Schema(description = "Mã hạng thẻ.R: Đăng ký, S: Bạc, G: Vàng, T: Titan, P: Platium hoặc Milion Miler", example = "G")
  private TierCode currentTierCode;

  @Schema(description = "Thông tin hạng thẻ hiện tại")
  private CurrentTierData currentTierData;

  @Schema(description = "Thông tin hạng thẻ tiếp theo")
  private UpgradeTierData upgradeTierData;

  @Schema(description = "Hội viên là hội viên triệu dặm, dùng để phân biệt P của Platium hoặc Million Miler", example = "false")
  private Boolean millionMiller;


  @Data
  public static class CurrentTierData {

    @Schema(description = "Hạng thẻ hiện tại", example = "Vàng")
    private String currentTier;

    @Schema(description = "Mã hạng thẻ.R: Đăng ký, S: Bạc, G: Vàng, T: Titan, P: Platium hoặc Milion Miler", example = "G")
    private TierCode currentTierCode;

    @Schema(description = "Kỳ xét hạng hiện tại kết thúc", example = "30/11/2024")
    public String currentQualifyingPeriodEndDate;

    @Schema(description = "Dặm xét hạng hiện tại", example = "6000")
    private Long availableBonusMiles;

    @Schema(description = "Dặm xét hạng mục tiêu", example = "30000")
    private Long targetBonusMiles;

    @Schema(description = "Tổng số chặng bay xét hạng", example = "11")
    private Double totalQualifyingSegments;

    @Schema(description = "Tổng số chặng bay mục tiêu", example = "27")
    private Double targetQualifyingSegments;

    public CurrentTierData(FPTAccountSummaryResponse accountSummaryResponse, Config.RankingLevel currentRankConfig) {
      this.currentTier = accountSummaryResponse.getData().getCurrentTier();
      this.currentTierCode = accountSummaryResponse.getData().getCurrentTierCode();
      this.currentQualifyingPeriodEndDate = Helper.convertToDefaultDateTimeApp(accountSummaryResponse.getData().getCurrentQualifyingPeriodEndDate());
      this.availableBonusMiles = accountSummaryResponse.getData().getAvailableBonusMiles();
      this.totalQualifyingSegments = accountSummaryResponse.getData().getTotalQualifyingSegments();

      this.targetBonusMiles = currentRankConfig.getQualifyingMiles();
      this.targetQualifyingSegments = currentRankConfig.getQualifyingSegments();
    }
  }

  @Data
  public static class UpgradeTierData {

    @Schema(description = "Hạng thẻ tiếp theo", example = "Bạch Kim")
    private String upgradeTier = "";

    @Schema(description = "Mã hạng thẻ tiếp theo.R: Đăng ký, S: Bạc, G: Vàng, T: Titan, P: Platium hoặc Milion Miler", example = "P")
    private TierCode upgradeTierCode = null;

    @Schema(description = "Kỳ xét hạng hiện tại kết thúc", example = "30/11/2024")
    public String currentQualifyingPeriodEndDate;

    @Schema(description = "Dặm xét hạng hiện tại", example = "6000")
    private Long availableBonusMiles;

    @Schema(description = "Dặm xét hạng mục tiêu", example = "50000")
    private Long targetBonusMiles = 0L;

    @Schema(description = "Tổng số chặng bay xét hạng", example = "11")
    private Double totalQualifyingSegments;

    @Schema(description = "Tổng số chặng mục tiêu", example = "45")
    private Double targetQualifyingSegments = 0D;

    public UpgradeTierData(FPTAccountSummaryResponse accountSummaryResponse, Config.RankingLevel upgradeRankConfig) {
      this.currentQualifyingPeriodEndDate = Helper.convertToDefaultDateTimeApp(accountSummaryResponse.getData().getCurrentQualifyingPeriodEndDate());
      this.availableBonusMiles = accountSummaryResponse.getData().getAvailableBonusMiles();
      this.totalQualifyingSegments = accountSummaryResponse.getData().getTotalQualifyingSegments();
      if (upgradeRankConfig != null) {
        this.upgradeTierCode = upgradeRankConfig.getTierCode();
        this.upgradeTier = upgradeRankConfig.getTier().get("vi");
        this.targetBonusMiles = upgradeRankConfig.getQualifyingMiles();
        this.targetQualifyingSegments = upgradeRankConfig.getQualifyingSegments();
      }
    }
  }

}
