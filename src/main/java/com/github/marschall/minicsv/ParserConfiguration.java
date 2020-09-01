package com.github.marschall.minicsv;

/**
 * Configuration of a {@link CsvParser}.
 */
public final class ParserConfiguration {

  private final char delimiter;
  private final boolean ignoreFirstLine;
  private final char quote;
//  private boolean supportsEscapes;
//  private Charset cs;

  ParserConfiguration(char delimiter, boolean ignoreFirstLine, char quote) {
    this.delimiter = delimiter;
    this.ignoreFirstLine = ignoreFirstLine;
    this.quote = quote;
  }

  char getDelimiter() {
    return this.delimiter;
  }

  boolean isIgnoreFirstLine() {
    return this.ignoreFirstLine;
  }

  char getQuote() {
    return this.quote;
  }

  public static ParserConfigurationBuilder builder() {
    return new ParserConfigurationBuilder();
  }

  public static final class ParserConfigurationBuilder {

    private char delimiter;
    private boolean ignoreFirstLine;
    private char quote;

    ParserConfigurationBuilder() {
      this.delimiter = ',';
    }

    public ParserConfigurationBuilder delimiter(char delimiter) {
      this.delimiter = delimiter;
      return this;
    }

    public ParserConfigurationBuilder quote(char quote) {
      this.quote = quote;
      return this;
    }

    public ParserConfigurationBuilder ignoreFirstLine() {
      this.ignoreFirstLine = true;
      return this;
    }

    public ParserConfigurationBuilder returnFirstLine() {
      this.ignoreFirstLine = false;
      return this;
    }

    public ParserConfiguration build() {
      return new ParserConfiguration(this.delimiter, this.ignoreFirstLine, this.quote);
    }

  }

}
