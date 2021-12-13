package org.ozzy.adventofcode.day13;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {

  record Coord(int x, int y) {};

  private static Set<Coord> paper = new HashSet<>();
  private static LinkedList<Coord> folds = new LinkedList<>();
  
  private static void doFold(Coord fold) {
    Set<Coord> toFold = StreamEx.of(paper).filter( c -> (fold.x!=0 && c.x>fold.x) || (fold.y!=0 && c.y>fold.y)).toSet();
    paper.removeAll(toFold);
    paper.addAll(StreamEx.of(toFold).map(c -> fold.x!=0 ? new Coord(fold.x-(c.x-fold.x),c.y) : new Coord(c.x,fold.y-(c.y-fold.y))).toSet());
  }
  
  private static void dumpGrid() {
    int maxx=0;
    int maxy=0;
    for(Coord c : paper) {
      if(c.x>maxx) maxx=c.x;
      if(c.y>maxy) maxy=c.y;
    }
    for(int y=0;y<=maxy;y++) {
      for(int x=0;x<=maxx;x++) {
        if(paper.contains(new Coord(x,y))) System.out.print('#'); else System.out.print(' ');
      }
      System.out.println("");
    }
    System.out.println("");
  }

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day13/data");

    StreamEx.ofLines(input).forEach(line -> {
      if (line.startsWith("fold")) {
        String p[] = line.split(" ")[2].split("=");
        if(p[0].equals("y"))
          folds.add(new Coord(0, Integer.valueOf(p[1])));
        else
          folds.add(new Coord(Integer.parseInt(p[1]), 0));
      } else {
        String p[] = line.split(",");
        paper.add(new Coord(Integer.parseInt(p[0]), Integer.parseInt(p[1])));
      }
    });

    doFold(folds.remove(0));
    System.out.println(paper.size());
    
    StreamEx.of(folds).forEach(f -> doFold(f));
    dumpGrid();
  }

}
