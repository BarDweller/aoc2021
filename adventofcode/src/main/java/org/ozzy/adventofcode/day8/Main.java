package org.ozzy.adventofcode.day8;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  
  private static Set<String> getSetForLength(List<String> allGroupings, int len){
    return StreamEx.of(allGroupings)
                   .filter(group -> group.length()==len)
                   .findFirst()
                   .map( group -> StreamEx.split(group, "").toSet())
                   .get();
  }
  
  private static int solveDisplay(List<List<String>> line) {
    //use all data to derive values. 
    List<String> allGroupings = StreamEx.of(line).flatMap(pair -> StreamEx.of(pair)).toList();

    Map<Set<String>, Integer> digitMap = new HashMap<>();
    
    //find the easy digits
    Set<String> one = getSetForLength(allGroupings, 2);
    digitMap.put(one, 1);
    Set<String> seven = getSetForLength(allGroupings, 3);
    digitMap.put(seven, 7);
    Set<String> four = getSetForLength(allGroupings, 4);
    digitMap.put(four, 4);
    Set<String> eight = getSetForLength(allGroupings, 7);
    digitMap.put(eight, 8);
    
    //any groupings of 5 that contain 7 must be 3's
    Set<String> three = StreamEx.of(allGroupings)
        .filter(group -> group.length()==5)
        .map( group -> StreamEx.split(group, "").toSet())
        .filter(group -> group.containsAll(seven))
        .findFirst()
        .get();
    digitMap.put(three, 3);
    
    //any groupings of 5 that are not 3's are either 2's or 5, if we subtract 4, we can split them apart.
    Set<String> two = StreamEx.of(allGroupings)
        .filter(group -> group.length()==5)
        .map( group -> StreamEx.split(group, "").toSet())
        .filter(group -> !group.equals(three))
        .filter(group -> { Set<String> t = new HashSet<>(); t.addAll(group); t.removeAll(four); return t.size()==3; } )
        .findFirst()
        .get();
    digitMap.put(two, 2);
    
    Set<String> five = StreamEx.of(allGroupings)
        .filter(group -> group.length()==5)
        .map( group -> StreamEx.split(group, "").toSet())
        .filter(group -> !group.equals(three))
        .filter(group -> { Set<String> t = new HashSet<>(); t.addAll(group); t.removeAll(four); return t.size()==2; } )
        .findFirst()
        .get();    
    digitMap.put(five, 5);
    
    // any groupings of 6 that contain 3's must be 9's
    Set<String> nine = StreamEx.of(allGroupings)
        .filter(group -> group.length()==6)
        .map( group -> StreamEx.split(group, "").toSet())
        .filter(group -> group.containsAll(three))
        .findFirst()
        .get();
    digitMap.put(nine, 9);
    
    // any groupings of 6 that contain 7's must be 0's
    Set<String> zero = StreamEx.of(allGroupings)
        .filter(group -> group.length()==6)
        .map( group -> StreamEx.split(group, "").toSet())
        .filter(group -> group.containsAll(seven) && !group.containsAll(three))
        .findFirst()
        .get();
    digitMap.put(zero, 0);
    
    //any groupings of 6 that are not 9's or 0's must be 6's
    Set<String> six = StreamEx.of(allGroupings)
        .filter(group -> group.length()==6)
        .map( group -> StreamEx.split(group, "").toSet())
        .filter(group -> !group.containsAll(nine) && !group.containsAll(zero))
        .findFirst()
        .get();
    digitMap.put(six, 6);
    
    return StreamEx.of(line.get(1))
                   .map( group -> StreamEx.split(group, "").toSet())
                   .map( digitMap::get )
                   .reduce(0, (sum,n) -> (sum*10)+n);
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day8/test");
    
    List<List<List<String>>> data = StreamEx.ofLines(input)
          .map(s -> StreamEx.of(s.split("( \\| )")).toList())
          .map(s -> StreamEx.of(s).map(p -> StreamEx.of(p.split(" ")).toList()).toList())
          .toList(); 

    //part 1
    System.out.println( StreamEx.of(data)
                           .flatMap(pair -> 
                                    StreamEx.of(pair)
                                            .skip(1) //only counting the output values =)
                                            .flatMap(groups -> 
                                                     StreamEx.of(groups)
                                                             .map(group -> group.length() )
                                                             .filter(size -> (size==2 || size==3 || size==4 || size==7 ))
                                                     )
                                    )
                           .count()
    );

    //part 2
    System.out.println(StreamEx.of(data).map(Main::solveDisplay).reduce(Integer::sum).get());
    
  }
}
