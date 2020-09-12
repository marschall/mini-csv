package com.github.marschall.minicsv;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class CsvParserQuotesTest {
  
  private static final String BANK_SAINT_PETERSBURG = "\"BANK \"SAINT PETERSBURG\" PUBLIC JOINT-STOCK COMPANY";

  @Test
  void quotes() throws IOException {
    ParserConfiguration parserConfiguration = ParserConfiguration.builder()
            .delimiter(',')
            .quote('"')
            .build();
    Path latestCsv = Paths.get("src/test/resources/quotes.csv");
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
    List<List<String>> expected = Arrays.asList(
            Arrays.asList("", "text,quote", "no quote", "", ""),
            Arrays.asList("", "text,quote", "no quote", "", ""));
    assertEquals(expected, lines);
  }

  @Test
  void doubleQuotes() throws IOException {
    ParserConfiguration parserConfiguration = ParserConfiguration.builder()
            .delimiter(',')
            .quote('"')
            .escape('"')
            .build();
    Path latestCsv = Paths.get("src/test/resources/double-quotes.csv");
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
    List<List<String>> expected = Arrays.asList(
            Arrays.asList("", "BANCO INBURSA, S.A.", "11531", "2308640000", "2308640099", "MCS", "MCC - Mastercard Mixed", "MCC", "MEX", "LAC"),
            Arrays.asList("", "CLOSED JOINT STOCK COMPANY \"ALFA-BANK\"", "19011", "5154640200", "5154640299", "MCS", "MCC - Mastercard Mixed", "MCC", "BLR", "EUR"),
            Arrays.asList(",", "\"BANK \"SAINT PETERSBURG\" PUBLIC JOINT-STOCK COMPANY", "10514", "5155240100", "5155240199", "MCS", "MCC - Mastercard Mixed", "MCC", "RUS", "EUR"),
            Collections.singletonList("\"23456789012345678901234567890123456789012345678901234567890123"),
            Collections.singletonList("\"234567890123456789012345678901234567890123456789012345678901234"),
            Collections.singletonList("\"234567890123456789012345678901234567890123456789012345678901235"));
    assertListEquals(expected, lines);
  }
  
  private static void assertListEquals(List<?> l1, List<?> l2) {
    assertEquals(l1.size(), l2.size(), "list size");
    for (int i = 0; i < l1.size(); i++) {
      Object a = l1.get(i);
      Object b = l1.get(i);
      int index = i;
      assertEquals(a, b, () -> "list element: " + index);
    }
  }

  @Test
  void escapedCharSubSequence() throws IOException {
    ParserConfiguration parserConfiguration = ParserConfiguration.builder()
            .delimiter(',')
            .quote('"')
            .escape('"')
            .build();
    Path latestCsv = Paths.get("src/test/resources/double-quotes2.csv");
    CsvParser parser = new CsvParser(parserConfiguration);
    parser.parse(latestCsv, US_ASCII, row -> {
      CellSet cellSet = row.getCellSet();
      assertTrue(cellSet.next());
      CharSequence charSequence = cellSet.getCharSequence();
      assertEquals(BANK_SAINT_PETERSBURG, charSequence.toString());
      assertEquals(BANK_SAINT_PETERSBURG.length(), charSequence.length());
      assertFalse(cellSet.next());
    });
  }

}
