package org.ozzy.adventofcode.day15;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {

  private static int[][] deltas = {        {0,-1},
                                   {-1, 0},       {1, 0},
                                           {0, 1}       };
  record Coord( int x, int y ) {};
  private static Map<Coord,Integer> grid = new HashMap<>();
  private static Map<Coord,Integer> calcdistance = new HashMap<>();
  private static Map<Coord,LinkedList<Coord>> shortestpaths = new HashMap<>();
  
  public static void doDijkstra( Map<Coord,Integer> grid , Coord source) {
    calcdistance.put(source, 0);
    Set<Coord> fixed = new HashSet<>();
    Set<Coord> todo = new HashSet<>();
    todo.add(source);
    while (todo.size() != 0) {
        Coord current = getCheapest(todo);
        todo.remove(current);
        StreamEx.of(deltas).map(d -> new Coord(current.x+d[0], current.y+d[1])).filter(grid::containsKey).forEach(c -> {
            if (!fixed.contains(c)) {
                calcMinimumDistance(c, grid.get(c), current);
                todo.add(c);
            }
        });
        fixed.add(current);
    }
}
  
  private static Coord getCheapest(Set<Coord> coordset) {
    Coord chosen = null;
    int min = Integer.MAX_VALUE;
    for (Coord c: coordset) {
      int distance = calcdistance.getOrDefault(c, Integer.MAX_VALUE);
      if (distance < min) {
          min = distance;
          chosen = c;
      }
      
    }
    return chosen;
}
  
  private static void calcMinimumDistance(Coord c, int score, Coord source) {
        Integer sourceDistance = calcdistance.getOrDefault(source, Integer.MAX_VALUE);
        if (sourceDistance + score < calcdistance.getOrDefault(c, Integer.MAX_VALUE)) {
            calcdistance.put(c, sourceDistance+score);
            LinkedList<Coord> shortestPath = new LinkedList<>(shortestpaths.getOrDefault(source,new LinkedList<>()));
            shortestPath.add(source);
            shortestpaths.put(c, shortestPath);
        }
    }
  
  private static Coord findBottomRight() {
    Coord result = new Coord(0,0);
    List<Coord> bodge = new ArrayList<>();
    bodge.add(result);
    StreamEx.of(grid.keySet()).forEach(c -> { Coord r = bodge.get(0); if (c.x>r.x || c.y>r.y) bodge.set(0, c); });
    return bodge.get(0);
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day15/data");

    StreamEx.ofLines(input)
    .zipWith(IntStreamEx.ints())
    .forKeyValue((line, lineNo) -> {
              StreamEx.split(line, "")
                      .map(Integer::parseInt)
                      .zipWith(IntStreamEx.ints())
                      .forKeyValue((Integer height, Integer colNo) -> grid.put(new Coord(colNo,lineNo), height));
                 });
    
    //part1.
    doDijkstra(grid, new Coord(0,0));
    
    Coord br = findBottomRight();
    
    System.out.println(shortestpaths.get(br));
    System.out.println(calcdistance.get(br));
    
    //part2.. expand grid.
    int[]nextrisk = {0,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9};
    Map<Coord,Integer> nextgrid = new HashMap<>();
    for(Map.Entry<Coord, Integer> e : grid.entrySet()) {
      Coord c= e.getKey();
      for(int y=0; y<5; y++) {
        for(int x=0; x<5; x++) {
            nextgrid.put(new Coord(c.x+(x*(br.x+1)),c.y+(y*(br.y+1))), nextrisk[e.getValue()+x+y]  );
        }
      }
    }
    //reset 
    calcdistance = new HashMap<>();
    shortestpaths = new HashMap<>();
    grid=nextgrid;
    
    doDijkstra(grid, new Coord(0,0));
    
    br = findBottomRight();
    
    System.out.println(shortestpaths.get(br));
    System.out.println(calcdistance.get(br));
  }

}

