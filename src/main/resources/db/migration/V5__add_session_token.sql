ALTER TABLE mesas
    ADD COLUMN token_sesion    VARCHAR(100),
    ADD COLUMN token_expira_en TIMESTAMP;

UPDATE mesas SET token_sesion = gen_random_uuid()::text
WHERE token_sesion IS NULL;
