package org.ozzy.adventofcode.day16;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

public class Main {
  
  private static class Packet {
    int version;
    int id;
    int bitcount;
    long data;
    List<Packet> subpackets;
  }
  
  private static Map<String,String> hexToBinStr = EntryStream.of("0","0000","1","0001","2","0010","3","0011","4","0100","5","0101","6","0110","7","0111")
                                          .append(EntryStream.of("8","1000","9","1001","A","1010","B","1011","C","1100","D","1101","E","1110","F","1111")).toMap();
  
  private static List<String> hex;
  private static String binary;
  
  private static String readByte() {
    return hexToBinStr.get(hex.remove(0));
  }
   
  private static Packet decodePacket() {
    Packet current = new Packet();
    
    if(binary.length()<6) {
      binary+=readByte();
      binary+=readByte();
    }
    
    current.version = Integer.parseInt(binary.substring(0, 3), 2);
    current.id =Integer.parseInt(binary.substring(3, 6), 2);
    binary=binary.substring(6);
    current.bitcount=6;

    if(current.id==4) {
        StringBuffer databits = new StringBuffer();
        //literal packet
        while(true) {
          while(binary.length()<5) {
            binary+=readByte();
          }
          databits.append(binary.substring(1,5));
          current.bitcount+=5;
          if(binary.startsWith("0")) {
            current.data = Long.parseLong(databits.toString(),2);
            binary=binary.substring(5);
            break;
          }else {
            binary=binary.substring(5);
          }
        }
      } else {
        //operator packet
        current.subpackets = new ArrayList<>();
        boolean fixedLen = binary.charAt(0) == '0';
        binary=binary.substring(1);
        current.bitcount+=1;
        
        if(fixedLen) {
          while(binary.length()<15) {
            binary+=readByte();
          }
          int bitlen = Integer.parseInt(binary.substring(0,15),2);
          binary = binary.substring(15);
          current.bitcount+=15;
          
          while(bitlen>0) {
            Packet p = decodePacket();
            current.subpackets.add(p);
            current.bitcount+=p.bitcount;
            bitlen-=p.bitcount;
          }
        }else {
          while(binary.length()<11) {
            binary+=readByte();
          }
          int packetcount = Integer.parseInt(binary.substring(0,11),2);
          binary = binary.substring(11);
          current.bitcount+=11;

          for(int i=0;i<packetcount;i++) {
            Packet p = decodePacket();
            current.subpackets.add(p);
            current.bitcount+=p.bitcount;
          }
        }
      }

    return current;
  }
  
  private static void dumpPacket(Packet p, String indent) {
    System.out.println(indent+"Packet:: ver:"+p.version+" "+(p.id==4?"val:"+p.data : "operator("+p.id+") "));
    if(p.id!=4) {
      for(Packet sp : p.subpackets) {
        dumpPacket(sp, indent+"  ");
      }
    }
  }
  
  private static int versionSum(Packet p) {
    if(p.id==4) return p.version;
    return StreamEx.of(p.subpackets).map(Main::versionSum).reduce(0, Integer::sum) + p.version;
  }
  
  private static long value(Packet p) {
    switch(p.id) {
      case 0 : return StreamEx.of(p.subpackets).map(Main::value).reduce(0L, Long::sum);
      case 1 : return StreamEx.of(p.subpackets).map(Main::value).reduce(1L, (a,b) -> a=a*b);
      case 2 : return StreamEx.of(p.subpackets).map(Main::value).min(Long::compare).get();
      case 3 : return StreamEx.of(p.subpackets).map(Main::value).max(Long::compare).get();
      case 4 : return p.data;
      case 5 : return value(p.subpackets.get(0)) > value(p.subpackets.get(1)) ? 1 : 0;
      case 6 : return value(p.subpackets.get(0)) < value(p.subpackets.get(1)) ? 1 : 0;
      case 7 : return value(p.subpackets.get(0)) == value(p.subpackets.get(1)) ? 1 : 0;
    }
    throw new IllegalStateException();
  }
  
  
  public static void main(String[] args) throws Exception {
    Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day16/data");

    hex = StreamEx.split(StreamEx.ofLines(input).findFirst().get(),"").toList();
    binary=""; 

    Packet p = decodePacket();
    dumpPacket(p," ");
    System.out.println(versionSum(p));
    System.out.println(value(p));
  }

}

