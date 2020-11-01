ALTER TABLE negativity_bans_log
    ADD COLUMN execution_time TIMESTAMP,
    ADD COLUMN revocation_time TIMESTAMP;
