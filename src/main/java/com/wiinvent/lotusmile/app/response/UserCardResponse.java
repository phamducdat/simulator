package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.app.response.fpt.FPTAccountSummaryResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.wiinvent.lotusmile.domain.util.Helper.convertToDefaultDateTimeApp;

@Data
@Builder
public class UserCardResponse {

  @Schema(description = "Kỳ xét hạng hiện tại kết thúc", example = "31/07/2024")
  public String currentQualifyingPeriodEndDate;

  @Schema(description = "Thẻ của tôi")
  private MyCardResponse myCard;

  @Schema(description = "Dặm thưởng hiện tại", example = "36937")
  private Long availableBonusMiles;

  @Schema(description = "Kỳ xét hạng hiện tại bắt đầu", example = "01/07/2024")
  private String currentQualifyingPeriodStartDate;

  @Schema(description = "Tổng số dặm xét hạng", example = "0")
  private Long totalQualifyingMiles;

  @Schema(description = "Tổng số chặng bay xét hạng", example = "0")
  private Double totalQualifyingSegments;

  @Schema(description = "Số dặm để nâng hạng thẻ", example = "15000")
  private Long qualifyingMilesToUpgrade;

  @Schema(description = "Số chặng cần để nâng hạng thẻ", example = "18")
  private Long qualifyingSegmentsToUpgrade;

  @Schema(description = "Số dặm xét hạng cần tích lũy thêm để duy trì hạng thẻ", example = "50000")
  private Long qualifyingMilesToMaintain;

  @Schema(description = "Số chặng bay cần tích lũy thêm để duy trì hạng", example = "18")
  private Long qualifyingSegmentsToMaintain;

  // TODO: get from customer profile
  public static UserCardResponse mapFrom(FPTAccountSummaryResponse fptAccountSummaryResponse, FPTCustomerProfileResponse customerProfile) {
    UserCardResponse userCardResponse = UserCardResponse.builder()
        .availableBonusMiles(fptAccountSummaryResponse.getData().getAvailableBonusMiles())
        .currentQualifyingPeriodStartDate(convertToDefaultDateTimeApp(fptAccountSummaryResponse.getData().getCurrentQualifyingPeriodStartDate()))
        .currentQualifyingPeriodEndDate(convertToDefaultDateTimeApp(fptAccountSummaryResponse.getData().getCurrentQualifyingPeriodEndDate()))
        .totalQualifyingMiles(fptAccountSummaryResponse.getData().getTotalQualifyingMiles())
        .totalQualifyingSegments(fptAccountSummaryResponse.getData().getTotalQualifyingSegments())
        .qualifyingMilesToUpgrade(fptAccountSummaryResponse.getData().getQualifyingMilesToUpgrade())
        .qualifyingSegmentsToUpgrade(fptAccountSummaryResponse.getData().getQualifyingSegmentsToUpgrade())
        .qualifyingMilesToMaintain(fptAccountSummaryResponse.getData().getQualifyingMilesToMaintain())
        .qualifyingSegmentsToMaintain(fptAccountSummaryResponse.getData().getQualifyingSegmentsToMaintain())
        .build();
    userCardResponse.setMyCard(new MyCardResponse(fptAccountSummaryResponse, customerProfile));
    return userCardResponse;
  }


}
