package org.ozzy.adventofcode.day14;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

public class Main {

  private static void doStep(Map<List<String>,AtomicLong> pairCounts, Map<String,AtomicLong> letterCounts, Map<List<String>,String> rules) {
    
    //deep copy the current pair state, else we'll corrupt it during the loop
    Map<List<String>,AtomicLong> pairCountsCopy = new HashMap<>();
    EntryStream.of(pairCounts).forEach(e -> pairCountsCopy.put(e.getKey(), new AtomicLong(e.getValue().get())));

    EntryStream.of(pairCountsCopy).forEach(e -> {
      //how many of these do we know about?
      long count = e.getValue().get();

      //what do we need to convert it into?
      String r = rules.get(e.getKey());

      //add the new letter to the totals, the number of times we'd add it to the string.
      letterCounts.putIfAbsent(r, new AtomicLong(0));
      letterCounts.get(r).addAndGet(count);
      
      //remove existing pair
      pairCounts.get(e.getKey()).addAndGet(-count);
      
      //calc new pairs and add.
      List<String> a = StreamEx.of(e.getKey().get(0), r).toList();
      List<String> b = StreamEx.of(r,e.getKey().get(1)).toList();
      pairCounts.putIfAbsent(a, new AtomicLong(0));
      pairCounts.putIfAbsent(b, new AtomicLong(0));
      pairCounts.get(a).addAndGet(count);
      pairCounts.get(b).addAndGet(count);
      
    });
  }
  
  private static void dumpAnswer(Map<String,AtomicLong> letterCounts) {
    long min=Long.MAX_VALUE;
    long max=0;
    for(AtomicLong l : letterCounts.values()) {
      long t = l.get();
      if(t>max) max = t;
      if(t<min) min = t;
    }
    System.out.println(max-min);
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day14/data");

    String template = StreamEx.ofLines(input).findFirst().get();
    
    //build rules map, using list of string as key
    Map<List<String>,String> rules = new HashMap<>();
    StreamEx.ofLines(input).skip(2).forEach(l -> {String []p = l.split(" -> "); rules.put(StreamEx.split(p[0],"").toList(), p[1]); } );
    
    //build initial total tables.. 
    Map<List<String>,AtomicLong> pairCounts = new HashMap<>();
    Map<String,AtomicLong> letterCounts = new HashMap<>();
    StreamEx.ofSubLists(StreamEx.split(template, "").toList(),2,1).forEach(l -> {
                                                                                  pairCounts.putIfAbsent(l, new AtomicLong(0));
                                                                                  pairCounts.get(l).incrementAndGet();
                                                                                  });
    StreamEx.split(template, "").forEach(l -> {
                                                  letterCounts.putIfAbsent(l, new AtomicLong(0));
                                                  letterCounts.get(l).incrementAndGet();
                                              });
    
    //part one.
    int steps = 10;
    //run loops    
    for(int i=0;i<steps; i++) {
      doStep(pairCounts,letterCounts,rules);
    }
    
    dumpAnswer(letterCounts);
    
    //part two.
    //run another 30
    steps = 30;
    for(int i=0;i<steps; i++) {
      doStep(pairCounts,letterCounts,rules);
    }
    dumpAnswer(letterCounts);
    
  }

}

