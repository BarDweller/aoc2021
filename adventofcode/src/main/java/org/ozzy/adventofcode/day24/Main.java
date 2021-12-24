package org.ozzy.adventofcode.day24;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {
  
  record Pair(int a, int b) {};
  
  public static void main(String args[]) throws Exception{
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day24/data");
    List<String> lines = StreamEx.ofLines(input).toList();
    
    //extract x&y values from each block of code.
    List<Pair> pairs = IntStreamEx.range(14).mapToObj(i -> new Pair(Integer.parseInt(lines.get(i*18+5).split(" ")[2]),
                                                                    Integer.parseInt(lines.get(i*18+15).split(" ")[2]))).toList();
    
    //build linked constraints
    Deque<Pair> stack = new ArrayDeque<>();
    Map<Integer,Pair> links = new HashMap<>();
    EntryStream.of(pairs).forKeyValue((idx,pair)->{ 
                  if(pair.a>0) {
                    stack.push(new Pair(idx,pair.b));
                  }else{
                    Pair p = stack.pop(); 
                    links.put(idx, new Pair(p.a,p.b+pair.a));
                  }
    });
    
    //resolve the digits according to constraints
    Map<Integer,Integer> min = new HashMap<>();
    Map<Integer,Integer> max = new HashMap<>();
    EntryStream.of(links).forKeyValue((idx,pair) -> {
      min.put(idx,Math.min(9, 9 + pair.b));
      min.put(pair.a,Math.min(9, 9 - pair.b));
      max.put(idx,Math.max(1, 1 + pair.b));
      max.put(pair.a,Math.max(1, 1 - pair.b));
    });
    
    //concat the digits back for output.
    System.out.println("Part1: "+IntStreamEx.range(13).mapToObj(i -> ""+min.get(i)).joining());
    System.out.println("Part2: "+IntStreamEx.range(13).mapToObj(i -> ""+max.get(i)).joining());

  }
}

