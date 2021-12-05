package org.ozzy.adventofcode.day5;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {
  
  record Coord( int x, int y ) {};
  record PairCoord( Coord a, Coord b) {};
  
  private static void addToGrid(Map<Coord,Integer> grid, Coord c) {
    grid.put(c,  grid.getOrDefault(c, 0)+1); 
  }
  
  private static void fillLine(Map<Coord,Integer> grid, PairCoord pc) {
      //updated after part2, just use a single generic fill line routine, instead of horiz/vert/diag
      //handles any horiz/vert/diag line(only 45' as per problem)
      int xd= pc.a.x == pc.b.x ? 0 : pc.a.x < pc.b.x ? 1 : -1;
      int yd= pc.a.y == pc.b.y ? 0 : pc.a.y < pc.b.y ? 1 : -1;
      int count = Integer.max(Math.abs(pc.a.x - pc.b.x), Math.abs(pc.a.y-pc.b.y));
      for(int x=pc.a.x,y=pc.a.y,c=0;c<=count;x+=xd,y+=yd,c++) {
        addToGrid(grid, new Coord(x,y));
      }
  }

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day5/data");
    
    //convert input to list of ints.. by parsing out the unwanted chars.
    List<Integer> s = StreamEx.ofLines(input)
                              .flatMap(line -> StreamEx.of(line.split(" -> "))
                                                       .flatMap(coordstr -> StreamEx.of(coordstr.split(",")))
                                       )
                              .map(Integer::parseInt)
                              .toList();

    //process list of ints by pairs, to make coords, then list of coords by pairs to make paircoords.
    List<PairCoord> pcs = StreamEx.ofSubLists(   StreamEx.ofSubLists(s, 2)
                                                         .flatMap(p -> StreamEx.of(p)
                                                                               .pairMap(Coord::new)
                                                                 )
                                                         .toList(),2
                                               )
                                   .flatMap( cp -> StreamEx.of(cp)
                                                            .pairMap(PairCoord::new)
                                           )
                                   .toList();

    //we'll track marked coords in a map.
    Map<Coord,Integer> grid = new HashMap<>();
    
    //identify straight lines & fille them to grid
    List<PairCoord> straightLines = StreamEx.of(pcs).filter(pc -> (pc.a.x==pc.b.x || pc.a.y==pc.b.y)).toList();
    StreamEx.of(straightLines).forEach(pc -> fillLine(grid,pc));
    //count grid locations with more than 2 marks
    long overlapCount = IntStreamEx.of(grid.values()).filter(a -> a>=2).count();
    System.out.println(overlapCount);
    
    //identify diagonals and add them to the grid
    List<PairCoord> diagonalLines = StreamEx.of(pcs).filter(pc -> (Math.abs(pc.a.x - pc.b.x) == Math.abs(pc.a.y - pc.b.y))).toList();
    StreamEx.of(diagonalLines).forEach(pc -> fillLine(grid,pc));
    //count grid location with more than 2 marks
    long overlapCountWithDiagonals = IntStreamEx.of(grid.values()).filter(a -> a>=2).count();
    System.out.println(overlapCountWithDiagonals);
  }
}