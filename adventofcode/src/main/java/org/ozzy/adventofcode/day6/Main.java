package org.ozzy.adventofcode.day6;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
    
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day6/data");
    
    List<Integer> s = StreamEx.of(StreamEx.ofLines(input).findFirst().get().split(",")).map(Integer::parseInt).toList();
    
    //create list with atomic longs with value 0 in positions 0-8
    LinkedList<AtomicLong> fish = new LinkedList<AtomicLong>();
    for(int i=0; i<9; i++) { fish.add(new AtomicLong(0)); }
    
    //update totals for each day
    StreamEx.of(s).forEach(d -> fish.get(d).incrementAndGet());
    
    int days=256; //set to 80 for part 1.
    for(int day=0; day<days; day++) {
      //add the 0 day fish to day 7 (about to become day 6)
      fish.get(7).addAndGet(fish.get(0).longValue());
      //make day 0 fish become day 8, and move all other fish down a day.
      fish.addLast(fish.removeFirst());
    }
    
    long result = StreamEx.of(fish).map(ai -> ai.longValue()).reduce(0L, (a,b) -> a+b);
    System.out.print(result);

  }
}
