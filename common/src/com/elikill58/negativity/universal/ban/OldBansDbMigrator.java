package com.elikill58.negativity.universal.ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Supplier;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.storage.DatabaseActiveBanStorage;
import com.elikill58.negativity.universal.database.Database;

public class OldBansDbMigrator {

	public static void performMigration() throws SQLException {
		Adapter adapter = Adapter.getAdapter();
		adapter.getLogger().info("[Bans DB Migration] Performing old bans database migration.");

		String banTable = getConfigString(adapter, "Database.table_ban");
		String uuidColumn = getConfigString(adapter, "ban.db.column.uuid");
		String timeColumn = getConfigString(adapter, "ban.db.column.time");
		String defColumn = getConfigString(adapter, "ban.db.column.def");
		String reasonColumn = getConfigString(adapter, "ban.db.column.reason");
		String cheatColumn = getConfigString(adapter, "ban.db.column.cheat_detect");
		String byColumn = getConfigString(adapter, "ban.db.column.by");

		Connection connection = Database.getConnection();
		checkState(connection != null, () -> "Could not find connection to the database.");

		checkState(tableExists(connection, banTable), () -> "The original bans table does not exist.");

		long now = System.currentTimeMillis();

		int bansProcessed = 0;
		int bansTransferred = 0;
		DatabaseActiveBanStorage storage = new DatabaseActiveBanStorage();
		try (PreparedStatement selectStm = connection.prepareStatement("SELECT * FROM " + banTable)) {
			ResultSet result = selectStm.executeQuery();
			while (result.next()) {
				UUID uuid = UUID.fromString(result.getString(uuidColumn));
				boolean def = result.getBoolean(defColumn);
				long time = -1;
				if (!def) {
					time = result.getLong(timeColumn);
				}
				String reason = result.getString(reasonColumn);
				String cheat = result.getString(cheatColumn);
				String by = result.getString(byColumn);

				bansProcessed++;
				// only retain active bans
				if (def || now <= time) {
					Ban ban = new Ban(uuid, reason, by, BanType.UNKNOW, time, cheat, null, BanStatus.ACTIVE);
					storage.save(ban);
					bansTransferred++;
				}
			}
		}

		if (bansProcessed == 0) {
			adapter.getLogger().info("[Bans DB Migration] No bans to migrate");
		} else if (bansTransferred == bansProcessed) {
			adapter.getLogger().info("[Bans DB Migration] All bans were transferred (" + bansTransferred + ")");
		} else {
			adapter.getLogger().info("[Bans DB Migration] Out of " + bansProcessed + " bans processed, " + bansTransferred + " were transferred and " + (bansProcessed - bansTransferred) + " were skipped because they were expired.");
		}
	}

	private static String getConfigString(Adapter adapter, String key) {
		String value = adapter.getConfig().getString(key);
		checkState(!value.equals(key), () -> "Missing required configuration value '" + key + "'");
		return value;
	}

	private static void checkState(boolean condition, Supplier<String> message) {
		if (!condition) {
			String msg = message.get();
			Adapter.getAdapter().getLogger().error("Failed to migrate old bans database: " + msg);
			throw new IllegalStateException("[Bans DB Migration] " + msg);
		}
	}

	private static boolean tableExists(Connection connection, String table) throws SQLException {
		try (PreparedStatement stm = connection.prepareStatement("SHOW TABLES LIKE ?")) {
			stm.setString(1, table);
			return stm.executeQuery().next();
		}
	}
}
