package org.ozzy.adventofcode.day10;

import java.nio.file.Path;
import java.util.List;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  
  private static long scoreLine(String line, boolean partTwo) {
    long rc=0;
    String stack="";
    for(int i=0; i<line.length(); i++) {
      char c = line.charAt(i);
      if(c=='(' || c=='[' || c=='{' || c=='<') {
        switch(c) {
          case '(' : stack=')'+stack; break;
          case '[' : stack=']'+stack; break;
          case '{' : stack='}'+stack; break;
          case '<' : stack='>'+stack; break;
        }
        
      }else {
        if(!stack.isEmpty() && c==stack.charAt(0)) {
          stack = stack.substring(1);
        } else {
          switch(c) {
            case ')' : rc=3; break;
            case ']' : rc=57; break;
            case '}' : rc=1197; break;
            case '>' : rc=25137; break;
          }
          break;
        }
      }      
    }
    
    if(partTwo) {
      //for part2, we want to skip failed lines.
      if(rc!=0) return 0;
      //now score this line based on required close chars.
      rc=0;
      for(int i=0; i<stack.length(); i++) {
        switch(stack.charAt(i)) {
          case ')' : rc=(rc*5)+1; break;
          case ']' : rc=(rc*5)+2; break;
          case '}' : rc=(rc*5)+3; break;
          case '>' : rc=(rc*5)+4; break;
        }
      }
    }
    return rc;
  }
  

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day10/data");

    //part one
    System.out.println(StreamEx.ofLines(input).map(a -> scoreLine(a,false)).reduce(0L,Long::sum));
    
    //part two
    List<Long> results = StreamEx.ofLines(input).map(a -> scoreLine(a,true)).remove(a -> a==0).sorted().toList();
    System.out.println(results.get(results.size()/2));
  }
}

