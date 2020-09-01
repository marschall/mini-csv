package com.github.marschall.minicsv;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class CsvParserQuotesTest {

  @Test
  void quotes() throws IOException {
    ParserConfiguration parserConfiguration = ParserConfiguration.builder()
      .delimiter(',')
      .quote('"')
      .escape('"')
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
            Arrays.asList("", "BANCO INBURSA, S.A.", "11531", "2308640000", "2308640099", "MCS", "MCC - Mastercard Mixed", "MCC", "MEX", "LAC"),
            Arrays.asList("", "CLOSED JOINT STOCK COMPANY \"ALFA-BANK\"", "19011", "5154640200", "5154640299", "MCS", "MCC - Mastercard Mixed", "MCC", "BLR", "EUR"),
            Arrays.asList(",", "\"BANK \"SAINT PETERSBURG\" PUBLIC JOINT-STOCK COMPANY", "10514", "5155240100", "5155240199", "MCS", "MCC - Mastercard Mixed", "MCC", "RUS", "EUR"));
    assertEquals(expected, lines);
  }

}
