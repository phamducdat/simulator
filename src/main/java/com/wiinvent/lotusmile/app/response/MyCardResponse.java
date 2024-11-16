package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.app.response.fpt.FPTAccountSummaryResponse;
import com.wiinvent.lotusmile.app.response.fpt.FPTCustomerProfileResponse;
import com.wiinvent.lotusmile.domain.util.Helper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyCardResponse {

  @Schema(description = "NAME", example = "PHAM VAN A")
  private String embossedName;

  @Schema(description = "MEMBER SINCE", example = "06/24")
  private String memberSince;

  @Schema(description = "VALID THROUGH, case không có: \"\"", example = "08/25")
  private String validThrough;

  @Schema(description = "Mã bông sen vàng", example = "0234345623442345")
  private String mainIdentifier;

  public MyCardResponse(FPTAccountSummaryResponse fptAccountSummaryResponse, FPTCustomerProfileResponse customerProfile) {
    this.setEmbossedName(fptAccountSummaryResponse.getData().getEmbossedName());
    this.setMainIdentifier(String.valueOf(customerProfile.getData().getMainIdentifier()));
    this.setMemberSince(Helper.convertDateMMYY(fptAccountSummaryResponse.getData().getEnrolmentDate()));

    // Trường hợp tài khoản mới tạo, không có currentTier => trả ""
//    this.setValidThrough(Helper.convertDateMMYY(fptAccountSummaryResponse.getData().getTierUpgradeEndDate()));
    this.setValidThrough(fptAccountSummaryResponse.getData().getCurrentTierEndDate() != null ? Helper.convertDateMMYY(fptAccountSummaryResponse.getData().getCurrentTierEndDate()) : "");
  }

}
