ALTER TABLE users ADD COLUMN provider VARCHAR(50);
ALTER TABLE users ADD CONSTRAINT unique_provider_username UNIQUE (provider, username);