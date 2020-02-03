CREATE TABLE IF NOT EXISTS negativity_bans_log
(
    id CHAR(36),
    reason TINYTEXT NOT NULL,
    banned_by VARCHAR(32) NOT NULL,
    expiration_time LONG NOT NULL,
    cheat_name VARCHAR(32),
    revoked BOOLEAN NOT NULL
);
