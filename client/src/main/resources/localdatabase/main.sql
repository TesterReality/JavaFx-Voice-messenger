
-- ----------------------------
-- Table structure for msg_history
-- ----------------------------
DROP TABLE IF EXISTS "msg_history";
CREATE TABLE "msg_history" (
  "id_msg" INTEGER NOT NULL,
  "id_msg_key" integer NOT NULL,
  "msg_text" TEXT NOT NULL,
  "msg_date" TEXT NOT NULL,
  PRIMARY KEY ("id_msg"),
  FOREIGN KEY ("id_msg_key") REFERENCES "msg_key_friend" ("id_msg_key") ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- ----------------------------
-- Table structure for msg_key_friend
-- ----------------------------
DROP TABLE IF EXISTS "msg_key_friend";
CREATE TABLE "msg_key_friend" (
  "id_msg_key" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "id_user_name" integer NOT NULL,
  "msg_key" TEXT,
  FOREIGN KEY ("id_user_name") REFERENCES "user_friend" ("id_user_friend") ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- ----------------------------
-- Table structure for sqlite_sequence
-- ----------------------------
DROP TABLE IF EXISTS "sqlite_sequence";
CREATE TABLE "sqlite_sequence" (
  "name",
  "seq"
);

-- ----------------------------
-- Table structure for user_friend
-- ----------------------------
DROP TABLE IF EXISTS "user_friend";
CREATE TABLE "user_friend" (
  "id_user_friend" integer NOT NULL,
  "user_name" TEXT NOT NULL,
  "user_contact" integer NOT NULL,
  PRIMARY KEY ("id_user_friend")
);

-- ----------------------------
-- Table structure for user_voice_key
-- ----------------------------
DROP TABLE IF EXISTS "user_voice_key";
CREATE TABLE "user_voice_key" (
  "id_voice" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "id_friend" integer NOT NULL,
  "key_my" TEXT,
  "key_friend" TEXT,
  "secret_key1" TEXT,
  "secret_key2" TEXT,
  CONSTRAINT "id" FOREIGN KEY ("id_friend") REFERENCES "user_friend" ("id_user_friend") ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- ----------------------------
-- Auto increment value for msg_key_friend
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 1 WHERE name = 'msg_key_friend';

-- ----------------------------
-- Auto increment value for user_voice_key
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 7 WHERE name = 'user_voice_key';

PRAGMA foreign_keys = true;
