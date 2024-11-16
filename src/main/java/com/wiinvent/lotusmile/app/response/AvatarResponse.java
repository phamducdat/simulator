package com.wiinvent.lotusmile.app.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AvatarResponse {

  @Schema(description = "Avatar hội viên, định dạng byte[]", example = "2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys")
  private byte[] avatar;
}
