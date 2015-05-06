

DROP TABLE comments cascade;
DROP TABLE entries cascade;
DROP TABLE users  cascade;


CREATE TABLE entries
(
  id serial NOT NULL,
  title text NOT NULL,
  tags text,
  content text NOT NULL,
  content_html text NOT NULL,
  postdate timestamp with time zone NOT NULL DEFAULT now(),
  state smallint NOT NULL,
  CONSTRAINT entries_pkey PRIMARY KEY (id)
);


CREATE TABLE comments
(
  id serial NOT NULL,
  entryid integer NOT NULL,
  replyto integer,
  name text NOT NULL,
  content text NOT NULL,
  postdate timestamp with time zone NOT NULL DEFAULT now(),
  clientinfo text,
  state smallint NOT NULL,
  CONSTRAINT comments_pkey PRIMARY KEY (id)
);


CREATE TABLE users
(
  id serial NOT NULL,
  username text NOT NULL,
  password text NOT NULL,
  role text,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_username_key UNIQUE (username)
);

