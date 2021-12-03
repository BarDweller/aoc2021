package org.ozzy.adventofcode.day3;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {

  public static List<String> rotate90(Collection<String> values){
    //rotate the strings through 90'
    List<StringBuffer> totals = StreamEx.of(values)
                                        .foldLeft(new ArrayList<StringBuffer>(), 
                                                  (sums,entry) -> {
                                                      for(int i=0, max=entry.length() ; i<max ; i++) {
                                                        if(sums.size() < i+1)sums.add(new StringBuffer());
                                                        sums.set(i, sums.get(i).append(entry.charAt(i)));
                                                      }
                                                      return sums;
                                                    }
                                                  );
    return StreamEx.of(totals).map(a -> a.toString()).toList();
  }
  
  //return string with most value per bit set as per input collection of bitstrings
  public static String mostCommonBitByIndex(Collection<String> totals) {
    String bits = StreamEx.of(totals)
        .map(a -> a.replace("0","").length() >= a.replace("1","").length() ? "1" : "0")
        .foldLeft(new StringBuffer(),
                  (buf, bit) -> buf.append(bit))
        .toString();
    return bits;
  }

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day3/data");
    List<String> values = FileReader.getFileAsListOfString(input);
    
    List<String> totals = rotate90(values);
    String bits = mostCommonBitByIndex(totals);
    
    int gamma = Integer.parseInt(bits, 2);
    System.out.println("gamma "+gamma);
    bits = bits.replace('0','X').replace('1','0').replace('X','1');
    int epsilon = Integer.parseInt(bits, 2);
    System.out.println("epsilon "+epsilon);
    System.out.println("gamma*epsilon "+(gamma * epsilon));
    
    //part2    
    Set<String> remaining = new HashSet<String>(values);
    int idx=0;
    while(remaining.size()>1) {
      bits = mostCommonBitByIndex(rotate90(remaining));
      Set<String> toRemove=new HashSet<String>();
      for(String s : remaining) {
        if(s.charAt(idx)!=bits.charAt(idx)) toRemove.add(s);
      }
      remaining.removeAll(toRemove);
      if(remaining.size()==1) break;
      idx++;
    }
    int oxy = Integer.parseInt(remaining.iterator().next(),2);
    System.out.println("oxy "+oxy);
    
    remaining = new HashSet<String>(values);
    idx=0;
    while(remaining.size()>1) {
      bits = mostCommonBitByIndex(rotate90(remaining));
      Set<String> toRemove=new HashSet<String>();
      for(String s : remaining) {
        if(s.charAt(idx)==bits.charAt(idx)) toRemove.add(s);
      }
      remaining.removeAll(toRemove);
      if(remaining.size()==1) break;
      idx++;
    }
    int co2 = Integer.parseInt(remaining.iterator().next(),2);
    System.out.println("co2 "+co2);
    
    System.out.println("oxy*co2 "+(oxy*co2));
  }

}
