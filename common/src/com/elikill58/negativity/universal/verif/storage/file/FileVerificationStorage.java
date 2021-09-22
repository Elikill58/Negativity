package com.elikill58.negativity.universal.verif.storage.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

public class FileVerificationStorage extends VerificationStorage {

	private final Path userDir;

	public FileVerificationStorage(Path userDir) {
		this.userDir = userDir;
	}

	@Override
	public CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			Path dir = userDir.resolve(playerId.toString());
			if (!Files.isDirectory(dir)) {
				return Collections.emptyList();
			}

			Adapter ada = Adapter.getAdapter();
			NegativityPlayer np = NegativityPlayer.getCached(playerId);
			List<Verificator> list = new ArrayList<>();
			try (DirectoryStream<Path> entries = Files.newDirectoryStream(dir,
					path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".json"))) {
				for(Path verification : entries) {
					try (BufferedReader reader = Files.newBufferedReader(verification)) {
						JSONObject json = (JSONObject) new JSONParser().parse(reader);
						Map<CheatKeys, VerifData> cheats = new HashMap<>(); // don't need to load it
						String startedBy = json.get("startedBy").toString();
						@SuppressWarnings("unchecked")
						List<String> result = (List<String>) json.get("result");
						int version = (int) (long) json.get("version");
						Version playerVersion = Version.getVersionByName(json.get("player_version").toString());
						list.add(new Verificator(np, startedBy, cheats, result, version, playerVersion));
					} catch (Exception e) {
						ada.getLogger().error("Could not load verification of file " + verification.toAbsolutePath());
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				throw new CompletionException(e);
			}
			return list;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<Void> saveVerification(Verificator verif) {
		return CompletableFuture.runAsync(() -> {
			Path dir = userDir.resolve(verif.getPlayerId().toString());
			Path file = dir.resolve(getNewFileName());
			JSONObject json = new JSONObject();
			json.put("startedBy", verif.getAsker());
			json.put("result", verif.getMessages());
			JSONObject jsonCheat = new JSONObject();
			verif.getCheats().forEach((cheat, verifData) -> {
				if(verifData.hasSomething()) {
					jsonCheat.put(cheat, verifData.toJson());
				} else
					jsonCheat.put(cheat, null);
			});
			json.put("cheats", jsonCheat);
			json.put("player_version", verif.getPlayerVersion().name());
			json.put("version", verif.getVersion());
			try {
				Files.createDirectories(dir);
				try (BufferedWriter writer = Files.newBufferedWriter(file)) {
					json.writeJSONString(writer);
				}
			} catch (IOException e) {
				Adapter.getAdapter().getLogger().error("Could not save verification to file.");
				e.printStackTrace();
			}
		});
	}
}
