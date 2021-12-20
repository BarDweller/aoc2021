package org.ozzy.adventofcode.day20;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {

  private static int[][] deltas = {{-1,-1},{0,-1},{1,-1},
                                   {-1, 0},{0, 0},{1, 0},
                                   {-1, 1},{0, 1},{1, 1}};
  record Coord( int x, int y ) {};
  private static Set<Coord> image = new HashSet<>();
  
  public static boolean beyondEdge(Coord c, int xmin, int xmax, int ymin, int ymax) {
    return c.x<xmin || c.x>xmax || c.y<ymin || c.y>ymax;
  }
  
  public static boolean newPixelFromCoordIsLit(int x, int y, int xmin, int xmax, int ymin, int ymax, int iter, String algo) {
    String background = algo.charAt(0)=='.'?"0":(iter%2==0)?"0":"1";
    String base2 = StreamEx.of(deltas).map(d -> new Coord(x+d[0], y+d[1])).map(c -> beyondEdge(c,xmin,xmax,ymin,ymax)?background:(image.contains(c)?"1":"0")).reduce("", (a,b)->a+b);
    return algo.charAt(Integer.parseInt(base2,2))=='#';
  }

  public static void doStep(String algo, int iter) {
    AtomicInteger xmin = new AtomicInteger(Integer.MAX_VALUE), xmax= new AtomicInteger(0), ymin= new AtomicInteger(Integer.MAX_VALUE), ymax = new AtomicInteger(0);
    StreamEx.of(image).forEach(coord -> { if(coord.x < xmin.get())xmin.set(coord.x);
                                          if(coord.x > xmax.get())xmax.set(coord.x);
                                          if(coord.y < ymin.get())ymin.set(coord.y);
                                          if(coord.y > ymax.get())ymax.set(coord.y);
                                        });

    Set<Coord> output = new HashSet<>();
    for(int y=ymin.get()-1; y<=(ymax.get()+1); y++) {
      for(int x=xmin.get()-1; x<=(xmax.get()+1); x++) {
        if(newPixelFromCoordIsLit(x,y,xmin.get(),xmax.get(),ymin.get(),ymax.get(),iter, algo)) 
          output.add(new Coord(x,y));
      }
    }
    
    image=output;
  }
  
  public static void dumpGrid() {
    AtomicInteger xmin = new AtomicInteger(Integer.MAX_VALUE), xmax= new AtomicInteger(0), ymin= new AtomicInteger(Integer.MAX_VALUE), ymax = new AtomicInteger(0);
    StreamEx.of(image).forEach(coord -> { if(coord.x < xmin.get())xmin.set(coord.x);
                                          if(coord.x > xmax.get())xmax.set(coord.x);
                                          if(coord.y < ymin.get())ymin.set(coord.y);
                                          if(coord.y > ymax.get())ymax.set(coord.y);
                                        });
    for(int y=ymin.get()-1; y<=(ymax.get()+1); y++) {
      for(int x=xmin.get()-1; x<=(xmax.get()+1); x++) {
        if(image.contains(new Coord(x,y)))
          System.out.print("#");
        else
          System.out.print(".");
      }
      System.out.println("");
    }
    System.out.println("");
  }

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day20/data");
    
    String algo = StreamEx.ofLines(input).findFirst().get();
    if(algo.length()!=512) throw new IllegalStateException();
    
    StreamEx.ofLines(input)
    .skip(2)
    .zipWith(IntStreamEx.ints())
    .forKeyValue((line, lineNo) -> {
              StreamEx.split(line, "")
                      .zipWith(IntStreamEx.ints())
                      .filterKeys(pixel -> pixel.equals("#"))
                      .forKeyValue((String pixel, Integer colNo) -> image.add(new Coord(colNo,lineNo)));
                 });
    
    for(int i=0; i<2; i++) {
      doStep(algo, i);
      //dumpGrid();
    }
    
    System.out.println(image.size());
    
    for(int i=2; i<50; i++) {
      doStep(algo,i);
    }
    
    System.out.println(image.size());
  }

}

