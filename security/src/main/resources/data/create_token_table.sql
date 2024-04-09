-- public."token" definition

-- Drop table

-- DROP TABLE public."token";

CREATE TABLE public."token" (
	expired bool NOT NULL,
	revoked bool NOT NULL,
	id int8 NOT NULL,
	user_id int8 NULL,
	"token" varchar(255) NULL,
	token_type varchar(255) NULL,
	CONSTRAINT token_pkey PRIMARY KEY (id),
	CONSTRAINT token_token_key UNIQUE (token),
	CONSTRAINT token_token_type_check CHECK (((token_type)::text = 'BEARER'::text))
);

-- Permissions

ALTER TABLE public."token" OWNER TO postgres;
GRANT ALL ON TABLE public."token" TO postgres;


-- public."token" foreign keys

ALTER TABLE public."token" ADD CONSTRAINT fkiblu4cjwvyntq3ugo31klp1c6 FOREIGN KEY (user_id) REFERENCES public."_user"(id);