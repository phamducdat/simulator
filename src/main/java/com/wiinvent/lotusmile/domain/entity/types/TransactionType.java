package com.wiinvent.lotusmile.domain.entity.types;

import io.swagger.v3.oas.annotations.media.Schema;

public enum TransactionType {
  //  TELECOM,
//  TICKET,
//  UPGRADE,
//  SSR,
//  TRANSFER,
//  CONVERT,
  UPDATE_PROFILE,
  @Schema(description = "Đổi mật khẩu")
  CHANGE_PASS,
  @Schema(description = "Quên mật khẩu")
  RESET_PASS,
//  CANCEL_SEGMENT,
//  DONATION,
}
