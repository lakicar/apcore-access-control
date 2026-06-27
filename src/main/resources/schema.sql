--
-- PostgreSQL database dump
--

\restrict jrCwsIcUzXwf2G37WZBlWFEFWj7cVrjLJ4auZAHdc2fC5uWW6jVYzQ3cV8pY8ze

-- Dumped from database version 17.10 (Homebrew)
-- Dumped by pg_dump version 17.10 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: client; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.client (
    client_id bigint NOT NULL,
    tax_id character varying(20) NOT NULL,
    registration_number character varying(20),
    name character varying(255) NOT NULL,
    address character varying(255),
    city character varying(100),
    postal_code character varying(20),
    legal_form character varying(100),
    status character varying(50),
    email character varying(255),
    phone character varying(50),
    data_source character varying(100),
    sync_date date,
    sync_time time without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.client OWNER TO admin;

--
-- Name: client_client_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.client_client_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.client_client_id_seq OWNER TO admin;

--
-- Name: client_client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.client_client_id_seq OWNED BY public.client.client_id;


--
-- Name: client_user; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.client_user (
    client_user_id bigint NOT NULL,
    client_id bigint NOT NULL,
    user_id bigint NOT NULL,
    role_code character varying(50) NOT NULL,
    active boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.client_user OWNER TO admin;

--
-- Name: client_user_client_user_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.client_user_client_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.client_user_client_user_id_seq OWNER TO admin;

--
-- Name: client_user_client_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.client_user_client_user_id_seq OWNED BY public.client_user.client_user_id;


--
-- Name: user_account; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.user_account (
    user_id bigint NOT NULL,
    username character varying(100) NOT NULL,
    email character varying(255) NOT NULL,
    password_hash text NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    date_of_birth date,
    phone character varying(50),
    active boolean DEFAULT true NOT NULL,
    last_login_at timestamp without time zone,
    failed_login_attempts integer DEFAULT 0 NOT NULL,
    locked_until timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.user_account OWNER TO admin;

--
-- Name: user_account_user_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.user_account_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_account_user_id_seq OWNER TO admin;

--
-- Name: user_account_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.user_account_user_id_seq OWNED BY public.user_account.user_id;


--
-- Name: user_permission; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.user_permission (
    user_permission_id bigint NOT NULL,
    client_user_id bigint NOT NULL,
    permission_code character varying(100) NOT NULL,
    permission_name character varying(255),
    allowed boolean DEFAULT true NOT NULL,
    granted_by_client_user_id bigint,
    granted_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.user_permission OWNER TO admin;

--
-- Name: user_permission_user_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.user_permission_user_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_permission_user_permission_id_seq OWNER TO admin;

--
-- Name: user_permission_user_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.user_permission_user_permission_id_seq OWNED BY public.user_permission.user_permission_id;


--
-- Name: client client_id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client ALTER COLUMN client_id SET DEFAULT nextval('public.client_client_id_seq'::regclass);


--
-- Name: client_user client_user_id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client_user ALTER COLUMN client_user_id SET DEFAULT nextval('public.client_user_client_user_id_seq'::regclass);


--
-- Name: user_account user_id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_account ALTER COLUMN user_id SET DEFAULT nextval('public.user_account_user_id_seq'::regclass);


--
-- Name: user_permission user_permission_id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_permission ALTER COLUMN user_permission_id SET DEFAULT nextval('public.user_permission_user_permission_id_seq'::regclass);


--
-- Name: client client_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT client_pkey PRIMARY KEY (client_id);


--
-- Name: client client_tax_id_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT client_tax_id_key UNIQUE (tax_id);


--
-- Name: client_user client_user_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client_user
    ADD CONSTRAINT client_user_pkey PRIMARY KEY (client_user_id);


--
-- Name: client_user uq_client_user; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client_user
    ADD CONSTRAINT uq_client_user UNIQUE (client_id, user_id);


--
-- Name: user_permission uq_user_permission; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_permission
    ADD CONSTRAINT uq_user_permission UNIQUE (client_user_id, permission_code);


--
-- Name: user_account user_account_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_account
    ADD CONSTRAINT user_account_email_key UNIQUE (email);


--
-- Name: user_account user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_account
    ADD CONSTRAINT user_account_pkey PRIMARY KEY (user_id);


--
-- Name: user_account user_account_username_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_account
    ADD CONSTRAINT user_account_username_key UNIQUE (username);


--
-- Name: user_permission user_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_permission
    ADD CONSTRAINT user_permission_pkey PRIMARY KEY (user_permission_id);


--
-- Name: client_user fk_client_user_client; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client_user
    ADD CONSTRAINT fk_client_user_client FOREIGN KEY (client_id) REFERENCES public.client(client_id) ON DELETE CASCADE;


--
-- Name: client_user fk_client_user_user_account; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.client_user
    ADD CONSTRAINT fk_client_user_user_account FOREIGN KEY (user_id) REFERENCES public.user_account(user_id) ON DELETE CASCADE;


--
-- Name: user_permission fk_user_permission_client_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_permission
    ADD CONSTRAINT fk_user_permission_client_user FOREIGN KEY (client_user_id) REFERENCES public.client_user(client_user_id) ON DELETE CASCADE;


--
-- Name: user_permission fk_user_permission_granted_by; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.user_permission
    ADD CONSTRAINT fk_user_permission_granted_by FOREIGN KEY (granted_by_client_user_id) REFERENCES public.client_user(client_user_id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

\unrestrict jrCwsIcUzXwf2G37WZBlWFEFWj7cVrjLJ4auZAHdc2fC5uWW6jVYzQ3cV8pY8ze

