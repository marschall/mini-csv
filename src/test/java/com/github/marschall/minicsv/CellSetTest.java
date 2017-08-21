package com.github.marschall.minicsv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
          this.validateFirstLine(cellSet);
          break;
        }

        case 1: {
          this.validateSecondLine(cellSet);
          break;
        }
        case 2: {
          this.validateThirdLine(cellSet);
          break;
        }

        default:
          fail("unexpected line number " + lineNumber);
      }
      lineNumberHolder.incrementAndGet();
    });
  }

  private void validateFirstLine(CellSet cellSet) {
    int cellIndex = 0;
    assertEquals(0, cellSet.getLineNumber());
    while (cellSet.next()) {
      switch (cellIndex) {
        case 0:
          assertEquals(0, cellSet.getColumnIndex());
          assertEquals(1, cellSet.getInt());
          assertEquals(1L, cellSet.getLong());
          assertEquals("1", cellSet.getString());
          assertTrue("1".contentEquals(cellSet.getCharSequence()));
          assertFalse(cellSet.isCellEmpty());
          break;
        case 1:
          assertEquals(1, cellSet.getColumnIndex());
          assertEquals("text", cellSet.getString());
          assertTrue("text".contentEquals(cellSet.getCharSequence()));
          assertFalse(cellSet.isCellEmpty());
          break;
        case 2:
          assertEquals(2, cellSet.getColumnIndex());
          assertEquals(22, cellSet.getInt());
          assertEquals(22L, cellSet.getLong());
          assertEquals("22", cellSet.getString());
          assertTrue("22".contentEquals(cellSet.getCharSequence()));
          assertFalse(cellSet.isCellEmpty());
          break;
        case 3:
          assertEquals(3, cellSet.getColumnIndex());
          assertTrue(cellSet.isCellEmpty());
          break;

        default:
          fail("unexpected cell index " + cellIndex);
      }
      cellIndex += 1;
    }
    assertEquals(4, cellIndex);
  }

  private void validateSecondLine(CellSet cellSet) {
    int cellIndex = 0;
    while (cellSet.next()) {
      switch (cellIndex) {
        case 0:
          assertEquals(0, cellSet.getColumnIndex());
          assertEquals(44, cellSet.getInt());
          assertEquals(44L, cellSet.getLong());
          assertEquals("44", cellSet.getString());
          assertTrue("44".contentEquals(cellSet.getCharSequence()));
          assertFalse(cellSet.isCellEmpty());
          break;
        case 1:
          assertEquals(1, cellSet.getColumnIndex());
          assertEquals("text", cellSet.getString());
          assertTrue("text".contentEquals(cellSet.getCharSequence()));
          assertFalse(cellSet.isCellEmpty());
          break;
        case 2:
          assertEquals(2, cellSet.getColumnIndex());
          assertEquals(3, cellSet.getInt());
          assertEquals(3L, cellSet.getLong());
          assertEquals("3", cellSet.getString());
          assertTrue("3".contentEquals(cellSet.getCharSequence()));
          assertFalse(cellSet.isCellEmpty());
          break;

        default:
          fail("unexpected cell index " + cellIndex);
      }
      cellIndex += 1;
    }
    assertEquals(3, cellIndex);
  }

  private void validateThirdLine(CellSet cellSet) {
    int cellIndex = 0;
    while (cellSet.next()) {
      switch (cellIndex) {
        case 0:
          assertEquals(0, cellSet.getColumnIndex());
          assertTrue(cellSet.isCellEmpty());
          break;
        case 1:
          assertEquals(1, cellSet.getColumnIndex());
          assertTrue(cellSet.isCellEmpty());
          break;
        case 2:
          assertEquals(2, cellSet.getColumnIndex());
          assertTrue(cellSet.isCellEmpty());
          break;

        default:
          fail("unexpected cell index " + cellIndex);
      }
      cellIndex += 1;
    }
    assertEquals(3, cellIndex);
  }

}
