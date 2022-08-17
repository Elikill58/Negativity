CREATE TABLE IF NOT EXISTS negativity_warns
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    reason TINYTEXT,
    expiration_time LONG DEFAULT 0,
    banned_by VARCHAR(36) NOT NULL,
    ip VARCHAR(39) NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    revocation_time LONG DEFAULT -1
);
