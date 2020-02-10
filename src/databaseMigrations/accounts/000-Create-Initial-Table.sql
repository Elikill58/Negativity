CREATE TABLE IF NOT EXISTS negativity_accounts
(
    id CHAR(36) NOT NULL PRIMARY KEY,
    playername VARCHAR(16),
    language CHAR(5) NOT NULL,
    minerate VARCHAR(512) NOT NULL,
    most_clicks_per_second INT NOT NULL,
    violations_by_cheat VARCHAR(1024) NOT NULL
);
