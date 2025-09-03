CREATE TABLE IF NOT EXISTS orders
(
    id            SERIAL PRIMARY KEY,
    user_id       INTEGER     NOT NULL,
    status        VARCHAR(20) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS items
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(50) NOT NULL,
    price DECIMAL     NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items
(
    id       SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    item_id  INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE INDEX idx_item_name ON items (name);