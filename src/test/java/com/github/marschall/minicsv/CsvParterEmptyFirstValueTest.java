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

class CsvParterEmptyFirstValueTest {

  @Test
  void ignoreFirstLine() throws IOException {
    Path latestCsv = Paths.get("src/test/resources/empty_first_value.csv");
    CsvParser parser = new CsvParser(',');
    List<List<String>> lines = new ArrayList<>(1);
    parser.parse(latestCsv, US_ASCII, row -> {
      List<String> cells = new ArrayList<>(3);
      CellSet cellSet = row.getCellSet();
      while (cellSet.next()) {
        cells.add(cellSet.getString());
      }
      lines.add(cells);
    });
    List<List<String>> expected = Collections.singletonList(Arrays.asList("", "value1", "value2"));
    assertEquals(expected, lines);
  }

}
