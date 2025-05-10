
INSERT INTO users(id, username, password) VALUES(1, 'testuser', 'pass');
INSERT INTO wordbook(id, name, user_id) VALUES(1, 'testWordbook', 1);

INSERT INTO category(name, wordbook_id) VALUES('testCategory', 1);
INSERT INTO word(word_name, content, category_id, wordbook_id) VALUES('word1', 'content1', 1, 1);
