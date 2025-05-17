
DELETE FROM word;
DELETE FROM category;

INSERT INTO users(id,username,password) VALUES (1,'testUser','pass');
INSERT INTO wordbook(id,name,user_id) VALUES(1,'wordbook1',1);
INSERT INTO category(id, name) VALUES(1, 'テストカテゴリ');
INSERT INTO word(id, word_name, content, category_id,wordbook_id)
VALUES(1, 'テストワード', 'テストコンテント', 1,1);
