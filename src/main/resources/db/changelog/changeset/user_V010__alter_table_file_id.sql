ALTER TABLE users
ALTER COLUMN profile_pic_file_id TYPE bigint USING profile_pic_file_id::bigint,
ALTER COLUMN profile_pic_small_file_id TYPE bigint USING profile_pic_small_file_id::bigint;
