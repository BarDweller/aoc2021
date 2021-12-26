package org.ozzy.adventofcode.day25;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {

  record Coord(int x, int y) {};
  record AtCoord<T>(Coord coord, T obj) {};

  private static Map<Coord,Character> move(Map<Coord,Character> map, char wanted, Function<Coord,Coord> newcoord) {
    return EntryStream.of(map)
                      .parallel()
                      .flatMapKeyValue( (c,w) -> StreamEx.of(c)
                                                         .map(newcoord)
                                                         .map(nc -> !w.equals(wanted)||map.containsKey(nc)?new AtCoord<Character>(c,w):new AtCoord<Character>(nc,w))
                                      )
                      .toMap(c -> c.coord, c->c.obj);
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day25/data");

    //all this, just to read in a grid of chars, keep the wanted ones, and store them in a map by x,y coord
    Map<Coord, Character> trench =  StreamEx.ofLines(input)
                                            .zipWith(IntStreamEx.ints())
                                            .flatMapKeyValue((String line, Integer lineNo) -> 
                                                                            StreamEx.split(line, "")
                                                                                    .map(s -> s.charAt(0))
                                                                                    .map(Character::valueOf)
                                                                                    .zipWith(IntStreamEx.ints())
                                                                                    .filterKeys(c -> c == '>' || c == 'v')
                                                                                    .mapKeyValue((Character cucumber, Integer colNo) -> new AtCoord<Character>(new Coord(colNo,lineNo),cucumber))
                                                             )
                                            .toMap(c -> c.coord, c->c.obj);

    //because we didn't just use nested for loops, now we have to go back to find the extents.
    int width = StreamEx.ofLines(input).findFirst().get().length();
    int height = (int) StreamEx.ofLines(input).count();
    
    //define fn's that give the 'next' position for a direction
    Function<Coord, Coord> newh = (c) -> new Coord((c.x + 1) % width, c.y);
    Function<Coord, Coord> newv = (c) -> new Coord(c.x, (c.y + 1) % height);

    int count=1;
    while(true) {
      Map<Coord,Character> after = move(move(trench,'>',newh),'v',newv);
      if(trench.equals(after)) {
        System.out.println(count);
        break;
      }
      trench=after;
      count++;
    }
    
  }

}
