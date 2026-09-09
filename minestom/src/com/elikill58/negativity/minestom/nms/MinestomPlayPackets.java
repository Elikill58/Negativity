package com.elikill58.negativity.minestom.nms;

import java.util.HashMap;
import java.util.function.Function;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInArmAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInCustomPayload;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInGround;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSetCreativeSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSettings;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSteerVehicle;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInTeleportAccept;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutCustomPayload;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityDestroy;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityHeadRotation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutMultiBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition.EnumPlayerTeleportFlags;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRelEntityLook;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRelEntityMove;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRelEntityMoveLook;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnloadChunk;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.minestom.impl.item.MinestomItemStack;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.MainHand;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.common.*;
import net.minestom.server.network.packet.client.play.*;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.common.*;
import net.minestom.server.network.packet.server.play.*;
import net.minestom.server.network.player.ClientSettings;

public class MinestomPlayPackets {

	public class Client {
		private static HashMap<Class<? extends ClientPacket>, Function<ClientPacket, NPacketPlayIn>> clientPackets = new HashMap<>();

		static {
			// same as https://github.com/Minestom/Minestom/blob/master/src/main/java/net/minestom/server/network/packet/client/ClientPacketsHandler.java

	        entry(ClientTeleportConfirmPacket.class, (p) -> {
	            NPacketPlayInTeleportAccept n = new NPacketPlayInTeleportAccept();
	            n.teleportId = p.teleportId();
	            return n;
	        });
	        entry(ClientQueryBlockNbtPacket.class);
	        entry(ClientSelectBundleItemPacket.class);
	        entry(ClientChangeDifficultyPacket.class);
	        entry(ClientChangeGameModePacket.class);
	        entry(ClientChatAckPacket.class);
	        entry(ClientCommandChatPacket.class);
	        entry(ClientSignedCommandChatPacket.class);
	        entry(ClientChatMessagePacket.class, (p) -> {
	            NPacketPlayInChat n = new NPacketPlayInChat();
	            n.message = p.message();
	            return n;
	        });
	        entry(ClientChatSessionUpdatePacket.class);
	        entry(ClientChunkBatchReceivedPacket.class);
	        entry(ClientStatusPacket.class);
	        entry(ClientTickEndPacket.class);
	        entry(ClientSettingsPacket.class, (p) -> {
	            NPacketPlayInSettings n = new NPacketPlayInSettings();
	            ClientSettings s = p.settings();
	            n.locale = s.locale().toString();
	            n.viewDistance = s.viewDistance();
	            n.chatMode = NPacketPlayInSettings.ChatMode.values()[s.chatMessageType().ordinal()];
	            n.displayedSkinParts = s.displayedSkinParts() & 0xFF;
	            n.mainHand = s.mainHand() == MainHand.RIGHT ? Hand.MAIN : Hand.OFF;
	            return n;
	        });
	        entry(ClientTabCompletePacket.class);
	        entry(ClientConfigurationAckPacket.class);
	        entry(ClientClickWindowButtonPacket.class);
	        entry(ClientClickWindowPacket.class);
	        entry(ClientCloseWindowPacket.class);
	        entry(ClientWindowSlotStatePacket.class);
	        entry(ClientCookieResponsePacket.class);
	        entry(ClientPluginMessagePacket.class, (p) -> {
	            NPacketPlayInCustomPayload n = new NPacketPlayInCustomPayload();
	            n.channel = p.channel();
	            n.data = p.data();
	            return n;
	        });
	        entry(ClientEditBookPacket.class);
	        entry(ClientQueryEntityNbtPacket.class);
	        entry(ClientInteractEntityPacket.class, (p) -> {
	            NPacketPlayInUseEntity n = new NPacketPlayInUseEntity();
	            n.entityId = p.targetId();
	            n.action = NPacketPlayInUseEntity.EnumEntityUseAction.INTERACT;
	            return n;
	        });
	        entry(ClientGenerateStructurePacket.class);
	        entry(ClientKeepAlivePacket.class, (p) -> {
	            NPacketPlayInKeepAlive n = new NPacketPlayInKeepAlive();
	            n.time = p.id();
	            return n;
	        });
	        entry(ClientLockDifficultyPacket.class);
	        entry(ClientPlayerPositionPacket.class, (p) -> {
	            NPacketPlayInPosition n = new NPacketPlayInPosition();
	            n.hasPos = true;
	            n.hasLook = false;
	            Point pos = p.position();
	            n.x = pos.x();
	            n.y = pos.y();
	            n.z = pos.z();
	            n.isGround = p.onGround();
	            return n;
	        });
	        entry(ClientPlayerPositionAndRotationPacket.class, (p) -> {
	            NPacketPlayInPositionLook n = new NPacketPlayInPositionLook();
	            n.hasPos = true;
	            n.hasLook = true;
	            n.x = p.position().x();
	            n.y = p.position().y();
	            n.z = p.position().z();
	            n.yaw = p.position().yaw();
	            n.pitch = p.position().pitch();
	            n.isGround = p.onGround();
	            return n;
	        });
	        entry(ClientPlayerRotationPacket.class, (p) -> {
	            NPacketPlayInLook n = new NPacketPlayInLook();
	            n.hasPos = false;
	            n.hasLook = true;
	            n.yaw = p.yaw();
	            n.pitch = p.pitch();
	            n.isGround = p.onGround();
	            return n;
	        });
	        entry(ClientPlayerPositionStatusPacket.class, (p) -> {
	            NPacketPlayInGround n = new NPacketPlayInGround();
	            n.isGround = p.onGround();
	            return n;
	        });
	        entry(ClientVehicleMovePacket.class);
	        entry(ClientSteerBoatPacket.class);
	        entry(ClientPickItemFromBlockPacket.class);
	        entry(ClientPickItemFromEntityPacket.class);
	        entry(ClientPingRequestPacket.class);
	        entry(ClientPlaceRecipePacket.class);
	        entry(ClientPlayerAbilitiesPacket.class);
	        entry(ClientEntityActionPacket.class, (p) -> {
	            NPacketPlayInEntityAction n = new NPacketPlayInEntityAction();
	            n.entityId = p.playerId();
	            n.sequence = p.horseJumpBoost();
	            n.action = mapEntityAction(p.action());
	            return n;
	        });
	        entry(ClientInputPacket.class, (p) -> {
	            float impulse = 0.98F;
	            float sideways = p.left() ? impulse : (p.right() ? -impulse : 0F);
	            float forward = p.forward() ? impulse : (p.backward() ? -impulse : 0F);
	            return new NPacketPlayInSteerVehicle(sideways, forward, p.jump(), p.shift());
	        });
	        entry(ClientPlayerLoadedPacket.class);
	        entry(ClientPongPacket.class, (p) -> {
	            NPacketPlayInPong n = new NPacketPlayInPong();
	            n.id = p.id();
	            return n;
	        });
	        entry(ClientSetRecipeBookStatePacket.class);
	        entry(ClientRecipeBookSeenRecipePacket.class);
	        entry(ClientNameItemPacket.class);
	        entry(ClientResourcePackStatusPacket.class);
	        entry(ClientAdvancementTabPacket.class);
	        entry(ClientSelectTradePacket.class);
	        entry(ClientSetBeaconEffectPacket.class);
	        entry(ClientHeldItemChangePacket.class, (p) -> {
	            NPacketPlayInHeldItemSlot n = new NPacketPlayInHeldItemSlot();
	            n.slot = p.slot();
	            return n;
	        });
	        entry(ClientUpdateCommandBlockPacket.class);
	        entry(ClientUpdateCommandBlockMinecartPacket.class);
	        entry(ClientCreativeInventoryActionPacket.class, (p) -> {
	            NPacketPlayInSetCreativeSlot n = new NPacketPlayInSetCreativeSlot();
	            n.slot = p.slot();
	            n.item = new MinestomItemStack(p.item());
	            return n;
	        });
	        entry(ClientUpdateJigsawBlockPacket.class);
	        entry(ClientUpdateStructureBlockPacket.class);
	        entry(ClientSetTestBlockPacket.class);
	        entry(ClientUpdateSignPacket.class);
	        entry(ClientAnimationPacket.class, (p) -> new NPacketPlayInArmAnimation());
	        // ClientSpectatePacket was removed in Minestom 26.x (now PlayerSpectateEntityEvent); not needed by any check.
	        entry(ClientTestInstanceBlockActionPacket.class);
	        entry(ClientPlayerBlockPlacementPacket.class, (p) -> {
	            NPacketPlayInBlockPlace n = new NPacketPlayInBlockPlace();
	            n.hand = Hand.valueOf(p.hand().name());
	            Point bp = p.blockPosition();
	            n.pos = new BlockPosition(bp.blockX(), bp.blockY(), bp.blockZ());
	            n.face = BlockFace.getById(p.blockFace().ordinal());
	            n.cursorX = p.cursorPositionX();
	            n.cursorY = p.cursorPositionY();
	            n.cursorZ = p.cursorPositionZ();
	            n.insideBlock = p.insideBlock();
	            return n;
	        });
	        entry(ClientUseItemPacket.class, (p) -> {
	            NPacketPlayInUseItem n = new NPacketPlayInUseItem();
	            n.hand = Hand.valueOf(p.hand().name());
	            n.sequence = p.sequence();
	            return n;
	        });
	        entry(ClientCustomClickActionPacket.class);
		}

		private static NPacketPlayInEntityAction.EnumPlayerAction mapEntityAction(ClientEntityActionPacket.Action action) {
			switch (action) {
				case LEAVE_BED: return NPacketPlayInEntityAction.EnumPlayerAction.LEAVE_BED;
				case START_SPRINTING: return NPacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING;
				case STOP_SPRINTING: return NPacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING;
				case START_JUMP_HORSE: return NPacketPlayInEntityAction.EnumPlayerAction.START_RIDING_JUMP;
				case STOP_JUMP_HORSE: return NPacketPlayInEntityAction.EnumPlayerAction.STOP_RIDING_JUMP;
				case OPEN_HORSE_INVENTORY: return NPacketPlayInEntityAction.EnumPlayerAction.OPEN_INVENTORY;
				case START_FLYING_ELYTRA: return NPacketPlayInEntityAction.EnumPlayerAction.START_FALL_FLYING;
				default: return null;
			}
		}

		private static void entry(Class<? extends ClientPacket> clazz) {
			clientPackets.put(clazz, null);
		}

		private static <T extends ClientPacket> void entry(Class<T> clazz, Function<T, NPacketPlayIn> writer) {
			clientPackets.put(clazz, (Function<ClientPacket, NPacketPlayIn>) writer);
		}

		public static NPacketPlayIn build(ClientPacket packet) {
			Function<ClientPacket, NPacketPlayIn> fct = clientPackets.get(packet.getClass());
			if (fct == null)
				return new NPacketPlayInUnset();
			return fct.apply(packet);
		}
	}

	public class Server {

		private static HashMap<Class<? extends ServerPacket>, Function<ServerPacket, NPacketPlayOut>> serverPackets = new HashMap<>();

	    static {
            entry(BundlePacket.class);
            entry(SpawnEntityPacket.class, (p) -> {
                NPacketPlayOutSpawnEntity n = new NPacketPlayOutSpawnEntity();
                n.entityId = p.entityId();
                n.entityUUID = p.uuid();
                net.minestom.server.entity.EntityType met = p.type();
                n.type = met == null ? EntityType.UNKNOWN : EntityType.get(met.name());
                n.x = p.position().x();
                n.y = p.position().y();
                n.z = p.position().z();
                n.yaw = p.position().yaw();
                n.pitch = p.position().pitch();
                n.modX = p.velocity().x();
                n.modY = p.velocity().y();
                n.modZ = p.velocity().z();
                return n;
            });
            entry(EntityAnimationPacket.class);
            entry(StatisticsPacket.class);
            entry(AcknowledgeBlockChangePacket.class);
            entry(BlockBreakAnimationPacket.class, (p) -> {
                NPacketPlayOutBlockBreakAnimation n = new NPacketPlayOutBlockBreakAnimation();
                n.entityId = p.entityId();
                Point bp = p.blockPosition();
                n.pos = new BlockPosition(bp.blockX(), bp.blockY(), bp.blockZ());
                n.destroyStage = p.destroyStage() & 0xFF;
                return n;
            });
            entry(BlockEntityDataPacket.class);
            entry(BlockActionPacket.class);
            entry(BlockChangePacket.class, (p) -> {
                NPacketPlayOutBlockChange n = new NPacketPlayOutBlockChange();
                Point bp = p.blockPosition();
                n.pos = new BlockPosition(bp.blockX(), bp.blockY(), bp.blockZ());
                n.stateId = p.blockStateId();
                return n;
            });
            entry(BossBarPacket.class);
            entry(ServerDifficultyPacket.class);
            entry(ChunkBatchFinishedPacket.class);
            entry(ChunkBatchStartPacket.class);
            entry(ChunkBiomesPacket.class);
            entry(ClearTitlesPacket.class);
            entry(TabCompletePacket.class);
            entry(DeclareCommandsPacket.class);
            entry(CloseWindowPacket.class);
            entry(WindowItemsPacket.class);
            entry(WindowPropertyPacket.class);
            entry(SetSlotPacket.class);
            entry(CookieRequestPacket.class);
            entry(SetCooldownPacket.class);
            entry(CustomChatCompletionPacket.class);
            entry(PluginMessagePacket.class, (p) -> new NPacketPlayOutCustomPayload(p.channel(), p.data()));
            entry(DamageEventPacket.class);
            entry(DebugSamplePacket.class);
            entry(DeleteChatPacket.class);
            entry(DisconnectPacket.class);
            entry(DisguisedChatPacket.class);
            entry(EntityStatusPacket.class);
            entry(EntityPositionSyncPacket.class, (p) -> {
                NPacketPlayOutEntityTeleport n = new NPacketPlayOutEntityTeleport();
                n.entityId = p.entityId();
                n.x = p.position().x();
                n.y = p.position().y();
                n.z = p.position().z();
                n.yaw = p.yaw();
                n.pitch = p.pitch();
                n.onGround = p.onGround();
                return n;
            });
            entry(ExplosionPacket.class, (p) -> {
                NPacketPlayOutExplosion n = new NPacketPlayOutExplosion();
                n.x = p.center().x();
                n.y = p.center().y();
                n.z = p.center().z();
                if (p.playerKnockback() != null)
                    n.vec = new Vector(p.playerKnockback().x(), p.playerKnockback().y(), p.playerKnockback().z());
                return n;
            });
            entry(UnloadChunkPacket.class, (p) -> {
                NPacketPlayOutUnloadChunk n = new NPacketPlayOutUnloadChunk();
                n.x = p.chunkX();
                n.y = p.chunkZ();
                return n;
            });
            entry(ChangeGameStatePacket.class);
            entry(OpenHorseWindowPacket.class);
            entry(HitAnimationPacket.class);
            entry(InitializeWorldBorderPacket.class);
            entry(KeepAlivePacket.class, (p) -> {
                NPacketPlayOutKeepAlive n = new NPacketPlayOutKeepAlive();
                n.time = p.id();
                return n;
            });
            entry(ChunkDataPacket.class);
            entry(WorldEventPacket.class);
            entry(ParticlePacket.class);
            entry(UpdateLightPacket.class);
            entry(JoinGamePacket.class);
            entry(MapDataPacket.class);
            entry(TradeListPacket.class);
            entry(EntityPositionPacket.class, (p) -> {
                NPacketPlayOutRelEntityMove n = new NPacketPlayOutRelEntityMove();
                n.entityId = p.entityId();
                n.deltaX = p.deltaX() / 4096.0;
                n.deltaY = p.deltaY() / 4096.0;
                n.deltaZ = p.deltaZ() / 4096.0;
                n.isGround = p.onGround();
                return n;
            });
            entry(EntityPositionAndRotationPacket.class, (p) -> {
                NPacketPlayOutRelEntityMoveLook n = new NPacketPlayOutRelEntityMoveLook();
                n.entityId = p.entityId();
                n.deltaX = p.deltaX() / 4096.0;
                n.deltaY = p.deltaY() / 4096.0;
                n.deltaZ = p.deltaZ() / 4096.0;
                n.yaw = p.yaw();
                n.pitch = p.pitch();
                n.isGround = p.onGround();
                return n;
            });
            entry(MoveMinecartPacket.class);
            entry(EntityRotationPacket.class, (p) -> {
                NPacketPlayOutRelEntityLook n = new NPacketPlayOutRelEntityLook();
                n.entityId = p.entityId();
                n.yaw = p.yaw();
                n.pitch = p.pitch();
                n.isGround = p.onGround();
                return n;
            });
            entry(VehicleMovePacket.class);
            entry(OpenBookPacket.class);
            entry(OpenWindowPacket.class);
            entry(OpenSignEditorPacket.class);
            entry(PingPacket.class, (p) -> new NPacketPlayOutPing(p.id()));
            entry(PingResponsePacket.class);
            entry(PlaceGhostRecipePacket.class);
            entry(PlayerAbilitiesPacket.class);
            entry(PlayerChatMessagePacket.class);
            entry(EndCombatEventPacket.class);
            entry(EnterCombatEventPacket.class);
            entry(DeathCombatEventPacket.class);
            entry(PlayerInfoRemovePacket.class);
            entry(PlayerInfoUpdatePacket.class);
            entry(FacePlayerPacket.class);
            entry(PlayerPositionAndLookPacket.class, (p) -> {
                NPacketPlayOutPosition n = new NPacketPlayOutPosition();
                n.teleportId = p.teleportId();
                n.x = p.position().x();
                n.y = p.position().y();
                n.z = p.position().z();
                n.yaw = p.yaw();
                n.pitch = p.pitch();
                n.flags = EnumPlayerTeleportFlags.get(p.flags());
                return n;
            });
            entry(PlayerRotationPacket.class);
            entry(RecipeBookAddPacket.class);
            entry(RecipeBookRemovePacket.class);
            entry(RecipeBookSettingsPacket.class);
            entry(DestroyEntitiesPacket.class, (p) -> {
                NPacketPlayOutEntityDestroy n = new NPacketPlayOutEntityDestroy();
                n.entityIds = p.entityIds().stream().mapToInt(Integer::intValue).toArray();
                return n;
            });
            entry(RemoveEntityEffectPacket.class);
            entry(ResetScorePacket.class);
            entry(ResourcePackPopPacket.class);
            entry(ResourcePackPushPacket.class);
            entry(RespawnPacket.class);
            entry(EntityHeadLookPacket.class, (p) -> {
                NPacketPlayOutEntityHeadRotation n = new NPacketPlayOutEntityHeadRotation();
                n.entityId = p.entityId();
                n.yaw = p.yaw();
                return n;
            });
            entry(MultiBlockChangePacket.class, (p) -> {
                NPacketPlayOutMultiBlockChange n = new NPacketPlayOutMultiBlockChange();
                long pos = p.chunkSectionPosition();
                n.chunkX = pos >> 42;
                n.chunkY = (pos << 44) >> 44;
                n.chunkZ = (pos << 22) >> 42;
                return n;
            });
            entry(SelectAdvancementTabPacket.class);
            entry(ServerDataPacket.class);
            entry(ActionBarPacket.class);
            entry(WorldBorderCenterPacket.class);
            entry(WorldBorderLerpSizePacket.class);
            entry(WorldBorderSizePacket.class);
            entry(WorldBorderWarningDelayPacket.class);
            entry(WorldBorderWarningReachPacket.class);
            entry(CameraPacket.class);
            entry(UpdateViewPositionPacket.class);
            entry(UpdateViewDistancePacket.class);
            entry(SetCursorItemPacket.class);
            entry(SpawnPositionPacket.class);
            entry(DisplayScoreboardPacket.class);
            entry(EntityMetaDataPacket.class);
            entry(AttachEntityPacket.class);
            entry(EntityVelocityPacket.class, (p) -> {
                NPacketPlayOutEntityVelocity n = new NPacketPlayOutEntityVelocity();
                n.entityId = p.entityId();
                n.vec = new Vector(p.velocity().x(), p.velocity().y(), p.velocity().z());
                return n;
            });
            entry(EntityEquipmentPacket.class);
            entry(SetExperiencePacket.class);
            entry(UpdateHealthPacket.class);
            entry(HeldItemChangePacket.class);
            entry(ScoreboardObjectivePacket.class);
            entry(SetPassengersPacket.class);
            entry(SetPlayerInventorySlotPacket.class);
            entry(TeamsPacket.class);
            entry(UpdateScorePacket.class);
            entry(UpdateSimulationDistancePacket.class);
            entry(SetTitleSubTitlePacket.class);
            entry(SetTimePacket.class);
            entry(SetTitleTextPacket.class);
            entry(SetTitleTimePacket.class);
            entry(EntitySoundEffectPacket.class);
            entry(SoundEffectPacket.class);
            entry(StartConfigurationPacket.class);
            entry(StopSoundPacket.class);
            entry(CookieStorePacket.class);
            entry(SystemChatPacket.class);
            entry(PlayerListHeaderAndFooterPacket.class);
            entry(NbtQueryResponsePacket.class);
            entry(CollectItemPacket.class);
            entry(EntityTeleportPacket.class, (p) -> {
                NPacketPlayOutEntityTeleport n = new NPacketPlayOutEntityTeleport();
                n.entityId = p.entityId();
                n.x = p.position().x();
                n.y = p.position().y();
                n.z = p.position().z();
                n.yaw = p.position().yaw();
                n.pitch = p.position().pitch();
                n.onGround = p.onGround();
                return n;
            });
            entry(TestInstanceBlockStatus.class);
            entry(SetTickStatePacket.class);
            entry(TickStepPacket.class);
            entry(TransferPacket.class);
            entry(AdvancementsPacket.class);
            entry(EntityAttributesPacket.class);
            entry(EntityEffectPacket.class, (p) -> {
                NPacketPlayOutEntityEffect n = new NPacketPlayOutEntityEffect();
                n.entityId = p.entityId();
                n.type = PotionEffectType.fromId(p.potion().effect().id());
                n.amplifier = (byte) p.potion().amplifier();
                n.duration = p.potion().duration();
                n.flags = 0;
                return n;
            });
            entry(DeclareRecipesPacket.class);
            entry(TagsPacket.class);
            entry(ProjectilePowerPacket.class);
            entry(CustomReportDetailsPacket.class);
            entry(ServerLinksPacket.class);
            entry(TrackedWaypointPacket.class);
            entry(ClearDialogPacket.class);
            entry(ShowDialogPacket.class);
	    }

		private static void entry(Class<? extends ServerPacket> clazz) {
			// sanitize here to get default result
			// serverPackets.put(clazz, null);
		}

		private static <T extends ServerPacket> void entry(Class<T> clazz, Function<T, NPacketPlayOut> writer) {
		    serverPackets.put(clazz, (Function<ServerPacket, NPacketPlayOut>) writer);
		}

		public static NPacketPlayOut build(ServerPacket packet) {
			return serverPackets.getOrDefault(packet.getClass(), (pa) -> new NPacketPlayOutUnset()).apply(packet);
		}
	}
}
