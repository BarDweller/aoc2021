package org.ozzy.adventofcode.day25;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {

  record Coord(int x, int y) {};
  record DirectionalMove(Function<Coord,Coord> movefn, Character target) {};

  private static Coord move(Map<Coord,Character> m, DirectionalMove d, Coord in) {
    if(m.get(in) != d.target) return in;
    Coord n = d.movefn.apply(in);
    return m.containsKey(n) ? in : n;
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day25/data");

    Map<Coord, Character> trench = new HashMap<>();
    StreamEx.ofLines(input).zipWith(IntStreamEx.ints()).forKeyValue((line, lineNo) -> {
      StreamEx.split(line, "").map(s -> s.charAt(0)).map(Character::valueOf).zipWith(IntStreamEx.ints())
          .filterKeys(c -> c == '>' || c == 'v')
          .forKeyValue((Character cucumber, Integer colNo) -> trench.put(new Coord(colNo, lineNo), cucumber));
    });
    
    int width = StreamEx.ofLines(input).findFirst().get().length();
    int height = (int) StreamEx.ofLines(input).count();
    
    Function<Coord, Coord> newh = (c) -> new Coord((c.x + 1) % width, c.y);
    Function<Coord, Coord> newv = (c) -> new Coord(c.x, (c.y + 1) % height);
    
    DirectionalMove h = new DirectionalMove(newh, '>');
    DirectionalMove v = new DirectionalMove(newv, 'v');

    int count=1;
    while(true) {
      Map<Coord,Character> afterHoriz = EntryStream.of(trench)
                                                   .mapKeys( c -> move(trench,h,c))
                                                   .toMap();
      Map<Coord,Character> afterVert = EntryStream.of(afterHoriz)
                                                  .mapKeys( c -> move(afterHoriz,v,c))
                                                  .toMap();
            
      if(trench.equals(afterVert)) {
        System.out.println(count);
        break;
      }
      trench.clear();
      trench.putAll(afterVert);
      count++;
    }
  }

}
