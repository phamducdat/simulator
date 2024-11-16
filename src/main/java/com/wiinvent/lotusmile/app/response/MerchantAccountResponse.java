package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MerchantAccountResponse {
  private Integer id;

  @Schema(description = "Ảnh đại diện", example = "https://static1.dev.wiinvent.tv/2024/09/11/screenshot_from_2024-09-11_11-37-44_1726029484462.png")
  private String avatarUrl;

  @Schema(description = "Tên hiển thị", example = "Phạm Thu An")
  private String displayName;

  private String username;

  @Schema(description = "Quyền, MANAGER = Quản lý, STAFF = Nhân viên", example = "MANAGER")
  private MerchantAccountRole role;
}
