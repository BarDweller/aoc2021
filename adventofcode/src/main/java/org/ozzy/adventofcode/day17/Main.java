package org.ozzy.adventofcode.day17;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  
  record Target( int xmin, int xmax, int ymin, int ymax ) {};
  
  record Probe( int x, int y, int xv, int yv, int maxy, int origx, int origy) {};
  
  private static Probe doStep(Probe p) {
    return new Probe(p.x+p.xv, p.y+p.yv, Math.max(p.xv-1,0), p.yv-1, Math.max(p.y+p.yv, p.maxy),p.origx,p.origy);
  }
  
  private static boolean outOfBounds(Probe p, Target t) {
    return p.y<t.ymin || p.x>t.xmax;
  }
  
  private static boolean onTarget(Probe p, Target t) {
    return p.y>=t.ymin && p.y<=t.ymax && p.x>=t.xmin && p.x<=t.xmax;
  }

  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day17/data");

    String line = FileReader.getFileAsListOfString(input).get(0);
    String d = line.split("\\:")[1];
    String txy[] = d.split(",");
    String xs[] = txy[0].split("=")[1].split("\\.\\.");
    String ys[] = txy[1].split("=")[1].split("\\.\\.");
    
    Target t = new Target(Integer.parseInt(xs[0]),Integer.parseInt(xs[1]),Integer.parseInt(ys[0]),Integer.parseInt(ys[1]));
    
    //init probes 
    List<Probe> remaining = new ArrayList<>();
    for(int y=t.ymin;y<=Math.abs(t.ymin); y++) {
      for(int x=1; x<=t.xmax; x++) {
        remaining.add(new Probe(0,0,x,y,0,x,y));
      }
    }
    
    //fire away!
    List<Probe> onTarget = new ArrayList<>();
    while(!remaining.isEmpty()) {
      List<Probe >arrived = StreamEx.of(remaining).parallel().filter(p -> onTarget(p,t)).toList();  
      onTarget.addAll(arrived);
      remaining.removeAll(arrived);
      remaining = StreamEx.of(remaining).parallel().map(Main::doStep).remove(p -> outOfBounds(p,t)).toList();
    }
    
    System.out.println(StreamEx.of(onTarget).map(p -> p.maxy).max(Integer::compareTo).get());
    System.out.println(onTarget.size());
  }

}

