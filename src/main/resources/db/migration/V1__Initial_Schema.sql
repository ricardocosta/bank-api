CREATE TABLE IF NOT EXISTS public.users (
    internal_id SERIAL PRIMARY KEY,
    public_id VARCHAR(64) NOT NULL UNIQUE,
    username VARCHAR(128) NOT NULL
);

CREATE INDEX IF NOT EXISTS user_public_id ON public.users USING BTREE(public_id);
