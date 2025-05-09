package com.github.marschall.minicsv;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    List<List<String>> expected = List.of(List.of("", "text,quote", "no quote", "", ""));
    assertEquals(expected, lines);
  }

  @Test
  void quoteRegression() throws IOException {
    ParserConfiguration parserConfiguration = ParserConfiguration.builder()
        .ignoreFirstLine()
        .delimiter(',')
        .quote('"')
        .build();
    Path latestCsv = Paths.get("src/test/resources/quote_regression.csv");
    CsvParser parser = new CsvParser(parserConfiguration);
    List<List<String>> lines = new ArrayList<>(1);
    parser.parse(latestCsv, US_ASCII, row -> {
      List<String> cells = new ArrayList<>(4);
      CellSet cellSet = row.getCellSet();

      cellSet.next();
      cells.add(Integer.toString(cellSet.getInt()));

      cellSet.next();
      cells.add(cellSet.getString());

      cellSet.next();
      cells.add(cellSet.getString());

      cellSet.next();
      cells.add(Integer.toString(cellSet.getInt()));

      cellSet.next();
      cells.add(Integer.toString(cellSet.getInt()));

      lines.add(cells);
    });
    List<List<String>> expected = List.of(
        List.of("1", "", "CY", "5819", "1"),
        List.of("3", "Protaras", "CY", "18918", "1"),
        List.of("22894", "Arco da Calheta, Madeira", "PT", "2271598", "1"));
    assertEquals(expected, lines);
  }

}
