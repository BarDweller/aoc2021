package org.ozzy.adventofcode.day7;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {
  
  private static BiFunction <List<Integer>, Integer, Integer> score = (s,idx) -> {
    return IntStreamEx.of(s).map(a -> Math.abs(a-idx)).sum();
  };
  
  private static int fuel(int a, int idx) {
    int distance = Math.abs(a-idx);
    return (distance * (distance+1))/2; 
  }
  
  private static BiFunction <List<Integer>, Integer, Integer> score2 = (s,idx) -> {
    return IntStreamEx.of(s).map(a -> fuel(a,idx)).sum();
  };
  
  private static int seekScore(List<Integer> s, BiFunction<List<Integer>, Integer, Integer> scoreFunction) {
    int minidx = Integer.MAX_VALUE;
    int idx=s.size()/2;
    //start at middle and seek forwards..
    while(true) {
      int t = scoreFunction.apply(s,idx);
        if(t<minidx) 
          minidx=t;
        else
          break;
        idx++;
    }
    idx=(s.size()/2)-1;
    //start back at middle and seek backwards..
    while(true) {
      int t = scoreFunction.apply(s,idx);
        if(t<minidx) 
          minidx=t;
        else
          break;
        idx--;
    } 
    return minidx;
  }
    
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day7/data");
    
    List<Integer> s = StreamEx.of(StreamEx.ofLines(input).findFirst().get().split(",")).map(Integer::parseInt).toList();
    s.sort(Integer::compare);
    
    System.out.println(seekScore(s,score));
    System.out.println(seekScore(s,score2));
  }
}
