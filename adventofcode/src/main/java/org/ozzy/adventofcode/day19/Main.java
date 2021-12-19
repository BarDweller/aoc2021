package org.ozzy.adventofcode.day19;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {
  
  record ScannerWithOffset( Scanner scanner, Coord offset ) {};
  record Coord( int x, int y, int z) {
    Coord(String s){
      this(parseStringForm(s));
    }
    Coord(List<Integer> xyz){
      this(xyz.get(0), xyz.get(1), xyz.get(2));
    }
    Coord(Coord a, Coord b){
      this(a.x+b.x,a.y+b.y,a.z+b.z);
    }
    int manhattan(Coord dest) {
      return Math.abs(dest.x-x) + Math.abs(dest.y-y) + Math.abs(dest.z-z);
    }
    private static List<Integer> parseStringForm(String s) {
      return StreamEx.split(s, ",").map(Integer::parseInt).toList();
    }
  };
  
  public static class Scanner {
    List<Coord> coords = new ArrayList<>();
    public void addCoord(Coord c) {
      if(!coords.contains(c))
        coords.add(c);
    }
    public Optional<ScannerWithOffset> checkForMatch(DirectionalScanner ds) {
      return StreamEx.of(ds.variants).filter(s -> compatible(s).isPresent()).findFirst().map(s -> new ScannerWithOffset(s, compatible(s).get()));
    }
    private Optional<Coord> compatible(Scanner o) {
      return StreamEx.of(coords)
                        .cross(o.coords)
                        .parallel()
                        .map(e->new Coord(e.getValue().x-e.getKey().x,e.getValue().y-e.getKey().y,e.getValue().z-e.getKey().z))
                        .filter(delta -> StreamEx.of(coords)
                                                 .cross(o.coords)
                                                 .filter( t -> (delta.x+t.getKey().x)==t.getValue().x &&
                                                               (delta.y+t.getKey().y)==t.getValue().y &&
                                                               (delta.z+t.getKey().z)==t.getValue().z )
                                                 .count() >=12
                        ).findFirst();
    }
  }
  

  
  public static class DirectionalScanner {
    final List<Scanner> variants;
    public DirectionalScanner(Scanner s) {
      variants = IntStreamEx.range(24).mapToObj( i -> { Scanner n = new Scanner(); s.coords.stream().map(c -> mutate(c,i)).forEach(c -> n.addCoord(c)); return n; }).toList();
    }
    private static Coord mutate(Coord o, int variant) {
      return switch(variant) {
        case 0 -> new Coord(o.x,o.y,o.z);
        case 1 -> new Coord(-o.y,o.x,o.z);
        case 2 -> new Coord(-o.x,-o.y,o.z);
        case 3 -> new Coord(o.y,-o.x,o.z);
        case 4 -> new Coord(o.x,-o.y,-o.z);
        case 5 -> new Coord(o.y,o.x,-o.z);
        case 6 -> new Coord(-o.x,o.y,-o.z);
        case 7 -> new Coord(-o.y,-o.x,-o.z);
        case 8 -> new Coord(o.x,-o.z,o.y);
        case 9 -> new Coord(o.z,o.x,o.y);
        case 10 ->new Coord(-o.x,o.z,o.y);
        case 11 ->new Coord(-o.z,-o.x,o.y);
        case 12 ->new Coord(-o.y,-o.z,o.x);
        case 13 ->new Coord(o.z,-o.y,o.x);
        case 14 ->new Coord(o.y,o.z,o.x);
        case 15 ->new Coord(-o.z,o.y,o.x);
        case 16 ->new Coord(-o.x,-o.z,-o.y);
        case 17 ->new Coord(o.z,-o.x,-o.y);
        case 18 ->new Coord(o.x,o.z,-o.y);
        case 19 ->new Coord(-o.z,o.x,-o.y);
        case 20 ->new Coord(o.y,-o.z,-o.x);
        case 21 ->new Coord(o.z,o.y,-o.x);
        case 22 ->new Coord(-o.y,o.z,-o.x);
        case 23 ->new Coord(-o.z,-o.y,-o.x);
        default -> throw new IllegalStateException();
      };
    }
  }
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day19/data");
        
    //read in all scanners
    LinkedList<Scanner> scanners = new LinkedList<>();
    StreamEx.ofLines(input).forEach(line -> {
      if(!line.isBlank())
      if(line.startsWith("---")) { scanners.addLast(new Scanner()); }
      else {scanners.getLast().addCoord(new Coord(line)); }
    });
    
    //generate all permutations for each scanner
    List<DirectionalScanner> ds = StreamEx.of(scanners).map(DirectionalScanner::new).toList();
    
    //find matches between permutations
    ScannerWithOffset []selectedScannersWithOffsets = new ScannerWithOffset[ds.size()];
    //start by matching against 1st scanner, with zero delta.
    selectedScannersWithOffsets[0] = new ScannerWithOffset(scanners.get(0), new Coord(0,0,0));
    LinkedList<Integer> todo = new LinkedList<>();
    todo.add(0);
    //iteratively lock in orientations of scanners & offsets until we have them all. 
    while (!todo.isEmpty()) {
      Integer current = todo.removeFirst();
      IntStreamEx.ofIndices(ds).forEach(i -> {
                                              if (selectedScannersWithOffsets[i] == null) {
                                                Optional<ScannerWithOffset> match = selectedScannersWithOffsets[current].scanner.checkForMatch(ds.get(i));
                                                if (match.isPresent()) {
                                                    selectedScannersWithOffsets[i] = new ScannerWithOffset(match.get().scanner, 
                                                                                                           new Coord(selectedScannersWithOffsets[current].offset, match.get().offset));
                                                    todo.addLast(i);
                                                }
                                              }
      });
    }
    
    //add all coords from all matched scanners now we know their true offsets (invert the delta, so when it's added to the coords, it normalizes them to zero scanner space)
    Scanner fixed = new Scanner();
    StreamEx.of(selectedScannersWithOffsets)
            .map(swo -> new ScannerWithOffset(swo.scanner, new Coord(-swo.offset.x, -swo.offset.y, -swo.offset.z)))
            .forEach( swo -> swo.scanner.coords.stream()
                                               .map(sc -> new Coord(sc,swo.offset))
                                               .forEach(fixed::addCoord) 
                       );
    System.out.println(fixed.coords.size());
  
    int max = StreamEx.ofCombinations(ds.size(), 2)
                       .map( i ->  selectedScannersWithOffsets[i[0]].offset.manhattan(selectedScannersWithOffsets[i[1]].offset) )
                       .max(Integer::compareTo)
                       .get();
    System.out.println(max);    
  }

}

