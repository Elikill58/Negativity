package com.elikill58.negativity.universal.dataStorage.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnectionProvider {

	Connection get() throws SQLException;

	void close();
}
