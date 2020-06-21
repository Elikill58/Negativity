package com.elikill58.negativity.universal.verif.storage.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

@SuppressWarnings("unchecked")
public class FileVerificationStorage extends VerificationStorage {

	private final File userDir;

	public FileVerificationStorage(File userDir) {
		this.userDir = userDir;
		this.userDir.mkdirs();
	}

	@Override
	public CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId) {
		Adapter ada = Adapter.getAdapter();
		NegativityPlayer np = ada.getNegativityPlayer(playerId);
		List<Verificator> list = new ArrayList<>();
		File file = new File(userDir.getAbsolutePath() + File.separator + playerId.toString());
		for(File verification : file.listFiles()) {
			if(!(verification.isFile() && verification.getName().endsWith(".json")))
				continue;
			try {
				String content = Files.readAllLines(verification.toPath()).stream().collect(Collectors.joining(""));
				JSONObject json = (JSONObject) new JSONParser().parse(content);
				Map<Cheat, VerifData> cheats = new HashMap<>(); // don't need to load it
				String startedBy = json.get("startedBy").toString();
				List<String> result = (List<String>) json.get("result");
				int version = (int) json.get("version");
				Version playerVersion = Version.getVersionByName(json.get("player_version").toString());
				list.add(new Verificator(np, startedBy, cheats, result, version, playerVersion));
			} catch (Exception e) {
				ada.log("Could not load verification of file " + verification.getAbsolutePath());
				e.printStackTrace();
			}
		}
		return CompletableFuture.completedFuture(list);
	}

	@Override
	public CompletableFuture<Void> saveVerification(Verificator verif) {
		File folder = new File(userDir.getAbsolutePath(), verif.getPlayerId().toString());
		folder.mkdirs();
		File file = new File(folder, getNewFileName());
		
		JSONObject json = new JSONObject();
		json.put("startedBy", verif.getAsker());
		json.put("result", verif.getMessages());
		JSONObject jsonCheat = new JSONObject();
		verif.getCheats().forEach((cheat, verifData) -> {
			if(verifData.hasSomething()) {
				jsonCheat.put(cheat.getKey(), verifData.getAllData().values().stream().filter(DataCounter::has).map(DataCounter::print).collect(Collectors.toList()));
			} else
				jsonCheat.put(cheat.getKey(), null);
		});
		json.put("cheats", jsonCheat);
		json.put("player_version", verif.getPlayerVersion().name());
		json.put("version", verif.getVersion());
		try {
			if(!file.exists())
				file.createNewFile();
			Files.write(file.toPath(), json.toJSONString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			Adapter.getAdapter().log("Could not save verification to file.");
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}
}
