package com.elikill58.negativity.universal.dataStorage.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class DatabaseNegativityAccountStorage extends NegativityAccountStorage {

	@Nullable
	@Override
	public NegativityAccount loadAccount(UUID playerId) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT language FROM negativity_accounts WHERE id = ?")) {
			stm.setString(1, playerId.toString());
			ResultSet result = stm.executeQuery();
			if (result.next()) {
				String language = result.getString(1);
				return new NegativityAccount(playerId, language);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveAccount(NegativityAccount account) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_accounts (id, language) VALUES (?, ?) ON DUPLICATE KEY UPDATE id = ?, language = ?")) {
			stm.setString(1, account.getPlayerId().toString());
			stm.setString(2, account.getLang());
			stm.setString(3, account.getPlayerId().toString());
			stm.setString(4, account.getLang());
			stm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		try {
			DatabaseMigrator.executeRemainingMigrations(Database.getConnection(), "accounts");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
	}
}
