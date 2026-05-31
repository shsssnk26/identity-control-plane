-- Add family_id to refresh_tokens to support token family tracking and reuse detection.
-- All tokens issued from the same login session share a family_id.
-- When reuse is detected on any token, the entire family is revoked.
ALTER TABLE auth.refresh_tokens
    ADD COLUMN family_id UUID NOT NULL DEFAULT gen_random_uuid();

-- Index for efficient family-wide revocation queries.
CREATE INDEX idx_refresh_tokens_family_id ON auth.refresh_tokens (family_id);
