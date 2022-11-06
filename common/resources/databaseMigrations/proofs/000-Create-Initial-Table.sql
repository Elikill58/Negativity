CREATE TABLE IF NOT EXISTS negativity_proofs
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL,
    cheat_key VARCHAR(64) NOT NULL,
    report_type VARCHAR(32) DEFAULT 'WARNING',
    check_name VARCHAR(64),
    check_informations LONGTEXT,
    ping INT DEFAULT 0,
    amount BIGINT DEFAULT 1,
    reliability INT DEFAULT 0,
    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version VARCHAR(32) DEFAULT 'HIGHER',
    warn BIGINT DEFAULT 0,
    tps VARCHAR(255)
);