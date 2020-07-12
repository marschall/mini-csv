package com.github.marschall.minicsv;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class CsvParserQuotesTest {

  @Test
  void quotes() throws IOException {
    ParserConfiguration parserConfiguration = ParserConfiguration.builder()
      .delimiter(',')
      .quote('"')
      .build();
    Path latestCsv = Paths.get("src/test/resources/qoutes.csv");
    CsvParser parser = new CsvParser(parserConfiguration);
    List<List<String>> lines = new ArrayList<>(1);
    parser.parse(latestCsv, US_ASCII, row -> {
      List<String> cells = new ArrayList<>(4);
      CellSet cellSet = row.getCellSet();
      while (cellSet.next()) {
        cells.add(cellSet.getString());
      }
      lines.add(cells);
    });
    List<List<String>> expected = Collections.singletonList(Arrays.asList("", "text,quote", "no quote", "", ""));
    assertEquals(expected, lines);
  }

}
