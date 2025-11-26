-- Repeatable Migration: 기본 무료 아이템 자동 복구
-- Last updated: 2025-11-26(1)

INSERT INTO store (price, name, item_type)
SELECT 0, 'No Background', 'BACKGROUND'
    WHERE NOT EXISTS (
    SELECT 1 FROM store
    WHERE price = 0 AND item_type = 'BACKGROUND'
);

INSERT INTO store (price, name, item_type)
SELECT 0, 'No Sofa', 'SOFA'
    WHERE NOT EXISTS (
    SELECT 1 FROM store
    WHERE price = 0 AND item_type = 'SOFA'
);

INSERT INTO store (price, name, item_type)
SELECT 0, 'No Wall Decoration', 'WALL'
    WHERE NOT EXISTS (
    SELECT 1 FROM store
    WHERE price = 0 AND item_type = 'WALL'
);

INSERT INTO store (price, name, item_type)
SELECT 0, 'No Floor Decoration', 'FLOOR'
    WHERE NOT EXISTS (
    SELECT 1 FROM store
    WHERE price = 0 AND item_type = 'FLOOR'
);