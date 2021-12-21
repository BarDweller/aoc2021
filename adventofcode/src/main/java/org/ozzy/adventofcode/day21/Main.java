package org.ozzy.adventofcode.day21;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;


public class Main {
  
  private record PlayerPositionAndScore(int position, int score) {};
  
  private static void doPartOne(List<PlayerPositionAndScore> state) {
    int rollCount = 0;
    while(true) {
      int distance = (3 * (rollCount%10))+6; 
      int pidx = rollCount%state.size();
      rollCount+=3;
      PlayerPositionAndScore current = state.get(pidx);
      int destSpace = ((current.position + distance -1)%10)+1;
      if(current.score+destSpace<1000) {
        PlayerPositionAndScore updated = new PlayerPositionAndScore(destSpace, current.score+destSpace);
        state.set(pidx,updated);
      }else {
        int loseridx = pidx-1<0?state.size()-1:pidx-1;
        System.out.println(state.get(loseridx).score * rollCount);
        break;
      }
    }
  }
  
  public static final int[] multipliers = { 1, 3, 6, 7, 6, 3, 1 }; //rolls that overlap. (eg, 233,323,332 are all same)

  private record State(int playerOnePos, int playerOneScore, int playerTwoPos, int playerTwoScore) {
    public State update(int pidx, int distance) {
      if (pidx == 0) {
        int destSpace = ((playerOnePos + distance -1)%10)+1;
        return new State(destSpace, playerOneScore + destSpace, playerTwoPos, playerTwoScore);
      } else {
        int destSpace = ((playerTwoPos + distance -1)%10)+1;
        return new State(playerOnePos, playerOneScore, destSpace, playerTwoScore + destSpace);
      }
    }
    public boolean gameOver() {
      return playerOneScore >= 21 || playerTwoScore >= 21;
    }
  }
  
  private static void doPartTwo(List<Integer> startPositions) {  
    Map<State, Long> todo = new HashMap<>();
    State start = new State(startPositions.get(0), 0, startPositions.get(1), 0);
    todo.put(start, 1L);

    List<AtomicLong> wins = StreamEx.of(startPositions).map(p -> new AtomicLong(0L)).toList();
    AtomicInteger player = new AtomicInteger(0);
    while (!todo.isEmpty()) {
      Map<State, Long> next = new HashMap<>();
      EntryStream.of(todo).forKeyValue((s, score) -> {
        for (int i = 0; i < 7; i++) {
          State n = s.update(player.get(), i + 3);
          if (n.gameOver()) {
            wins.get(player.get()).addAndGet(multipliers[i] * score);
          } else {
            next.merge(n, multipliers[i] * score, Long::sum);
          }
        }
      });

      if (!(player.addAndGet(1) < startPositions.size()))
        player.set(0);
      todo = next;
    }

    System.out.println(Math.max(wins.get(0).get(), wins.get(1).get()));
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day21/data");
    List<Integer> startPositions = StreamEx.ofLines(input).map(line -> Integer.parseInt(line.split(" ")[4])).toList();
    
    List<PlayerPositionAndScore> state = new ArrayList<>();
    state.add(new PlayerPositionAndScore(startPositions.get(0), 0));
    state.add(new PlayerPositionAndScore(startPositions.get(1), 0));
    
    doPartOne(state);

    doPartTwo(startPositions);
  }

}

