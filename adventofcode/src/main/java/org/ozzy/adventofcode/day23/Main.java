package org.ozzy.adventofcode.day23;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;

public class Main {
  
  private static Map<Character, Integer> costs = Map.of('A',1,'B',10,'C',100,'D',1000);
  private static Map<Character, Integer> targetMap = Map.of('A',2,'B',4,'C',6,'D',8);
  private static Set<Integer> targetIndices = new HashSet<>(targetMap.values());
  private static record BurrowAndScore(List<String> burrow, long score) {};

  private static boolean isAllowed(List<String> burrow, int startSlot, int destinationSlot) {
    for(int idx=Math.min(startSlot, destinationSlot); idx<=Math.max(startSlot, destinationSlot); idx++) {
      if(idx==startSlot || targetIndices.contains(idx))continue;
      if(!burrow.get(idx).equals("."))return false;
    }
    return true;
  }
  
  private static boolean isRoomOk(List<String> burrow, char piece, int destinationSlot) {
    return burrow.get(destinationSlot).replaceAll(""+piece+"|\\.", "").isEmpty();
  }
  
  private static Optional<Character> getFirstLetterInRoom(String room) {
    return room.chars().filter(c -> c!='.').findFirst().stream().mapToObj(c -> Character.valueOf((char)c)).findFirst();
  }
  
  private static List<Integer> getPossibleMoves(List<String> burrow, int startSlot) {
    if(!targetIndices.contains(startSlot)) {
      char piece = burrow.get(startSlot).charAt(0);
      int targetForpiece = targetMap.get(piece);
      if(isAllowed(burrow, startSlot, targetForpiece ) && isRoomOk(burrow, piece, targetForpiece)){
        return Collections.singletonList(targetForpiece);
      }else {
        return Collections.emptyList();
      }
    }else {
      char movingPiece = getFirstLetterInRoom(burrow.get(startSlot)).get();
      if(startSlot == targetMap.get(movingPiece) && isRoomOk(burrow, movingPiece, startSlot)){
        return Collections.emptyList();
      }
      
      return IntStreamEx.ofIndices(burrow)
                 .remove(idx -> idx==startSlot)
                 .remove(idx -> targetIndices.contains(idx) && idx!=targetMap.get(movingPiece))
                 .remove(idx -> idx == targetMap.get(movingPiece) && !isRoomOk(burrow, movingPiece, idx))
                 .filter(idx -> isAllowed(burrow, startSlot, idx))
                 .mapToObj(Integer::valueOf)
                 .toList();
    }
  }

  private static BurrowAndScore move(List<String>burrow, int startSlot, int destinationSlot) {
    List<String> newBurrow = new ArrayList<>(burrow);
    AtomicInteger distanceTravelled = new AtomicInteger(0);
    char movingPiece = getFirstLetterInRoom(burrow.get(startSlot)).get();
    if(burrow.get(startSlot).length()==1) {
      newBurrow.set(startSlot, ".");
    }else {
      StringBuffer newRoom = new StringBuffer();
      AtomicBoolean found=new AtomicBoolean(false);
      String old_room=burrow.get(startSlot);
      old_room.chars().forEach(i -> {
        char c = (char)i;
        if(c=='.') {
          distanceTravelled.incrementAndGet();
          newRoom.append(c);
        } else if(!found.get()) {
          distanceTravelled.incrementAndGet();
          newRoom.append('.');
          found.set(true);
        } else {
          newRoom.append(c);
        }
      });
      newBurrow.set(startSlot, newRoom.toString());
    }
    distanceTravelled.addAndGet(Math.abs(startSlot-destinationSlot));
    
    //if target isn't a room, update is simple, otherwise addToRoom, and update distance.
    if(burrow.get(destinationSlot).length()==1) {
      newBurrow.set(destinationSlot, ""+movingPiece);
    }else {
      newBurrow.set(destinationSlot,burrow.get(destinationSlot).replaceFirst("\\.",""+movingPiece));
      //distance into room is fn of how many empty spaces were in room
      distanceTravelled.addAndGet( burrow.get(destinationSlot).length() - burrow.get(destinationSlot).replaceAll("\\.", "").length());
    }
    
    return new BurrowAndScore(newBurrow, distanceTravelled.get() * costs.get(movingPiece));
  }
  
  private static  Map<List<String>, Long> solve(List<String> burrow){
    Map<List<String>, Long> states = new HashMap<>();
    Deque<List<String>> queue = new ArrayDeque<>();
    states.put(burrow,0L);
    queue.add(burrow);
    while(!queue.isEmpty()) {
      List<String> currentBurrow = queue.pop();
      EntryStream.of(currentBurrow).forKeyValue((idx,piece) -> {
        if(!getFirstLetterInRoom(piece).isEmpty()) {
          for(int dest: getPossibleMoves(currentBurrow, idx)) {
            BurrowAndScore proposed = move(currentBurrow, idx, dest);
            long proposedcost = states.get(currentBurrow) + proposed.score;
            //keep the cheapest route to a given state
            if(proposedcost<states.getOrDefault(proposed.burrow, Long.MAX_VALUE)) {
              states.put(proposed.burrow, proposedcost);
              queue.add(proposed.burrow);
            }
          }
        }
      });
    }
    return states;
  }

  
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day23/data");
    List<String> map = FileReader.getFileAsListOfString(input);
    
    int width = map.get(1).length()-2;
    int rooms = (map.get(3).trim().length()-1) / 2;
    int edge = (width-((rooms*2)-1))/2;
    
    //part1.
    List<String> burrow = new ArrayList<>();
    for(int i=0;i<edge;i++) burrow.add(".");
    for(int i=0;i<rooms;i++) { burrow.add(""+map.get(2).charAt(1+edge+1+(i*2)-1) + map.get(3).charAt(1+edge+1+(i*2)-1)); burrow.add("."); }
    burrow.add(".");
    
    System.out.println(burrow);
    Map<List<String>, Long> states = solve(burrow);
    System.out.println(states.get(List.of(".", ".", "AA", ".", "BB", ".", "CC", ".", "DD", ".", ".")));
    
    //part 2, inject extra chars
    burrow.set(2, ""+burrow.get(2).charAt(0)+"DD"+burrow.get(2).charAt(1));
    burrow.set(4, ""+burrow.get(4).charAt(0)+"CB"+burrow.get(4).charAt(1));
    burrow.set(6, ""+burrow.get(6).charAt(0)+"BA"+burrow.get(6).charAt(1));
    burrow.set(8, ""+burrow.get(8).charAt(0)+"AC"+burrow.get(8).charAt(1));
    
    System.out.println(burrow);
    states = solve(burrow);
    System.out.println(states.get(List.of(".", ".", "AAAA", ".", "BBBB", ".", "CCCC", ".", "DDDD", ".", ".")));

  }

}
