package org.ozzy.adventofcode.day18;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  
  private static class Pair {
    Pair leftNest;
    Pair rightNest;
    Integer value;
    Pair parent = null;
    
    public Pair(String s) {
      this(StreamEx.split(s, "").toList(),null);
    }
    
    public Pair(Integer value, Pair enclosing) {
      this.value=value;
      this.parent=enclosing;
    }
    
    public Pair(List<String> s, Pair enclosing) {
      this.parent = enclosing;
      if(!s.get(0).equals("[")) throw new IllegalStateException();
      s.remove(0); //remove "["
      if(s.get(1).equals(",")) {
        leftNest = new Pair(s.get(0).charAt(0)-'0',this);
        s.remove(0); //remove number
        s.remove(0); //remove comma
        if(s.get(1).equals("]")) {
          rightNest = new Pair(s.get(0).charAt(0)-'0',this);
          s.remove(0); //remove number
        }else {
          rightNest = new Pair(s, this);
        }
      }else {
        leftNest = new Pair(s, this);
        s.remove(0); //remove comma
        if(s.get(1).equals("]")) {
          rightNest = new Pair(s.get(0).charAt(0)-'0',this);
          s.remove(0); //remove number
        }else {
          rightNest = new Pair(s, this);
        }
      }
      s.remove(0); //remove "]"
    }
    
    public String toString() {
      if(value!=null) return value.toString();
      return "[" +leftNest.toString() + "," + rightNest.toString() + "]";
    }
    
    public void reduce() {
      boolean workToDo = true;
      while(workToDo) {
        Optional<Pair> lm4d = getLeftMostMoreThanFourDeep();
        if(lm4d.isPresent()) {
          workToDo = lm4d.get().explode();
        }else {
          workToDo = false;
        }
        if(!workToDo) {
          workToDo = split();
        }
      }
    }
    
    
    public int magnitude() {
      if (value!=null) return value;
      return 3 * leftNest.magnitude() + 2 * rightNest.magnitude();
    }
    
    public Pair add(Pair p) {
      Pair r =  new Pair("["+this.toString()+","+p.toString()+"]");
      r.reduce();
      return r;
    }
    
    private boolean isAtLeastFourDeep() {
      return value==null && parent !=null && parent.parent != null && parent.parent.parent !=null && parent.parent.parent.parent !=null;
    }
    
    private Optional<Pair> getLeftMostMoreThanFourDeep() {
        return StreamEx.of(getFlatPairList()).filter(Pair::isAtLeastFourDeep).findFirst();
    }
    
    private List<Pair> getFlatPairList(){
      List<Pair> result = new ArrayList<>();
      result.add(this);
      if(leftNest!=null) result.addAll(leftNest.getFlatPairList());
      if(rightNest!=null) result.addAll(rightNest.getFlatPairList());
      return result;
    }
    
    private Pair nearestValue(Pair number, int delta) {
      Pair root = parent.parent.parent.parent;
      List<Pair> values = StreamEx.of(root.getFlatPairList()).filter(p -> p.value!=null).toList();
      int idx = values.indexOf(number) + delta;
      if(idx<0 || idx>=values.size()) 
        return null;
      else
        return values.get(idx);
    }
    
    private boolean explode() {
      //find the left & right pairs in order using the object identity of the numbers in this pair
      Pair nearestLeft = nearestValue(this.leftNest,-1);
      Pair nearestRight = nearestValue(this.rightNest,+1);
      
      //if there was a number, bump it.
      if (nearestLeft != null) nearestLeft.value += leftNest.value;
      if (nearestRight != null) nearestRight.value += rightNest.value;
      
      //replace this node with a 0 value.
      leftNest = null; 
      rightNest = null; 
      value = 0;
      return true;
    }
    
    private boolean split() {
      if (value!=null && value >=10) {
        int splitVal = value / 2;
        leftNest = new Pair(splitVal,this);
        rightNest = new Pair(splitVal + value % 2,this);
        value = null;
        return true;
      } else {
        if(value==null) {
          if (leftNest.split()) return true;
          if (rightNest.split()) return true;
        }
      }
      return false;
    }
  }
  

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day18/data");
    
    System.out.println(StreamEx.ofLines(input).map(Pair::new).reduce((a,b)->a.add(b)).get().magnitude());
        
    List<Pair> allNumbers = StreamEx.ofLines(input).map(Pair::new).toList();
    int max = StreamEx.ofCombinations(allNumbers.size(), 2)
                      .map( i -> Math.max(allNumbers.get(i[0]).add(allNumbers.get(i[1])).magnitude(),
                                          allNumbers.get(i[1]).add(allNumbers.get(i[0])).magnitude()) )
                      .max(Integer::compareTo)
                      .get();
    System.out.println(max);

  }

}

