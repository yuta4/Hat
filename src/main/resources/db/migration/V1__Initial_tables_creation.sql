create sequence game_seq start 1 increment 50;
create sequence game_word_seq start 1 increment 50;
create sequence player_seq start 1 increment 1;
create sequence team_seq start 1 increment 50;
create sequence word_seq start 1 increment 1;

create table game (
    id int8 not null,
    allow_skip_words boolean not null,
    game_progress varchar(255),
    is_active boolean,
    paused_time_remains int8,
    turn_end_time timestamp,
    turn_status varchar(255),
    words_per_player int4,
    owner_id int8,
    team_turn_id int8,
    turn_current_guessing_id int8,
    primary key (id));

create table game_watcher (
    game_id int8 not null,
    player_id int8 not null,
    primary key (game_id, player_id));

create table game_words_language (
    game_id int8 not null,
    words_language varchar(255));

create table game_words_level (
    game_id int8 not null,
    words_level varchar(255));

create table game_word (
    id int8 not null,
    current_turn_guessed boolean,
    game_id int8,
    team_id int8,
    word_id int8,
    primary key (id));

create table player (
    id int8 not null,
    email varchar(255) not null,
    login varchar(255) not null,
    name varchar(255) not null,
    password varchar(255),
    last_game_id int8,
    primary key (id));

create table team (
    id int8 not null,
    name varchar(255),
    score int8,
    game_id int8,
    player_turn_id int8,
    primary key (id));

create table team_players (
    team_id int8 not null,
    player_id int8 not null,
    primary key (team_id, player_id));

create table word (
    id int8 not null,
    language varchar(255) not null,
    level varchar(255) not null,
    string varchar(255) not null,
    used int4 not null,
    primary key (id));

alter table game_watcher add constraint
uk_game_watcher_player_id unique (player_id);

alter table player add constraint
uk_player_email unique (email);

alter table player add constraint
uk_player_login unique (login);

alter table game add constraint
fk_game_owner_id foreign key (owner_id) references player (id);

alter table game add constraint
fk_game_team_turn_id foreign key (team_turn_id) references team (id);

alter table game add constraint
fk_game_turn_current_guessing_id foreign key (turn_current_guessing_id) references game_word (id);

alter table game_watcher add constraint
fk_game_watcher_player_id foreign key (player_id) references player (id);

alter table game_watcher add constraint
fk_game_watcher_game_id foreign key (game_id) references game (id);

alter table game_words_language add constraint
fk_game_words_language_game_id foreign key (game_id) references game (id);

alter table game_words_level add constraint
fk_game_words_level_game_id foreign key (game_id) references game (id);

alter table game_word add constraint
fk_game_word_game_id foreign key (game_id) references game (id);

alter table game_word add constraint
fk_game_word_team_id foreign key (team_id) references team (id);

alter table game_word add constraint
fk_game_word_word_id foreign key (word_id) references word (id);

alter table player add constraint
fk_player_last_game_id foreign key (last_game_id) references game (id);

alter table team add constraint
fk_team_game_id foreign key (game_id) references game (id);

alter table team add constraint
fk_team_player_turn_id foreign key (player_turn_id) references player (id);

alter table team_players add constraint
fk_team_players_player_id foreign key (player_id) references player (id);

alter table team_players add constraint
fk_team_players_team_id foreign key (team_id) references team (id);