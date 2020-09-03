package com.github.marschall.minicsv;

import com.github.marschall.lineparser.Line;

@FunctionalInterface
interface CellSetFactory {
  
  CellSet newCellSet(Line line, int lineNumber);

}
