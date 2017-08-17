package com.github.marschall.minicsv;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.github.marschall.lineparser.LineParser;

public class CellSetTest {

  @Test
  public void test() throws IOException {
    LineParser parser = new LineParser();
    AtomicInteger lineNumberHolder = new AtomicInteger(0);
    parser.forEach(Paths.get("src/test/resources/test.csv"), StandardCharsets.US_ASCII, line -> {
      int lineNumber = lineNumberHolder.get();
      CellSet cellSet = new CellSet(line, lineNumber, ',');
      switch (lineNumber) {
        case 0: {
          int cellNumber = 0;
          while (cellSet.next()) {
            cellNumber += 1;
          }
          break;
        }

        case 1: {
          int cellNumber = 0;
          while (cellSet.next()) {
            cellNumber += 1;
          }
          break;
        }
        case 2: {
          int cellNumber = 0;
          while (cellSet.next()) {
            cellNumber += 1;
          }
          break;
        }

        default:
          fail("unexpected line number " + lineNumber);
      }
      lineNumberHolder.incrementAndGet();
    });
  }

}
