package org.ozzy.adventofcode.day4;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {

  public static class Board {
    public List<List<Integer>> rows = new ArrayList<>();
    public List<List<Integer>> columns = new ArrayList<>();
    public Set<Integer> allnumbers = new HashSet<>();
    public LinkedList<Integer> marked = new LinkedList<>();

    public Board(List<String> rowdata) {
      //parse row data into rows
      StreamEx.of(rowdata).skip(1).map( row -> StreamEx.of(row.split(" ")).remove(String::isEmpty).map(Integer::parseInt).toList() ).forEach(rows::add);
      //parse rows into columns
      for(int i=0;i<rows.get(0).size();i++) {columns.add(new ArrayList<>());}
      StreamEx.of(rows).forEach(row -> StreamEx.of(row).zipWith(IntStreamEx.ints()).forKeyValue((rowitem,idx)-> {columns.get(idx).add(rowitem); allnumbers.add(rowitem);}));
    }
    
    public void markNumber(Integer n) {
      marked.add(n);
    }
    
    public boolean winningBoard() {
      return    StreamEx.of(columns).filter(column -> marked.containsAll(column)).count() !=0 
             || StreamEx.of(rows).filter(row -> marked.containsAll(row)).count() !=0;
    }
    
    public int score() {
      allnumbers.removeAll(marked);
      return IntStreamEx.of(allnumbers).sum() * marked.getLast();
    }
  }
  
  private static void findWinner(List<Integer> nos, List<Board> boards) {
    for(int no : nos) {
      for(Board b : boards) {
        b.markNumber(no);
        if(b.winningBoard()) {
          System.out.println(b.score());
          return;
        }
      }
    }
  }
  
  private static void findLastWinner(List<Integer> nos, List<Board> boards) {
    for(int no : nos) {
      List<Board> toRemove = new ArrayList<>();
      for(Board b : boards) {
        b.markNumber(no);
        if(b.winningBoard()) {
          if(boards.size()>1) {
            toRemove.add(b);
          }else {
            System.out.println(b.score());
            return;
          }
        }
      }
      boards.removeAll(toRemove);
    }
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day4/data");
    List<String> values = FileReader.getFileAsListOfString(input);
    
    //read in numbers
    List<Integer> drawnNos = StreamEx.of(values.remove(0).split(",")).map(Integer::parseInt).toList();

    //read in boards
    List<Board> boards = StreamEx.ofSubLists(values, 6).map(Board::new).toList();
    
    //part1
    findWinner(drawnNos,boards);
    
    //part2
    //reset boards
    boards = StreamEx.ofSubLists(values, 6).map(Board::new).toList();
    findLastWinner(drawnNos,boards);
  }

}

