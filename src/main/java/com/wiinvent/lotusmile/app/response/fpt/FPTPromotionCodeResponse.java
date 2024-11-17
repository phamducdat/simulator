package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class FPTPromotionCodeResponse extends BaseFPTResponse {

  private List<Data> data;

  public static FPTPromotionCodeResponse createFakeData(String identifierNo) {
    FPTPromotionCodeResponse response = new FPTPromotionCodeResponse();
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    response.setErrorMessage("Fake data");
    List<Data> listData = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      Data data = new Data();
      data.setIdentifierNo(identifierNo);
      data.setPromotionCode("PROMO" + (100 + i));
      data.setEffectiveFromDate("2023-0" + i + "-01");
      data.setEffectiveToDate("2023-0" + (i + 1) + "-01");
      data.setStatus(i % 2 == 0 ? "ACTIVE" : "INACTIVE"); // Alternate status
      data.setTransactionType(i % 2 == 0 ? "Mua dặm thưởng" : "Chuyển nhượng dặm thưởng");
      data.setTransactionTypeId(i % 2 == 0 ? AirPromotionTransactionType.BUYQM : AirPromotionTransactionType.BUYQS);
      data.setDescription("Khuyến mại Chuyển nhượng Dặm tháng 9; Ưu đãi đặc biệt chỉ có trên Website; Giảm 40% giá bán");

      listData.add(data);
    }
    response.setData(listData);
    return response;
  }

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

  public enum AirPromotionTransactionType {

    @Schema(description = "Mua Dặm Thưởng")
    POINT,

    @Schema(description = "Mua Dặm xét hạng")
    BUYBM,

    @Schema(description = "Mua Chặng xét hạng")
    BUYQM,

    @Schema(description = "Chuyển đổi Dặm xét hạng")
    BUYQS,

    @Schema(description = "Chuyển đổi Dặm xét hạng")
    CVTQM,

    @Schema(description = "Mua/chuyển nhượng dặm")
    CVTQS,

    @Schema(description = "Gia hạn dặm")
    PROLONG,

    @Schema(description = "Khôi phục dặm")
    REINST,

    @Schema(description = "Chuyển nhượng dặm thưởng")
    TRANSFER
  }

  @lombok.Data
  public static class Data {
    private String identifierNo;
    private String promotionCode;
    private String effectiveFromDate;
    private String effectiveToDate;
    private String status;
    private String createdTime;
    private String transactionType;
    private String landingPageUrl;
    private String description;
    private AirPromotionTransactionType transactionTypeId;
    private String usedQuantity;
    private String remaingQuantity;
    private String maxQuantity;
  }
}
