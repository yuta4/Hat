delete from word where trim(string) = '';
DROP sequence word_seq;
create sequence word_seq start 1 increment 50;
SELECT SETVAL('word_seq',MAX(id)+1) FROM word;