package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.app.response.fpt.FPTAccountSummaryResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.domain.entity.types.fpt.TierCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {


  @Schema(description = "Họ và tên", example = "Daniel Craig")
  private String embossedName;

  @Schema(description = "Thẻ của tôi")
  private MyCardResponse myCard;

  @Schema(description = "Thẻ Visa - Chưa có thông tin, chưa triển khai")
  private VisaResponse visaCard;

  @Schema(description = "User ID của hội viên", example = "34385965")
  private Integer userId;

  @Schema(description = "Customer ID của hội viên", example = "3331257")
  private String customerId;

  @Schema(description = "Account ID của hội viên", example = "3296111")
  private String accountId;

  @Schema(description = "Email", example = "danielcraig@gmail.com")
  private String email;

  @Schema(description = "Mobile", example = "0984110067")
  private String mobile;

  @Schema(description = "Hạng thẻ hiện tại", example = "Million Miler")
  private String currentTier;

  @Schema(description = "Mã hạng thẻ. R: Đăng ký, S: Bạc, G: Vàng, T: Titan, P:Platium hoặc Milion Miler", example = "P")
  private TierCode currentTierCode;

  @Schema(description = "Số dặm hiện tại", example = "1130273")
  private Long availableBonusMiles;

  public static UserSummaryResponse createFrom(Integer userId, FPTAccountSummaryResponse fptAccountSummaryResponse, FPTCustomerProfileResponse customerProfileResponse) {
    UserSummaryResponse userSummaryResponse = UserSummaryResponse.builder()
        .userId(userId)
        .customerId(String.valueOf(fptAccountSummaryResponse.getData().getCustomerId()))
        .accountId(String.valueOf(fptAccountSummaryResponse.getData().getAccountId()))
        .embossedName(customerProfileResponse.getData().getEmbossedName())
        .email(fptAccountSummaryResponse.getData().getEmail())
        .mobile(customerProfileResponse.getData().getAddress().getPhoneNumber())
        .currentTier(fptAccountSummaryResponse.getData().getCurrentTier())
        .currentTierCode(fptAccountSummaryResponse.getData().getCurrentTierCode())
        .availableBonusMiles(fptAccountSummaryResponse.getData().getAvailableBonusMiles())
        .build();
    userSummaryResponse.setMyCard(new MyCardResponse(fptAccountSummaryResponse, customerProfileResponse));
    return userSummaryResponse;
  }

}
