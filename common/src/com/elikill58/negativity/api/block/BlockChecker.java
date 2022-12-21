package com.elikill58.negativity.api.block;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.item.Material;

public class BlockChecker {

	private final List<Block> blocks;

	public BlockChecker(List<Block> blocks) {
		this.blocks = blocks;
	}
	
	public BlockChecker(Block... blocks) {
		this.blocks = Arrays.asList(blocks);
	}
	
	public BlockChecker(Block blocks) {
		this.blocks = Arrays.asList(blocks);
	}
	
	public List<Block> getBlocks() {
		return blocks;
	}

	public boolean has(Predicate<Material> checker) {
		for(Block b : blocks)
			if(checker.test(b.getType()))
				return true;
		return false;
	}

	public boolean has(Material... types) {
		return has(Arrays.asList(types).stream().map(Material::getId).collect(Collectors.toList()).toArray(new String[] {}));
	}
	
	public boolean has(String... types) {
		List<String> m = Arrays.asList(types).stream().map(String::toUpperCase).collect(Collectors.toList());
		for(Block b : blocks) {
			String blockId = b.getType().getId().toUpperCase();
			if(m.contains(blockId))
				return true;
			for(String all : m)
				if(blockId.contains(all))
					return true;
		}
		return false;
	}

	public boolean hasOther(Material... types) {
		return has(Arrays.asList(types).stream().map(Material::getId).collect(Collectors.toList()).toArray(new String[] {}));
	}
	
	public boolean hasOther(String... types) {
		List<String> m = Arrays.asList(types).stream().map(String::toUpperCase).collect(Collectors.toList());
		for(Block b : blocks) {
			String blockId = b.getType().getId().toUpperCase();
			if(!m.contains(blockId))
				return true;
			for(String all : m)
				if(!blockId.contains(all))
					return true;
		}
		return false;
	}
}
