package com.wiinvent.lotusmile.app.response;

import com.wiinvent.lotusmile.domain.entity.types.fpt.Gender;
import com.wiinvent.lotusmile.domain.entity.types.fpt.NameOrdering;
import com.wiinvent.lotusmile.domain.entity.types.fpt.PersonalType;
import com.wiinvent.lotusmile.domain.entity.types.fpt.Title;
import com.wiinvent.lotusmile.domain.util.Helper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerInfoResponse {

  @Schema(description = "Danh xưng", example = "Mrs")
  private Title title;

  @Schema(description = "Họ")
  private String firstName;

  @Schema(description = "Tên")
  private String lastName;

  @Schema(description = "")
  private Long accountId;

  @Schema(description = "")
  private Long customerId;

  @Schema(description = "Mã bông sen vàng")
  private Long mainIdentifier;

  @Schema(description = "Tên hiển thị trên thẻ")
  private NameOrdering nameOrdering;

  @Schema(description = "Họ và tên(bản địa)")
  private String localFullName;

  @Schema(description = "Ngày sinh", example = "20/11/1992")
  private String dateOfBirth;

  @Schema(description = "Giới tính", example = "F")
  private Gender gender;

  @Schema(description = "Số giấy tờ", example = "102312312412")
  private String identityCardNumber;

  @Schema(description = "Ngôn ngữ", example = "vi")
  private String preferredLanguage;

  @Schema(description = "Quốc tịch", example = "VN")
  private String nationality;

  @Schema(description = "Công ty")
  private String companyName;

  @Schema(description = "Chức danh")
  private String businessTitle;

  @Schema(description = "Địa chỉ liên hệ")
  private AddressInfo address;

  @Schema(description = "Thông tin giấy tờ cá nhân")
  private PersonalDocumentInfo personalDocument;

  @Schema(description = "Trạng thái tài khoản, A: Active, B: blocked, S: Suspended", example = "A")
  private String status;

  @Schema(description = "Tên trạng thái", example = "Active")
  private String statusName;

  @Schema(description = "Ngày tạo tài khoản")
  private String enrolmentDate;

  @Schema(description = "Địa chỉ (bản địa)")
  private String localAddress;

  @Data
  public static class AddressInfo {

    @Schema(description = "Địa chỉ", example = "Long Biên, Hà Nội")
    private String street;

    @Schema(description = "Quận/Thành phố", example = "ha noi")
    private String city;

    @Schema(description = "Quốc gia/Vùng", example = "VN")
    private String country;

    @Schema(description = "Tỉnh/Bang", example = "BU")
    private String region;

    @Schema(description = "Mã bưu chính", example = "10000")
    private String postalCode;

    @Schema(description = "Email", example = "danielcraig@gmail.com")
    private String email;

    @Schema(description = "Số điện thoại di động", example = "983444333")
    private String phoneNumber;

    @Schema(description = "Mã điện thoại", example = "84")
    private String phoneCountryCode;
  }

  @Data
  public static class PersonalDocumentInfo {

    @Schema(description = "Số định giấy tờ", example = "102312312412")
    private String personalId;

    @Schema(description = "Loại giấy tờ, I - Identify, P - passport", example = "I")
    private PersonalType personalType;

    @Schema(description = "Nơi cấp", example = "VN")
    private String personalCountryCodeIssue;

    @Schema(description = "Ngày cấp", example = "03/04/2021")
    private String personalIdDateIssue;

    public void setPersonalIdDateIssue(String personalIdDateIssue) {
      this.personalIdDateIssue = Helper.convertToDefaultDateTimeApp(personalIdDateIssue);
    }
  }

}
