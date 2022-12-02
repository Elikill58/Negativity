package com.elikill58.negativity.api.packets.nms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.impl.item.JsonMaterial;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.json.JSONArray;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.json.parser.ParseException;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Handshake;
import com.elikill58.negativity.api.packets.nms.versions.PreFlattening;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public abstract class NamedVersion {

	protected final HashMap<Integer, PacketType.Client> playIn = new HashMap<>();
	protected final HashMap<Integer, PacketType.Server> playOut = new HashMap<>();
	protected final HashMap<Integer, PacketType.Handshake> handshake = new HashMap<>();
	protected final HashMap<Integer, PacketType.Login> login = new HashMap<>();
	protected final HashMap<Integer, PacketType.Status> status = new HashMap<>();
	protected final HashMap<Integer, EntityType> entityTypes = new HashMap<>();
	protected final HashMap<Integer, Material> materials = new HashMap<>();
	protected final HashMap<Integer, String> materialNameEntities = new HashMap<>();
	protected final String name;

	public NamedVersion(String name) {
		this.name = name;
		handshake.put(0, Handshake.IS_SET_PROTOCOL);

		int i = 0;
		for (String type : Arrays.asList("furnace", "chest", "trapped_chest", "ender_chest", "jukebox", "dispenser", "dropper", "sign", "spawner", "piston", "brewing_stand",
				"enchanting_table", "end_portal", "beacon", "skull", "daylight_detector", "hopper", "comparator", "banner", "structure_block", "end_gateway", "command_block", "shulker_box",
				"bed", "conduit", "barrel", "smoker", "blast_furnace", "lectern", "bell", "jigsaw", "campfire", "beehive", "sculk_sensor", "sculk_catalyst", "sculk_shrieker")) {
			materialNameEntities.put(i++, type);
		}
	}

	public String getName() {
		return name;
	}

	public void log() {
		Adapter.getAdapter().getLogger().info("Loaded version " + getName() + ". Packets playIn/playOut: " + playIn.size() + "/" + playOut.size() + ", entityTypes: " + entityTypes.size()
				+ ", materials: " + (materials.isEmpty() ? PreFlattening.size() : materials.size()));
	}

	public void loadPostFlattening(String dir) {
		Adapter ada = Adapter.getAdapter();
		try (InputStream input = UniversalUtils.openBundledFile(dir + "blocks.json")) {
			if (input == null) {
				ada.getLogger().error("Blocks.json file doesn't exist for directory " + dir + ".");
				return;
			}

			StringBuilder blocks = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					blocks.append(line);
				}
			}

			loadBlocks(blocks.toString());
		} catch (Exception e) {
			ada.getLogger().error("Failed to read blocks.json file.");
			e.printStackTrace();
		}
	}

	private void loadBlocks(String rawJson) throws ParseException {
		JSONArray json = (JSONArray) new JSONParser().parse(rawJson);
		for (Object obj : json) {
			JSONObject jsonBlock = (JSONObject) obj;
			JsonMaterial material = new JsonMaterial(jsonBlock);
			for (int i = Integer.parseInt(jsonBlock.get("minStateId").toString()); i <= Integer.parseInt(jsonBlock.get("maxStateId").toString()); i++)
				materials.put(i, material);
		}
	}

	/**
	 * Create packet with given ID for the given direction.
	 * <p>
	 * Return unset packet if not found
	 * 
	 * @param dir      direction of packet (for example: server to client)
	 * @param packetId the ID of the packet
	 * @return new created packet
	 */
	public NPacket getPacket(PacketDirection dir, int packetId) {
		switch (dir) {
		case CLIENT_TO_SERVER:
			return createPacket(dir, packetId, playIn);
		case SERVER_TO_CLIENT:
			return createPacket(dir, packetId, playOut);
		case HANDSHAKE:
			return createPacket(dir, packetId, handshake);
		case LOGIN:
			return createPacket(dir, packetId, login);
		case STATUS:
			return createPacket(dir, packetId, status);
		}
		return null;
	}

	private @NonNull NPacket createPacket(PacketDirection dir, int packetId, HashMap<Integer, ? extends PacketType> packetTypes) {
		PacketType type = packetTypes.get(packetId);
		if (type != null)
			return type.createNewPacket();
		Adapter.getAdapter().debug("Failed to find packetId " + packetId + " for " + dir.name() + " (registered: " + packetTypes.size() + ")");
		return dir.createUnsetPacket("ID:" + packetId);
	}

	/**
	 * Get entity type according to given ID. <br>
	 * Return {@link EntityType#UNKNOWN} is not found
	 * 
	 * @param id the entity type id
	 * @return type of entity or unknown
	 */
	public @NonNull EntityType getEntityType(int id) {
		if (!entityTypes.containsKey(id))
			Adapter.getAdapter().debug("Can't find entity type with id " + id);
		return entityTypes.getOrDefault(id, EntityType.UNKNOWN);
	}

	/**
	 * Get material according to block ID.<br>
	 * Retrieve it with complete blockState not only simple ID (for MC 1.13 and
	 * more)
	 * 
	 * @param id the ID of the material
	 * @return the material of the given id
	 */
	public Material getMaterial(int id) {
		if (!materials.containsKey(id))
			Adapter.getAdapter().debug("Can't find material with id " + id);
		return materials.getOrDefault(id, Materials.AIR);
	}

	public Material getMaterialForEntityBlock(int id) {
		return ItemRegistrar.getInstance().get(materialNameEntities.getOrDefault(id, "air"));
	}
}
