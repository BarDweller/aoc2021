package org.ozzy.adventofcode.day9;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {
  
  record Coord( int x, int y ) {};
  private static Map<Coord,Integer> heightMap = new HashMap<>();
  
  private static boolean isLowPoint(Coord c) {
    Coord above = new Coord(c.x,c.y-1);
    Coord below = new Coord(c.x,c.y+1);
    Coord left = new Coord(c.x-1,c.y);
    Coord right = new Coord(c.x+1,c.y);
    Integer current = heightMap.get(c);
    
    return current < heightMap.getOrDefault(above, Integer.MAX_VALUE) &&
           current < heightMap.getOrDefault(below, Integer.MAX_VALUE) &&
           current < heightMap.getOrDefault(left, Integer.MAX_VALUE) &&
           current < heightMap.getOrDefault(right, Integer.MAX_VALUE);
  }
  
  private static Integer risk(Coord c) {
    return heightMap.get(c) + 1;
  }
  
  private static void findBasin(Coord c, Set<Coord> currentBasin) {
    currentBasin.add(c);
    Coord above = new Coord(c.x,c.y-1);
    Coord below = new Coord(c.x,c.y+1);
    Coord left = new Coord(c.x-1,c.y);
    Coord right = new Coord(c.x+1,c.y);    
    //eval above
    if( heightMap.containsKey(above) && !currentBasin.contains(above) && heightMap.get(above)!=9 ) {
      findBasin(above, currentBasin);
    }
    //eval below
    if( heightMap.containsKey(below) && !currentBasin.contains(below) &&  heightMap.get(below)!=9 ) {
      findBasin(below, currentBasin);
    }
    //eval left
    if( heightMap.containsKey(left) && !currentBasin.contains(left) &&  heightMap.get(left)!=9 ) {
      findBasin(left, currentBasin);
    }
    //eval right
    if( heightMap.containsKey(right) && !currentBasin.contains(right) && heightMap.get(right)!=9 ) {
      findBasin(right, currentBasin);
    }
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day9/data");
    

    StreamEx.ofLines(input)
            .zipWith(IntStreamEx.ints())
            .forKeyValue((line, lineNo) -> {
                      StreamEx.split(line, "")
                              .map(Integer::parseInt)
                              .zipWith(IntStreamEx.ints())
                              .forKeyValue((Integer height, Integer colNo) -> heightMap.put(new Coord(colNo,lineNo), height));
                         });
      
    //part one
    System.out.println(StreamEx.ofKeys(heightMap).filter(Main::isLowPoint).map(Main::risk).reduce(0, Integer::sum));

    
    Set<Coord> allKnownBasinCoords = new HashSet<>();
    Set<Set<Coord>> basins = new HashSet<>();
    for(Coord c : heightMap.keySet()) {
      if(heightMap.get(c)==9) continue; //skip the nines =)
      if(!allKnownBasinCoords.contains(c)) { //skip done coords
        Set<Coord> basin = new HashSet<>();
        findBasin(c, basin);
        allKnownBasinCoords.add(c);
        basins.add(basin);
      }
    }
    
    //part 2
    System.out.println( StreamEx.of(basins).map(b -> b.size()).sorted().skip(basins.size() - 3).reduce(1, (a,b) -> a*b) );
  }
}

