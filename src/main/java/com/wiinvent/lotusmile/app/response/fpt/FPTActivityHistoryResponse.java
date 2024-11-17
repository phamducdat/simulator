package com.wiinvent.lotusmile.app.response.fpt;

import com.wiinvent.lotusmile.domain.exception.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class FPTActivityHistoryResponse extends BaseFPTResponse {

  public List<Data> data;

  public static FPTActivityHistoryResponse fakeData(String fromDate, String toDate, Integer firstResult, Integer maxResults) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    Random random = new Random();

    // Handle null dates by generating random dates
    LocalDate from = fromDate != null ? LocalDate.parse(fromDate, formatter) : generateRandomDate(2020, 2023);
    LocalDate to = toDate != null ? LocalDate.parse(toDate, formatter) : from.plusDays(random.nextInt(30) + 1);  // Ensure `to` is after `from`

    // Handle null firstResult and maxResults
    firstResult = firstResult != null ? firstResult : 0;
    maxResults = maxResults != null ? maxResults : 10;

    long daysBetween = ChronoUnit.DAYS.between(from, to);

    // Number of results should not exceed `maxResults` and must start at `firstResult`
    int availableResults = maxResults;
    int numberOfResults = Math.min(availableResults, maxResults);  // Determine how many results to return

    FPTActivityHistoryResponse response = new FPTActivityHistoryResponse();

    Integer finalFirstResult = firstResult;
    response.setData(IntStream.range(0, numberOfResults)
        .mapToObj(i -> {
          Data entry = new Data();
          entry.setId((long) (finalFirstResult + i));  // Unique ID based on firstResult + i

          // Generate a random date between `fromDate` and `toDate`
          LocalDate randomDate = from.plusDays(random.nextInt((int) daysBetween + 1));
          entry.setActivityDate(randomDate.format(formatter));

          // Fill in other random values
          entry.setStatus(randomStatus());
          entry.setStatusName(randomStatusName());
          long bonusMiles = (random.nextInt(1000) - 500);
          entry.setBonusMiles(bonusMiles);
          if (bonusMiles > 0) {
            entry.setType("Points Transfer+");
            entry.setQualifyingMiles((long) random.nextInt(1000));
          } else {
            entry.setType("Points Transfer-");
            entry.setQualifyingMiles(null);
          }
          entry.setComment("Generated comment #" + (i + 1));
          entry.setSource("System");

          return entry;
        }).toList());

    // Set error code and message as per the fake data
    response.setErrorCode(ErrorCode.FPT.MCAB_200);
    response.setErrorMessage("Fake data");

    return response;
  }

  @Deprecated(forRemoval = true)
  private static LocalDate generateRandomDate(int startYear, int endYear) {
    Random random = new Random();
    int randomYear = startYear + random.nextInt(endYear - startYear + 1);
    int randomDayOfYear = random.nextInt(365) + 1;  // To cover all days in the year
    return LocalDate.ofYearDay(randomYear, randomDayOfYear);
  }

  @Deprecated(forRemoval = true)
  private static String randomStatus() {
    String[] statuses = {"B"};
    return statuses[new Random().nextInt(statuses.length)];
  }

  @Deprecated(forRemoval = true)
  private static String randomStatusName() {
    String[] statusNames = {"Booked"};
    return statusNames[new Random().nextInt(statusNames.length)];
  }

  @Override
  public String getErrorMessageConverted() {
    return this.getErrorCode();
  }

  @lombok.Data
  public static class Data {
    private Long id;
    // yyyy-MM-dd
    private String activityDate;
    private String type;
    private String status;
    private String statusName;
    private Long bonusMiles;
    private Long qualifyingMiles;
    private String comment;
    private String source;
  }
}
