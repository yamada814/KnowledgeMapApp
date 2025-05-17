
INSERT INTO users(id,username, password) VALUES(1,'testuser', 'pass');
INSERT INTO wordbook(id,name, user_id) VALUES(1,'testWordbook', 1);

INSERT INTO category(id,name, wordbook_id) VALUES(1,'testCategory', 1);
INSERT INTO word(id,word_name, content, category_id, wordbook_id) VALUES(1,'word1', 'content1', 1, 1);
INSERT INTO word(id,word_name, content, category_id, wordbook_id) VALUES(2,'word2', 'content1', 1, 1);

ALTER TABLE users ALTER COLUMN id RESTART WITH 2;
ALTER TABLE wordbook ALTER COLUMN id RESTART WITH 2;
ALTER TABLE category ALTER COLUMN id RESTART WITH 2;
ALTER TABLE word ALTER COLUMN id RESTART WITH 3;
