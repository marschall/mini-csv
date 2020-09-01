package com.github.marschall.minicsv;

/**
 * Configuration of a {@link CsvParser}.
 */
public final class ParserConfiguration {

  private final char delimiter;
  private final boolean ignoreFirstLine;
  private final char quote;
  private final char escape;
//  private Charset cs;

  ParserConfiguration(char delimiter, boolean ignoreFirstLine, char quote, char escape) {
    this.delimiter = delimiter;
    this.ignoreFirstLine = ignoreFirstLine;
    this.quote = quote;
    this.escape = escape;
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

  char getEscape() {
    return this.escape;
  }

  public static ParserConfigurationBuilder builder() {
    return new ParserConfigurationBuilder();
  }

  public static final class ParserConfigurationBuilder {

    private char delimiter;
    private boolean ignoreFirstLine;
    private char quote;
    private char escape;

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

    public ParserConfigurationBuilder escape(char escape) {
      this.escape = escape;
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
      return new ParserConfiguration(this.delimiter, this.ignoreFirstLine, this.quote, this.escape);
    }

  }

}
