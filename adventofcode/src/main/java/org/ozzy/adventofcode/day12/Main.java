package org.ozzy.adventofcode.day12;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  
  public static void findPath(Map<String,List<String>> caveMap, Set<String> visitedSmallCaves, Set<LinkedList<String>> paths, LinkedList<String> currentPath, String currentNode){
    
    if(currentNode.equals("end")) {
      LinkedList<String> copyOfCurrentPath = new LinkedList<>(currentPath);
      copyOfCurrentPath.add("end");
      paths.add(copyOfCurrentPath);
      return;
    }
    
    currentPath.addLast(currentNode);
    
    Set<String> options = new HashSet<>(caveMap.get(currentNode));
    options.removeAll(visitedSmallCaves);
    
    for(String o : options) {
      if(o.toLowerCase().equals(o)){visitedSmallCaves.add(o);}
      findPath(caveMap,visitedSmallCaves,paths,currentPath,o);
      if(o.toLowerCase().equals(o)){visitedSmallCaves.remove(o);}
    }
    
    currentPath.removeLast();
  }
  
  public static void findPath2(Map<String,List<String>> caveMap, Set<String> visitedOnceSmallCaves, Set<String> visitedTwiceSmallCave, Set<LinkedList<String>> paths, LinkedList<String> currentPath, String currentNode){
    
    if(currentNode.equals("end")) {
      LinkedList<String> copyOfCurrentPath = new LinkedList<>(currentPath);
      copyOfCurrentPath.add("end");
      paths.add(copyOfCurrentPath);
      return;
    }
    
    currentPath.addLast(currentNode);
    
    Set<String> options = new HashSet<>(caveMap.get(currentNode));
    options.removeAll(visitedTwiceSmallCave);
    options.remove("start");
    
    for(String o : options) {
      boolean once=false;
      if(o.toLowerCase().equals(o)){
        if(visitedOnceSmallCaves.contains(o)) {
          if(!visitedTwiceSmallCave.isEmpty()) {
            continue;
          }else {
            visitedTwiceSmallCave.add(o);
          }
        }else {
          once=true;
          visitedOnceSmallCaves.add(o);
        }
      }
      findPath2(caveMap,visitedOnceSmallCaves,visitedTwiceSmallCave,paths,currentPath,o);
      if(o.toLowerCase().equals(o)){
        if(once)
          visitedOnceSmallCaves.remove(o);
        else
          visitedTwiceSmallCave.remove(o);
      }
    }
    
    currentPath.removeLast();
  }

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day12/data");

    Map<String,List<String>> caveMap = new HashMap<>();
    StreamEx.ofLines(input).forEach(line -> { String p[]=line.split("-"); caveMap.putIfAbsent(p[0], new ArrayList<>()); caveMap.get(p[0]).add(p[1]);} );
    
    //add in the reverse paths
    Set<String> allKeys = new HashSet<>(caveMap.keySet());
    for(String k : allKeys) {
        for(String s : caveMap.get(k)) {
          caveMap.putIfAbsent(s, new ArrayList<>());
          caveMap.get(s).add(k);
        }
    }
    
    Set<LinkedList<String>> paths = new HashSet<>();
    LinkedList<String> currentPath = new LinkedList<>();
    Set<String> visitedSmallCaves = new HashSet<>();
    visitedSmallCaves.add("start");
    findPath(caveMap,visitedSmallCaves,paths,currentPath,"start");
    
    System.out.println(paths.size());
    
    Set<LinkedList<String>> paths2 = new HashSet<>();
    LinkedList<String> currentPath2 = new LinkedList<>();
    Set<String> visitedTwiceSmallCave = new HashSet<>();
    Set<String> visitedOnceSmallCaves = new HashSet<>();
    visitedSmallCaves.add("start");
    findPath2(caveMap,visitedOnceSmallCaves,visitedTwiceSmallCave,paths2,currentPath2,"start");
    
    System.out.println(paths2.size());
  }

}

