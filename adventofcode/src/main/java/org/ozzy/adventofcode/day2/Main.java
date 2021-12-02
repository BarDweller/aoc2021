package org.ozzy.adventofcode.day2;

import java.nio.file.Path;
import java.util.List;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  
  public record Delta(int x, int y) {
    public static Delta fromString(String combined) {
      String[] parts = combined.split(" ");
      String direction = parts[0];
      int amount = Integer.valueOf(parts[1]);
      int x=0; 
      int y=0;
      switch(direction) {
        case "forward"  : x=amount; break;
        case "backward" : x=-amount; break;
        case "up"       : y=-amount; break;
        case "down"     : y=amount; break;
      }
      return new Delta(x,y);
    } 
  };
  
  public record PartTwoDelta(int aim, int x, int y) {};
 

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day2/test");
    List<String> values = FileReader.getFileAsListOfString(input);
    
    Delta result = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .reduce(new Delta(0,0), (total, element) -> {return new Delta(total.x+element.x, 
                                                                          total.y+element.y);});
    System.out.println(result + " " +(result.x*result.y));
    
    //approach 1, map all deltas to part 2 deltas then reduce as per part1
    PartTwoDelta result2 = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .map(a -> new PartTwoDelta(0, a.x, a.y))
            .reduce(new PartTwoDelta(0,0,0), (total, element) -> {return new PartTwoDelta(total.aim+element.y, 
                                                                                          total.x+element.x, 
                                                                                          total.y+(total.aim*element.x));});
    System.out.println(result2 + " " +(result2.x*result2.y));
    
    //approach2, use the reduce variant that allows for Type change, but requires a combining function for the new Type.
    //           since we're generating the new PartTwoDelta with the previous one already added, our combining function is 
    //           just 'take the new total and use it'. 
    PartTwoDelta result3 = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .reduce(new PartTwoDelta(0,0,0), 
                    (total, element) -> {return new PartTwoDelta(total.aim+element.y, 
                                                                 total.x+element.x, 
                                                                 total.y+(total.aim*element.x));},
                    (total, othertotal) -> othertotal);
    
    System.out.println(result3 + " " +(result2.x*result2.y));
  }

}
