package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.XRAY;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.ray.RayResult;
import com.elikill58.negativity.api.ray.block.BlockRayBuilder;
import com.elikill58.negativity.api.ray.block.BlockRayResult;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;

public class XRay extends Cheat {

	private static final List<Material> ORES = Arrays.asList(Materials.COAL_ORE, Materials.IRON_ORE, Materials.GOLD_ORE,
			Materials.DIAMOND_ORE, Materials.EMERALD_ORE, Materials.REDSTONE_ORE, Materials.QUARTZ_ORE,
			Materials.LAPIS_ORE);
	private static final List<Material> IMPORTANT_ORES = Arrays.asList(Materials.GOLD_ORE, Materials.DIAMOND_ORE,
			Materials.EMERALD_ORE, Materials.REDSTONE_ORE, Materials.QUARTZ_ORE, Materials.LAPIS_ORE);
	private static final List<Material> MINING_BLOCK = Arrays.asList(Materials.STONE, Materials.ANDESITE,
			Materials.GRANITE, Materials.DIORITE, Materials.GRAVEL);
	private static final long TIME_MINING = 10000;

	public XRay() {
		super(XRAY, CheatCategory.WORLD, Materials.EMERALD_ORE, CheatDescription.BLOCKS);
	}

	@Check(name = "minerate", description = "Count minerate")
	public void onBlockBreakMinerate(BlockBreakEvent e) {
		Player p = e.getPlayer();
		NegativityAccount acc = NegativityAccount.get(p.getUniqueId());
		Minerate mine = acc.getMinerate();
		MinerateType type = MinerateType.getMinerateType(e.getBlock().getType().getId());
		mine.addMine(type, p);
		if(type == null)
			return;
		int minedType = 0, fullMined = mine.getFullMined();
		for (int i : mine.getMined().values())
			minedType += i;
		int relia = minedType / fullMined;
		Negativity.alertMod(ReportType.WARNING, p, this, relia, "minerate",
				type.getOreName() + " mined. Full mined: " + fullMined + ". Mined by type: " + mine,
				hoverMsg("main", "%name%", type.getName(), "%nb%", mine.getMinerateType(type)));
		NegativityAccountStorage.getStorage().saveAccount(acc);
	}

	@Check(name = "mining-direction", description = "Check mining direction of player")
	public void onBlockBreak(BlockBreakEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Block b = e.getBlock();

		long time = System.currentTimeMillis();
		boolean isMining = (time - np.longs.get(XRAY, "is-mining", 0l)) < TIME_MINING;
		if (ORES.contains(b.getType())) {
			np.ints.set(XRAY, "mining-ore", 3);
		} else {
			int timeMiningOre = np.ints.get(XRAY, "mining-ore", 0);
			if (timeMiningOre > 0) {
				if (isMining) {
					// search for ore
					BlockRayResult blockResult = new BlockRayBuilder(p)
							.neededType(IMPORTANT_ORES.toArray(new Material[0])).build().compile();

					Location playerLoc = p.getLocation().clone();
					playerLoc.setY(b.getY());
					Vector v = p.getRotation().setY(b.getY());
					BlockRayResult checkForBuildDir = new BlockRayBuilder(playerLoc, v.multiply(new Vector(-1, 1, -1)))
							.maxDistance(10).build().compile();
					double distanceWithBuild = checkForBuildDir.getBlock() == null ? Double.MAX_VALUE
							: checkForBuildDir.getBlock().getLocation().distance(p.getLocation());

					Location loc = b.getLocation();
					double blockDistance = blockResult.getBlock().getLocation().distance(p.getLocation());
					if (blockResult.getRayResult().equals(RayResult.NEEDED_FOUND)
							&& blockResult.hasBlockExceptSearched() && blockDistance > 2 && distanceWithBuild < 500) {
						Location lastLoc = np.locations.get(XRAY, "mining-loc", null);
						if (lastLoc != null && blockIsJustAround(loc, lastLoc)) {
							Negativity.alertMod(ReportType.WARNING, p, this, 80, "mining-direction",
									"Found " + blockResult.getType() + ", timeMining: " + timeMiningOre
											+ ", blockDistance: " + blockDistance + ", distanceWithBuild: "
											+ distanceWithBuild);
						}
						np.locations.set(XRAY, "mining-loc", loc);
					} else
						np.ints.set(XRAY, "mining-ore", timeMiningOre - 1);
				} else
					np.ints.set(XRAY, "mining-ore", timeMiningOre - 1);
			}
		}
		if (MINING_BLOCK.contains(b.getType()))
			np.longs.set(XRAY, "is-mining", time);
	}

	private boolean blockIsJustAround(Location loc1, Location loc2) {
		return (loc1.getBlockX() - loc2.getBlockX() <= 1) && (loc1.getBlockY() - loc2.getBlockY() <= 1)
				&& (loc1.getBlockZ() - loc2.getBlockZ() <= 1);
	}
}
