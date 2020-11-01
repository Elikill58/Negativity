ALTER TABLE negativity_accounts
    ADD COLUMN creation_time TIMESTAMP NOT NULL DEFAULT NOW();
