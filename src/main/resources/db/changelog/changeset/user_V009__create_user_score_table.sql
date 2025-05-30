-- liqubase formatted sql
create table if not exists user_score (
	id bigint primary key generated always as identity unique,
	user_id bigint not null,
	score int not null default 0,

	constraint fk_user_id foreign key (user_id) references users(id)
);
--TODO: план запроса
--create index idx_user_score_userid_score_desc ON user_score(user_id, score DESC);