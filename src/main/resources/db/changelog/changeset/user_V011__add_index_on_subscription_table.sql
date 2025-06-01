CREATE INDEX IF NOT EXISTS idx_subscription_follower ON subscription(follower_id, followee_id);
