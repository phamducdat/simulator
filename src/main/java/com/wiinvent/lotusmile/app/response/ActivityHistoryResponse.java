package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.app.response.fpt.FPTActivityHistoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.wiinvent.lotusmile.domain.util.Helper.convertToDefaultDateTimeApp;

@Data
@Builder
public class ActivityHistoryResponse {

  private Long id;

  @Schema(description = "Loại giao dịch (đang hỏi)", example = "Points Transfer+")
  private String type;

  @Schema(description = "Dặm thưởng, trường hợp trừ dặm sẽ trả về số âm", example = "100")
  private Long bonusMiles;

  @Schema(description = "Dặm xét hạng tích lũy", example = "10")
  private Long qualifyingMiles;

  @Schema(description = "Ngày giao dịch", example = "25/07/2024")
  private String activityDate;

  @Schema(description = "Mã trạng thái của giao dịch", example = "B")
  private String status;

  @Schema(description = "Tên trạng thái của giao dịch", example = "Booked")
  private String statusName;

  @Schema(description = "Mô tả thêm giao dịch nếu có", example = "Thu chut")
  private String comment;

  @Schema(description = "Nguồn tạo giao dịch", example = "f")
  private String source;

  public static ActivityHistoryResponse mapFrom(FPTActivityHistoryResponse.Data fptActivityHistoryDataResponse) {
    return ActivityHistoryResponse.builder()
        .id(fptActivityHistoryDataResponse.getId())
        .type(fptActivityHistoryDataResponse.getType())
        .bonusMiles(fptActivityHistoryDataResponse.getBonusMiles())
        .qualifyingMiles(fptActivityHistoryDataResponse.getQualifyingMiles() != null ? fptActivityHistoryDataResponse.getQualifyingMiles() : 0L)
        .activityDate(convertToDefaultDateTimeApp(fptActivityHistoryDataResponse.getActivityDate()))
        .status(fptActivityHistoryDataResponse.getStatus())
        .statusName(fptActivityHistoryDataResponse.getStatusName())
        .comment(fptActivityHistoryDataResponse.getComment())
        .source(fptActivityHistoryDataResponse.getSource())
        .build();
  }
}
