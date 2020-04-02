package com.elikill58.negativity.sponge.protocols;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.RideEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class SpeedProtocol extends Cheat {

	public SpeedProtocol() {
		super(CheatKeys.SPEED, false, ItemTypes.BEACON, CheatCategory.MOVEMENT, true, "speed", "speedhack");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		if (np.justDismounted) {
			// Dismounting a boat teleports the player, triggering a false positive
			return;
		}
		Vector3d fromVect = e.getFromTransform().getPosition();
		Vector3d toVect = e.getToTransform().getPosition();
		double walkSpeed = p.get(Keys.WALKING_SPEED).get();
		if (p.getLocation().sub(Vector3i.UNIT_Y).getBlockType().equals(BlockTypes.SPONGE)
				|| np.isFlying() || walkSpeed > 2.0F || hasEnderDragonAround(p)
				|| np.hasPotionEffect(PotionEffectTypes.SPEED) || np.hasPotionEffect("DOLPHINS_GRACE") || p.getVehicle().isPresent()) {
			return;
		}

		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}

		if (canBoostWithPackedIce(p.getLocation())) {
			return;
		}

		ReportType type = (np.getWarn(this) > 7) ? ReportType.VIOLATION : ReportType.WARNING;
		boolean mayCancel = false;
		double distance = toVect.sub(0, toVect.getY(), 0).distance(fromVect.sub(0, fromVect.getY(), 0));
		String proof = "In ground: " + p.isOnGround() + "WalkSpeed: " + p.get(Keys.WALKING_SPEED).get() + "  Distance between from/to location: " + distance;
		if (p.isOnGround() && distance >= 0.75D) {
			mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(distance * 100 * 2), proof,
					"Distance Last/New position: " + distance + "\n(With same Y)\nPlayer on ground", "Distance Last-New position: " + distance);
		} else if (!p.isOnGround()) {
			for(Entity et : p.getNearbyEntities(5))
				if(et.getType().equals(EntityTypes.CREEPER))
					return;
			float fallDistance = np.getFallDistance();
			String downName = p.getLocation().sub(0, 1, 0).getBlock().getType().getId();
			Location<World> from = e.getFromTransform().getLocation(), to = e.getToTransform().getLocation();
			if (!(from.getY() < to.getY() || p.isOnGround() || fallDistance > 0.0F || p.get(FoodData.class).get().foodLevel().get() < 6
					|| downName.contains("SLAB") || downName.contains("STEP") || downName.contains("SPONGE")
					|| downName.contains("SLIME_BLOCK") || downName.contains("ICE"))) {
				double f = (from.getY() - to.getY()) / 2.0D;
				if (!(F_LIST.contains(f) || (f < 0.47D && f > 0.46D) || (f < 0.02D && f > 0.01D)) && !p.getLocation().getBlock().getType().getId().contains("WATER") && !downName.contains("WATER") && !downName.contains("WEB")) {
					mayCancel = SpongeNegativity.alertMod(
							np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(99),
							"Player NOT in ground. WalkSpeed: " + walkSpeed + " Distance shorted: " + f,
							"Distance: " + f, "Distance: " + f);
				}
			}
			if(!mayCancel) {
				if(distance >= 0.85D) {
					mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(distance * 100 * 2), proof,
							"Distance Last/New position: " + distance + "\n(With same Y)\nPlayer jumping", "Distance Last-New position: " + distance);
				} else {
					BlockType under = e.getToTransform().getLocation().copy().sub(0, 1, 0).getBlockType();
					if (under.getId().contains("STEP")) {
						double distanceWithY = e.getFromTransform().getPosition().distance(e.getToTransform().getPosition());
						if (distanceWithY > 0.4) {
							np.SPEED_NB++;
							if (np.SPEED_NB > 4)
								mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.SPEED), 86 + np.SPEED_NB, "HighSpeed - Block under: " + under.getId() + ", Speed: " + distanceWithY + ", nb: " + np.SPEED_NB);
						} else
							np.SPEED_NB = 0;
					}
				}
			}
		}
		if (mayCancel && isSetBack()) {
			e.setCancelled(true);
		}
	}

	@Listener
	public void onEntityMount(RideEntityEvent.Mount event, @First Player player) {
		SpongeNegativityPlayer.getNegativityPlayer(player).justDismounted = true;
	}

	@Listener
	public void onEntityDismount(RideEntityEvent.Dismount event, @First Player player) {
		Task.builder()
				.delayTicks(3)
				.execute(() -> SpongeNegativityPlayer.getNegativityPlayer(player).justDismounted = false)
				.submit(SpongeNegativity.getInstance());
	}

	private boolean canBoostWithPackedIce(Location<World> feetLocation) {
		if (feetLocation.sub(Vector3i.UNIT_Y).getBlockType() != BlockTypes.PACKED_ICE) {
			return false;
		}

		BlockType blockTypeAtHead = feetLocation.add(Vector3i.UNIT_Y).getBlockType();
		return blockTypeAtHead == BlockTypes.TRAPDOOR || blockTypeAtHead == BlockTypes.IRON_TRAPDOOR;
	}
	
	private boolean hasEnderDragonAround(Player p) {
		for(Entity et : p.getWorld().getEntities())
			if(et.getType().equals(EntityTypes.ENDER_DRAGON) && et.getLocation().getPosition().distance(p.getLocation().getPosition()) < 15)
				return true;
		return false;
	}

	@Listener
	public void onEntityDamage(DamageEntityEvent e) {
		Entity damaged = e.getTargetEntity();
		if (damaged instanceof Player)
			SpongeNegativityPlayer.getNegativityPlayer((Player) damaged).BYPASS_SPEED = 3;
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}

	private final List<Double> F_LIST = Arrays.asList(0.03920000076293961D, 0.03920000076293917D, 0.0D,
			0.03276131020250217D, 0.028767927000034277D, 0.07632600455671046D, 0.013803173459940865D,
			0.018122953979760492D, 0.018122953979760492D, 0.03918750000000015D, 0.032761310202502614D,
			0.02619121792176493D, 0.040443614330228694D, 0.013746000988483775D, 0.017507033767796276D,
			0.01817973367687098D, 0.01653859408177394D, 0.015965626910608766D, 0.016503786845887713D,
			0.005200000023841911D, 0.010000000000000009D, 0.014732501406483856D, 0.011732552240547811D,
			0.005328000352859519D, 0.0333200007653236D, 0.006296864748065234D, 0.035940010583495496D,
			0.016690972346944388D, 0.06653028447689735D, 0.021144478961028668D, 0.0027204774809259646D,
			0.03918749999999971D, 0.022254607390188585D, 0.04061515508025826D, 0.022254607390189474D,
			0.07761600225830101D, 0.10499999999999998D, 0.10499999344348909D, 0.025270519530039337D,
			0.06396511038438035D, 0.007531072166550246D, 0.046580451629800734D, 0.039187499999997044D,
			0.033320000765325375D, 0.023690967029402543D, 0.07761600225829923D, 0.07761600225830279D,
			0.11681600302124195D, 0.17499999999999716D, 0.17499999999999716D, 0.23207968747772156D,
			0.22680463046910404D, 0.0391875000000006D, 0.047071719097424136D, 0.03254978976379164D, 0.0750218738309556D,
			0.02225460739018814D, 0.009800000336763759D, 0.029400001010291277D, 0.02352000080823302D,
			0.05096000116705568D, 0.023520000808275654D, 0.050960001167076996D, 0.009800000336781522D,
			0.02548000087563196D, 0.007840000269425218D, 0.03136000107770087D, 0.03136000107764403D,
			0.02114447896102689D, 0.002720477480927741D, 0.02939120043827259D, 0.005003033771895815D,
			0.07852684485229133D, 0.05411152379635098D, 0.0034841843643391712D, 0.03365709126414629D,
			0.04320968517403401D, 0.03337880059356735D, 0.04609868587078836D, 0.07756249999999909D,
			0.0060025001168284575D, 0.003725224382066017D, 0.02643868427014695D, 0.009614876287834306D,
			0.02643868427014695D, 0.02089388473969933D, 0.02023210002460729D, 0.008441913610305107D,
			0.032321134613610525D, 0.022927642704296147D, 0.03114600528100908D, 0.02527051953004289D,
			0.07502187383095915D, 0.0426458028454455D, 0.022254607390131298D, 0.036670088836586956D,
			0.020893884739702884D, 0.025094125213861673D, 0.034088546104840134D, 0.002878750056027002D,
			0.0022141748754620494D, 0.022254607390301828D, 0.040615155080256926D, 0.03408854610484369D,
			0.06787032969610607D, 0.05411152379635453D, 0.002214174875469155D, 0.05411152379635453D,
			0.02225460739056473D, 0.020468439829734564D, 0.0206944200509227D, 0.009667699613380876D,
			0.03750002384185791D, 0.06080002307891874D, 0.03772203259474338D, 0.03621390184022033D,
			0.021144478961030444D, 0.00625002384185791D, 0.02554262409816488D, 0.0625D, 0.02554262409816843D,
			0.02114447896102911D, 0.05992159054804702D, 0.03354439459599856D, 0.05228653300591901D,
			0.08396363151805497D, 0.036400761319463015D, 0.0748727475503026D, 0.029479971746241507D,
			0.06809037363654191D, 0.07333972048350113D, 0.025270519530041557D, 0.02352000080823835D,
			0.031360001077651134D, 0.03920000076293939D, 0.3928124999999998D, 0.03920000076293939D, 0.7873262571990489D,
			0.032989926232101396D, 0.3303750000000001D, 1.2492850116491319D, 0.9744362590014939D, 0.6747137561142442D,
			0.29593749999999996D, 0.04658045162980251D, 0.025270519530041113D);
}
