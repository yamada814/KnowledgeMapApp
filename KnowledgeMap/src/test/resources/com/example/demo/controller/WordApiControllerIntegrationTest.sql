
DELETE FROM word;
DELETE FROM category;

INSERT INTO category(id, name) VALUES(1, 'テストカテゴリ');
INSERT INTO word(id, word_name, content, category_id)
VALUES(1, 'テストワード', 'テストコンテント', 1);
