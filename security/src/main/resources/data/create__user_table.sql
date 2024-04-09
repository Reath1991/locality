-- public."_user" definition

-- Drop table

-- DROP TABLE public."_user";

CREATE TABLE public."_user" (
	id int8 NOT NULL,
	email varchar(255) NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL,
	mobile varchar(255) NULL,
	"password" varchar(255) NULL,
	"role" varchar(255) NULL,
	user_name varchar(255) NULL,
	CONSTRAINT "_user_email_key" UNIQUE (email),
	CONSTRAINT "_user_mobile_key" UNIQUE (mobile),
	CONSTRAINT "_user_pkey" PRIMARY KEY (id),
	CONSTRAINT "_user_role_check" CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'USER'::character varying, 'SUSCRIBER'::character varying])::text[]))),
	CONSTRAINT "_user_user_name_key" UNIQUE (user_name)
);

-- Permissions

ALTER TABLE public."_user" OWNER TO postgres;
GRANT ALL ON TABLE public."_user" TO postgres;


