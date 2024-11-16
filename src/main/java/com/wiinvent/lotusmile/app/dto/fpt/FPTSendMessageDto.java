package com.wiinvent.lotusmile.app.dto.fpt;

import lombok.Data;
import lombok.NonNull;

@Data
public class FPTSendMessageDto {

  @NonNull
  private Long customerId;

  @NonNull
  private String messageContent;

}
