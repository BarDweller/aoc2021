package org.ozzy.adventofcode.day22;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.StreamEx;

public class Main {
  private record Cube(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax, boolean on) {
    static Cube fromLine(String line) {
      String d[] = line.split(" ")[1].split(",");
      String x[] = d[0].split("=")[1].split("\\.\\.");
      String y[] = d[1].split("=")[1].split("\\.\\.");
      String z[] = d[2].split("=")[1].split("\\.\\.");
      return new Cube(Integer.parseInt(x[0]), Integer.parseInt(x[1]),
                      Integer.parseInt(y[0]), Integer.parseInt(y[1]),
                      Integer.parseInt(z[0]), Integer.parseInt(z[1]),
                      line.startsWith("on")
          );
    }
    Optional<Cube> intersect(Cube c, boolean newOn) {
      if (xmin > c.xmax || xmax < c.xmin || 
          ymin > c.ymax || ymax < c.ymin || 
          zmin > c.zmax || zmax < c.zmin) return Optional.empty();
      return Optional.of(new Cube(
              Math.max(xmin, c.xmin), Math.min(xmax, c.xmax),
              Math.max(ymin, c.ymin), Math.min(ymax, c.ymax),
              Math.max(zmin, c.zmin), Math.min(zmax, c.zmax), newOn));
    }
    long volume() {
      return (xmax - xmin + 1L) * (ymax - ymin + 1L) * (zmax - zmin + 1L) * (on ? 1 : -1);
    }
    boolean isCubeOkForPartOne() {
      return xmin>=-50 && xmax<=50 && ymax>=-50 && ymax<=50 && zmin>=-50 && zmax<=50;
    }
  };
  
  private static long doSteps(List<Cube> rebootSteps) {
    List<Cube> reactor = new ArrayList<>();
    for (Cube current : rebootSteps) {
        List<Cube> addForCurrent = new ArrayList<>();
        if (current.on) addForCurrent.add(current);
        //find intersections, and add them inverted.
        for (Cube existing : reactor) {
            Optional<Cube> intersection = existing.intersect(current, !existing.on);
            if(intersection.isPresent())
              addForCurrent.add(intersection.get());
        }
        reactor.addAll(addForCurrent);
    }
    return StreamEx.of(reactor).map(Cube::volume).reduce(0L, Long::sum);
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day22/data");
    
    List<Cube> partOneCubes = StreamEx.ofLines(input).map(Cube::fromLine).filter(c -> c.isCubeOkForPartOne()).toList();
    System.out.println(doSteps(partOneCubes));
    
    List<Cube> partTwoCubes = StreamEx.ofLines(input).map(Cube::fromLine).toList();
    System.out.println(doSteps(partTwoCubes));
    
  }

}

