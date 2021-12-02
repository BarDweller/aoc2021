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
            .reduce(new Delta(0,0), (total, element) -> new Delta(total.x+element.x, 
                                                                  total.y+element.y));
    System.out.println(result + " " +(result.x*result.y));
    
    //approach 1, map all deltas to part 2 deltas then reduce as per part1
    PartTwoDelta result2 = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .map(a -> new PartTwoDelta(0, a.x, a.y))
            .reduce(new PartTwoDelta(0,0,0), (total, element) -> new PartTwoDelta(total.aim+element.y, 
                                                                                  total.x+element.x, 
                                                                                  total.y+(total.aim*element.x)));
    System.out.println(result2 + " " +(result2.x*result2.y));
    
    //approach2, use the reduce variant that allows for Type change, but requires a combining function for the new Type.
    //           since we're generating the new PartTwoDelta with the previous one already added, our combining function is 
    //           just 'take the new total and use it'. 
    PartTwoDelta result3 = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .reduce(new PartTwoDelta(0,0,0), 
                    (total, element) -> new PartTwoDelta(total.aim+element.y, 
                                                         total.x+element.x, 
                                                         total.y+(total.aim*element.x)),
                    (total, othertotal) -> othertotal);
    
    System.out.println(result3 + " " +(result3.x*result3.y));
    
    
    //using fold instead of reduce.. 
    Delta foldresult1 = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .foldLeft((a,b) -> new Delta(a.x+b.x, a.y+b.y))  //not providing a seed, means the result is optional (if the stream were empty)
            .get();
    System.out.println(foldresult1 + " " +(foldresult1.x*foldresult1.y));
    
    //provide initial seed to avoid that 'get' ;p
    Delta foldresult1b = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .foldLeft(new Delta(0,0),
                      (a,b) -> new Delta(a.x+b.x, a.y+b.y));
    System.out.println(foldresult1b + " " +(foldresult1b.x*foldresult1b.y));
    
    //part2 using fold feels cleaner than using reduce above =)
    PartTwoDelta foldresult2 = 
        StreamEx.of(values)
            .map(a -> Delta.fromString(a))
            .foldLeft(new PartTwoDelta(0,0,0),
                      (PartTwoDelta a, Delta b) -> new PartTwoDelta(a.aim+b.y, a.x+b.x,a.y+(a.aim*b.x)));
    System.out.println(foldresult2 + " " +(foldresult2.x*foldresult2.y));
    
  }

}
