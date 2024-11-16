package com.wiinvent.lotusmile.app.response.base;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {

  public final List<T> data;
  public final Metadata metadata;

  public static <T> PageResponse<T> createFrom(Page<T> pageData) {
    PageResponse<T> pageResponse = new PageResponse(pageData.getContent(), Metadata.createFrom(pageData));
    return pageResponse;
  }
}