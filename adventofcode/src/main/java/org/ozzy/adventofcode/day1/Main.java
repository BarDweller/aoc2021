package org.ozzy.adventofcode.day1;

import java.nio.file.Path;
import java.util.List;

import org.ozzy.adventofcode.common.FileReader;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public class Main {

	public static void main(String[] args) throws Exception {
		Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day1/data");				
		List<Integer> values = FileReader.getFileAsListOfInt(input);
		
		long part1 = IntStreamEx.of(values)
				.pairMap((a,b)->(b>a ? b : 0))
				.filter(a -> a>0)
				.count();		
		System.out.println(part1);
		
		long part2 = StreamEx.ofSubLists(values, 3, 1)
				.map(a -> IntStreamEx.of(a).sum())
				.pairMap((a,b)->(b>a ? b : 0))
				.filter(a -> a>0)
				.count();		
		System.out.println(part2);
	}

}
