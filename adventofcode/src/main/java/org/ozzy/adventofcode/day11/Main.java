package org.ozzy.adventofcode.day11;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {
  
  private static int[][] deltas = {{-1,-1},{0,-1},{1,-1},
                                   {-1, 0},       {1, 0},
                                   {-1, 1},{0, 1},{1, 1}};
  record Coord( int x, int y ) {};
  private static Map<Coord,Integer> energyMap = new HashMap<>();
  
  private static void doFlash(Coord c, Set<Coord> flashed) {
    energyMap.put(c,energyMap.get(c)+1);
    
    if(energyMap.get(c) > 9 && !flashed.contains(c)) {
      flashed.add(c);
      StreamEx.of(deltas)
              .map(d -> new Coord(c.x+d[0], c.y+d[1]))
              .filter(cd -> energyMap.containsKey(cd))
              .remove(cd -> flashed.contains(cd))
              .forEach(cd -> doFlash(cd,flashed));
    }
  }
  
  private static int step() {
    //increase all by 1.
    EntryStream.of(energyMap).forKeyValue((k,v)->energyMap.put(k, v+1));
    
    //do flashes
    Set<Coord> flashed = new HashSet<>();
    EntryStream.of(energyMap).filterValues(v -> v>9).forEach(e -> doFlash(e.getKey(), flashed));
    
    //reset flashed
    StreamEx.of(flashed).forEach(c -> energyMap.put(c, 0));
    
    return flashed.size();
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day11/data");

    StreamEx.ofLines(input)
    .zipWith(IntStreamEx.ints())
    .forKeyValue((line, lineNo) -> {
              StreamEx.split(line, "")
                      .map(Integer::parseInt)
                      .zipWith(IntStreamEx.ints())
                      .forKeyValue((Integer height, Integer colNo) -> energyMap.put(new Coord(colNo,lineNo), height));
                 });
    
    //part one.
    int steps=100;
    int flashed=0;
    for(int i=1; i<=steps; i++) {
      flashed+=step();
    }
    System.out.println(flashed);
    
    //part two (starting from step 100 from part 1, assumption step 2 wasn't within first 100 steps)
    while(true) {
      flashed = step();
      steps++;
      if(flashed==100) break;
    }
    
    System.out.println(steps);
  }

}

