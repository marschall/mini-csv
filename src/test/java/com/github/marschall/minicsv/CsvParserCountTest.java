package com.github.marschall.minicsv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class CsvParserCountTest {

  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();
    String fileName;
    if (args.length == 0) {
      fileName = "/home/marschall/git/mini-csv/ss10pusa.csv";
    } else {
      fileName = args[0];
    }
    Path path = Paths.get(fileName);
    CsvParser parser = new CsvParser(',');
    Accumulator count = new Accumulator();
    parser.parse(path, StandardCharsets.ISO_8859_1, row -> {
      CellSet cellSet = row.getCellSet();
      while (cellSet.next()) {
        count.increment();
      }
    });

    long duration = System.currentTimeMillis() - start;
    if (duration < 1000L) {
      System.out.println(duration + " msec for " + count.getCount() + " cells");
    } else {
      System.out.println(TimeUnit.MILLISECONDS.toSeconds(duration) + " sec for " + count.getCount() + " cells");
    }
  }

  static final class Accumulator {

    private long count;

    void increment() {
      this.count += 1L;
    }

    long getCount() {
      return this.count;
    }

  }

}
