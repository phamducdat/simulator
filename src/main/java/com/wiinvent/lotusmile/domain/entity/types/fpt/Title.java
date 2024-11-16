package com.wiinvent.lotusmile.domain.entity.types.fpt;

public enum Title {
  Mr,
  MS,
  Mrs,
  Mis,
  Mstr;

  @Deprecated(forRemoval = true)
  public Gender getGender() {
    return switch (this) {
      case Mr, Mstr -> Gender.M;
      case MS, Mrs, Mis -> Gender.F;
      default -> Gender.M;
    };
  }

}