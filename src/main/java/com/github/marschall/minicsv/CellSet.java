package com.github.marschall.minicsv;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.github.marschall.charsequences.CharSequences;
import com.github.marschall.lineparser.Line;

/**
 * A set of cells in a row, can be iterated over much like a
 * {@link java.sql.ResultSet}.
 *
 * <p>It is an error to hold on to a {@link CellSet} for longer than
 * the row callback invoked by
 * {@link com.github.marschall.minicsv.CsvParser#parse(Path, Charset, Consumer)}.
 * </p>
 *
 * @see <a href="http://blog.joda.org/2015/09/naming-optional-query-methods.html">Naming Optional query methods</a>
 */
public final class CellSet {

  // TODO quotes
  // TODO BigDecimal pattern
  // TODO default values for empty int and long

  private final CharSequence charSequence;

  private final char delimiter;
  private final char quote;

  private int nextStart;
  private int nextEnd;
  private int columnIndex;

  private final int lineNumber;
  private boolean start;

  CellSet(Line line, int lineNumber, char delimiter, char quote) {
    this.lineNumber = lineNumber;
    this.quote = quote;
    this.charSequence = line.getContent();
    this.delimiter = delimiter;

    this.start = true;
    this.columnIndex = -1; // first time scanning will increment to 0
  }

  CellSet(Line line, int lineNumber, char delimiter) {
    this(line, lineNumber, delimiter, (char) 0);
  }

  private int findEnd() {
    if (this.hasQuote() && this.isCellQuoted()) {
      return this.findEndQuoted();
    } else {
      return this.findEndUnquoted();
    }
  }

  private boolean hasQuote() {
    return this.quote != 0;
  }

  private boolean isCellQuoted() {
    return (this.nextStart > 0) && (this.charSequence.charAt(this.nextStart - 1) == this.quote);
  }

  private int findEndUnquoted() {
    int fromIndex = this.nextStart;
    int length = this.charSequence.length();
    if (fromIndex > length) {
      return length;
    }
    int end = CharSequences.indexOf(this.charSequence, this.delimiter, fromIndex);
    if (end == -1) {
      return length;
    } else {
      return end;
    }
  }

  private int findEndQuoted() {
    int fromIndex = this.nextStart;
    int length = this.charSequence.length();
    if (fromIndex > length) {
      return length;
    }
    int end = CharSequences.indexOf(this.charSequence, this.quote, fromIndex);
    if (end == -1) {
      return length;
    } else {
      return end;
    }
  }

  public boolean next() {
    if (this.nextStart ==  -1) {
      return false;
    }

    // keep code paths as similar as possible in order to reduce method
    // size after inlining
    if (this.start) {
      this.nextStart = this.findFirstStart();
      this.start = false;
    } else {
      if (this.hasQuote()) {
        this.nextStart = this.findNextStartQuoted();
      } else {
        this.nextStart = this.nextEnd + 1; // skip the delimiter
      }
    }

    if (this.nextStart > this.charSequence.length()) {
      // end is reached
      this.nextStart = -1;
      return false;
    } else {
      this.nextEnd = this.findEnd();
      this.columnIndex += 1;
      return true;
    }

  }

  private int findNextStartQuoted() {
    int start = this.nextEnd + 1; // skip the delimiter
    if (this.isCellQuoted()) {
      start += 1; // skip the quote
    }
    if ((this.charSequence.length() > start) && (this.charSequence.charAt(start) == this.quote)) {
      start += 1; // skip the next quote
    }
    return start;
  }

  private int findFirstStart() {
    if (!this.hasQuote()) {
      return 0;
    } else {
      if ((this.charSequence.length() > 0) && (this.charSequence.charAt(0) == this.quote)) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  public void whileHasNext(Consumer<CellSet> consumer) {
    while (this.next()) {
      consumer.accept(this);
    }
  }

  public void ifNotEmpty(Consumer<CellSet> consumer) {
    if (!this.isCellEmpty()) {
      consumer.accept(this);
    }
  }

  public int getColumnIndex() {
    return this.columnIndex;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  /**
   * Returns the current cell as a {@link CharSequence}.
   *
   * <p>No assumption about the implementation class of the returned
   * {@link CharSequence} can be made.</p>
   *
   * <p>It is an error to hold on to a {@link CellSet} for longer than
   * the row callback invoked by
   * {@link com.github.marschall.minicsv.CsvParser#parse(Path, Charset, Consumer)}.
   * </p>
   *
   * @return the current cell as a {@link CharSequence}
   */
  public CharSequence getCharSequence() {
    return this.charSequence.subSequence(this.nextStart, this.nextEnd);
  }

  public boolean isCellEmpty() {
    return (this.nextEnd - this.nextStart) == 0;
  }

  /**
   * Parses the current cell as an {@code int}.
   *
   * @implNote does not allocate
   * @return the current cell as an {@code int}
   * @throws NumberFormatException if the current cell does not contain a parsable int
   */
  public int getInt() {
    return CharSequences.parseInt(this.charSequence, this.nextStart, this.nextEnd);
  }

  public int getIntOrDefault(int defaultValue) {
    if (this.isCellEmpty()) {
      return defaultValue;
    }
    try {
      return CharSequences.parseInt(this.charSequence, this.nextStart, this.nextEnd);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public Integer getInteger() {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return CharSequences.parseInt(this.charSequence, this.nextStart, this.nextEnd);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public Optional<Integer> findInteger() {
    if (this.isCellEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(CharSequences.parseInt(this.charSequence, this.nextStart, this.nextEnd));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /**
   * Parses the current cell as an {@code long}.
   *
   * @implNote does not allocate
   * @return the current cell as an {@code long}
   * @throws NumberFormatException if the current cell does not contain a parsable long
   */
  public long getLong() {
    return CharSequences.parseLong(this.charSequence, this.nextStart, this.nextEnd);
  }

  public String getString() {
    return this.getCharSequence().toString();
  }

  public LocalDate getLocalDate(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(this.getCharSequence(), formatter);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  public Optional<LocalDate> findLocalDate(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(LocalDate.parse(this.getCharSequence(), formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public LocalTime getLocalTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return LocalTime.parse(this.getCharSequence(), formatter);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  public Optional<LocalTime> findLocalTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(LocalTime.parse(this.getCharSequence(), formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public LocalDateTime getLocalDateTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return LocalDateTime.parse(this.getCharSequence(), formatter);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  public Optional<LocalDateTime> findLocalDateTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(LocalDateTime.parse(this.getCharSequence(), formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public ZonedDateTime getZonedDateTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return ZonedDateTime.parse(this.getCharSequence(), formatter);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  public Optional<ZonedDateTime> findZonedDateTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(ZonedDateTime.parse(this.getCharSequence(), formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public OffsetDateTime getOffsetDateTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return OffsetDateTime.parse(this.getCharSequence(), formatter);
    } catch (DateTimeParseException e) {
      // TODO catch or throw exception
      return null;
    }
  }

  public Optional<OffsetDateTime> findOffsetDateTime(DateTimeFormatter formatter) {
    if (this.isCellEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(OffsetDateTime.parse(this.getCharSequence(), formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public BigDecimal getBigDecimal() {
    if (this.isCellEmpty()) {
      return null;
    }
    // TODO catch or throw exception
    return new BigDecimal(this.getString());
  }

  public BigDecimal getBigDecimal(NumberFormat format) throws ParseException {
    if (this.isCellEmpty()) {
      return null;
    }
    // TODO catch or throw exception
    return (BigDecimal) format.parse(this.getString());
  }

  public UUID getUuid() {
    if (this.isCellEmpty()) {
      return null;
    }
    try {
      return CharSequences.uuidFromCharSequence(this.getCharSequence());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

}
