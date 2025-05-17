INSERT INTO users(id,username,password) VALUES (1,'testUser','pass');
INSERT INTO wordbook(id,name,user_id) VALUES(1,'wordbook1',1);
ALTER TABLE wordbook ALTER COLUMN id RESTART WITH 2;