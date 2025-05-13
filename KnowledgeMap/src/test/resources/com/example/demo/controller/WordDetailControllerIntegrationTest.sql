
INSERT INTO users(id,username,password) VALUES (1,'testUser','pass');
INSERT INTO wordbook(id,name,user_id) VALUES(1,'wordbook1',1);
INSERT INTO category(id, name) VALUES(1, 'テストカテゴリ');
INSERT INTO word(id, word_name, content, category_id,wordbook_id)
VALUES(1, 'テストワード1', 'テストコンテント1', 1, 1);
INSERT INTO word(id, word_name, content, category_id,wordbook_id)
VALUES(2, 'テストワード2', 'テストコンテント2', 1, 1);
INSERT INTO word(id, word_name, content, category_id,wordbook_id)
VALUES(3, 'テストワード3', 'テストコンテント3', 1, 1);

