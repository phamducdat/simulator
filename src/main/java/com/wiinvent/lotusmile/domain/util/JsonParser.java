package com.wiinvent.lotusmile.domain.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An utility class for parsing json objects using Jackson data binding library
 *
 * @author Artemii Vishnevskii
 * @author Temaa.mann@gmail.com
 * @since 12.04.2016.
 */
@Log4j2
@UtilityClass
public class JsonParser {

  private static ObjectMapper mObjectMapper;

  /**
   * Creates an {@link ObjectMapper} for mapping json objects. Mapper can be configured here
   *
   * @return created {@link ObjectMapper}
   */
  public static ObjectMapper getMapper() {
    if (mObjectMapper == null) {
      mObjectMapper = new ObjectMapper();
      mObjectMapper
          .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .registerModule(new ParameterNamesModule())
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule());
    }
    return mObjectMapper;
  }

  /**
   * Maps json string to specified class
   *
   * @param json   string to parse
   * @param tClass class of object in which json will be parsed
   * @param <T>    generic parameter for tClass
   * @return mapped T class instance
   * @throws IOException
   */
  public static <T> T entity(String json, Class<T> tClass) {
    try {
      return getMapper().readValue(json, tClass);
    } catch (Exception e) {
      return null;
    }

  }

  public static <T> T entityFromJson(String json, Class<T> tClass) {
    try {
      return getMapper().readValue(json, tClass);
    } catch (Exception e) {
//      log.error(e);
      return null;
    }
  }

  /**
   * Maps json string to {@link ArrayList} of specified class object instances
   *
   * @param json   string to parse
   * @param tClass class of object in which json will be parsed
   * @param <T>    generic parameter for tClass
   * @return mapped T class instance
   * @throws IOException
   */
  public static <T> ArrayList<T> arrayList(String json, Class<T> tClass) {
//    log.debug("==========arrayList: " + json);
    try {
      TypeFactory typeFactory = getMapper().getTypeFactory();
      JavaType type = typeFactory.constructCollectionType(ArrayList.class, tClass);
      return getMapper().readValue(json, type);
    } catch (Exception e) {
//      log.error(" ===> arrayList: ", e);
    }
    return null;
  }

  /**
   * Writes specified object as string
   *
   * @param object object to write
   * @return result json
   * @throws IOException
   */
  public static String toJson(Object object) {
    try {
      return getMapper().writeValueAsString(object);
    } catch (Exception e) {
      return null;
    }
  }

  public static String toJson(List<Object> object) {
    try {
      return getMapper().writeValueAsString(object);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Convert int[] to ArrayList<Integer></>
   *
   * @param ints
   * @return
   */
  public static ArrayList<Integer> intArrayList(int[] ints) {
    return IntStream.of(ints).boxed().collect(Collectors.toCollection(ArrayList::new));
  }

  /**
   * @param object
   * @return
   */
  public static MultiValueMap<String, String> objectToMap(Object object) {
    MultiValueMap parameters = new LinkedMultiValueMap();
    Map<String, String> maps =
        getMapper().convertValue(object, new TypeReference<Map<String, String>>() {
        });
    parameters.setAll(maps);
    return parameters;
  }

  public static String toSnack(String camelCase) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCase);
  }

  /**
   * @param object
   * @return
   */
  public static MultiValueMap<String, String> objectToMapSnackCase(Object object) {
    MultiValueMap parameters = new LinkedMultiValueMap();
    Map<String, String> maps =
        getMapper().convertValue(object, new TypeReference<Map<String, String>>() {
        });

    Map<String, String> mapSnack = new HashMap<>();
    for (Map.Entry<String, String> entry : maps.entrySet()) {
      mapSnack.put(toSnack(entry.getKey()), entry.getValue());
    }
    parameters.setAll(mapSnack);
    return parameters;
  }

  public static void writeValue(OutputStream out, Object value) {
    try {
      getMapper().writeValue(out, value);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> Page<T> toPage(String json, Class<T> tClass) throws IOException {
    ObjectMapper mapper = getMapper();
    JsonNode jsonNode = mapper.readTree(json);
    List<T> content = mapper.convertValue(jsonNode.get("content"), mapper.getTypeFactory().constructCollectionType(List.class, tClass));

    int page = jsonNode.get("number").asInt();
    int size = jsonNode.get("size").asInt();
    long totalElements = jsonNode.get("totalElements").asLong();
    return new PageImpl<>(content, PageRequest.of(page, size), totalElements);
  }
//  public static void main(String[] args) {
//    List<String> value = JsonParser.arrayList(null, String.class);
//    System.out.println(value);
//  }
}
