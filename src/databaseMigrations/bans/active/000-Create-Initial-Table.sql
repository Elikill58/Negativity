CREATE TABLE IF NOT EXISTS negativity_bans_active
(
    id CHAR(36) PRIMARY KEY,
    reason TINYTEXT NOT NULL,
    banned_by VARCHAR(32) NOT NULL,
    expiration_time LONG NOT NULL,
    cheat_name VARCHAR(32)
);
