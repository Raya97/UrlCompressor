CREATE TABLE IF NOT EXISTS link_manager.short_links
(
    id         BIGSERIAL PRIMARY KEY,
    original   VARCHAR(2000)                       NOT NULL,
    alias      VARCHAR(50) UNIQUE                  NOT NULL,
    clicks     BIGINT    DEFAULT 0                 NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    valid_until TIMESTAMP,
    owner_id   BIGINT                              NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES link_manager.app_users (id)
    );
