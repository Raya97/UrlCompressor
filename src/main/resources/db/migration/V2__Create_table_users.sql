CREATE TABLE IF NOT EXISTS link_manager.app_users
(
    id        BIGSERIAL PRIMARY KEY,
    login     VARCHAR(50) UNIQUE NOT NULL,
    pass_hash VARCHAR(100)       NOT NULL
    );
