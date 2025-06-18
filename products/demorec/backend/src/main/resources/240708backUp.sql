--
-- PostgreSQL database dump
--

-- Dumped from database version 15.7
-- Dumped by pg_dump version 15.7

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: __internal_key_value__; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.__internal_key_value__ (
    k character varying,
    value_bigint bigint,
    value_varchar character varying
);


ALTER TABLE public.__internal_key_value__ OWNER TO admin;

--
-- Name: assoc_battery_pack_battery_pack_all_components_demo_rec_compone; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_battery_pack_battery_pack_all_components_demo_rec_compone (
    battery_pack_id bigint NOT NULL,
    all_components_id bigint NOT NULL
);


ALTER TABLE public.assoc_battery_pack_battery_pack_all_components_demo_rec_compone OWNER TO admin;

--
-- Name: assoc_battery_pack_battery_pack_all_connections_join_connection; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_battery_pack_battery_pack_all_connections_join_connection (
    battery_pack_id bigint NOT NULL,
    all_connections_id bigint NOT NULL
);


ALTER TABLE public.assoc_battery_pack_battery_pack_all_connections_join_connection OWNER TO admin;

--
-- Name: assoc_battery_pack_inspected_instances_type_battery_pack_type; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_battery_pack_inspected_instances_type_battery_pack_type (
    inspected_instances_id bigint NOT NULL,
    type_id bigint NOT NULL
);


ALTER TABLE public.assoc_battery_pack_inspected_instances_type_battery_pack_type OWNER TO admin;

--
-- Name: assoc_demo_rec_component_connects_connected_by_join_connection; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_demo_rec_component_connects_connected_by_join_connection (
    connects_id bigint NOT NULL,
    connected_by_id bigint NOT NULL
);


ALTER TABLE public.assoc_demo_rec_component_connects_connected_by_join_connection OWNER TO admin;

--
-- Name: assoc_demo_rec_component_demo_rec_component_is_a_component_kind; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_demo_rec_component_demo_rec_component_is_a_component_kind (
    demo_rec_component_id bigint NOT NULL,
    is_a_id bigint NOT NULL
);


ALTER TABLE public.assoc_demo_rec_component_demo_rec_component_is_a_component_kind OWNER TO admin;

--
-- Name: assoc_demo_rec_component_demo_rec_component_photo_photo; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_demo_rec_component_demo_rec_component_photo_photo (
    demo_rec_component_id bigint NOT NULL,
    photo_id bigint NOT NULL
);


ALTER TABLE public.assoc_demo_rec_component_demo_rec_component_photo_photo OWNER TO admin;

--
-- Name: assoc_demo_rec_component_part_of_composed_of_join_connection; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_demo_rec_component_part_of_composed_of_join_connection (
    part_of_id bigint NOT NULL,
    composed_of_id bigint NOT NULL
);


ALTER TABLE public.assoc_demo_rec_component_part_of_composed_of_join_connection OWNER TO admin;

--
-- Name: assoc_measuring_value_measuring_value_dimension_dimension; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_measuring_value_measuring_value_dimension_dimension (
    measuring_value_id bigint NOT NULL,
    dimension_id bigint NOT NULL
);


ALTER TABLE public.assoc_measuring_value_measuring_value_dimension_dimension OWNER TO admin;

--
-- Name: assoc_process_process_consists_of_process_step; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_process_process_consists_of_process_step (
    process_id bigint NOT NULL,
    consists_of_id bigint NOT NULL
);


ALTER TABLE public.assoc_process_process_consists_of_process_step OWNER TO admin;

--
-- Name: assoc_process_process_deconstruction_of_battery_pack; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_process_process_deconstruction_of_battery_pack (
    process_id bigint NOT NULL,
    deconstruction_of_id bigint NOT NULL
);


ALTER TABLE public.assoc_process_process_deconstruction_of_battery_pack OWNER TO admin;

--
-- Name: assoc_process_step_process_step_deconstructs_join_connection; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_process_step_process_step_deconstructs_join_connection (
    process_step_id bigint NOT NULL,
    deconstructs_id bigint NOT NULL
);


ALTER TABLE public.assoc_process_step_process_step_deconstructs_join_connection OWNER TO admin;

--
-- Name: assoc_process_step_process_step_measured_measuring_value; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_process_step_process_step_measured_measuring_value (
    process_step_id bigint NOT NULL,
    measured_id bigint NOT NULL
);


ALTER TABLE public.assoc_process_step_process_step_measured_measuring_value OWNER TO admin;

--
-- Name: assoc_process_step_process_step_uses_end_effector; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.assoc_process_step_process_step_uses_end_effector (
    process_step_id bigint NOT NULL,
    uses_id bigint NOT NULL
);


ALTER TABLE public.assoc_process_step_process_step_uses_end_effector OWNER TO admin;

--
-- Name: battery_pack; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.battery_pack (
    id bigint NOT NULL,
    state_of_charge double precision NOT NULL,
    health double precision,
    name character varying NOT NULL,
    x_pos double precision,
    y_pos double precision,
    z_pos double precision,
    condition character varying DEFAULT 'HEALTHY'::character varying NOT NULL,
    condition_description character varying
);


ALTER TABLE public.battery_pack OWNER TO admin;

--
-- Name: battery_pack_type; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.battery_pack_type (
    id bigint NOT NULL,
    capacity double precision NOT NULL,
    voltage double precision NOT NULL,
    bms_location character varying NOT NULL,
    vin character varying NOT NULL,
    car_brand character varying,
    car_model character varying,
    car_year character varying
);


ALTER TABLE public.battery_pack_type OWNER TO admin;

--
-- Name: component_kind; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.component_kind (
    id bigint NOT NULL,
    name character varying NOT NULL,
    o_e_number character varying,
    human_description character varying,
    width double precision,
    length double precision,
    height double precision,
    weight double precision
);


ALTER TABLE public.component_kind OWNER TO admin;

--
-- Name: demo_rec_component; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.demo_rec_component (
    id bigint NOT NULL,
    name character varying NOT NULL,
    x_pos double precision,
    y_pos double precision,
    z_pos double precision,
    condition character varying DEFAULT 'HEALTHY'::character varying NOT NULL,
    condition_description character varying
);


ALTER TABLE public.demo_rec_component OWNER TO admin;

--
-- Name: dimension; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.dimension (
    id bigint NOT NULL,
    unit character varying NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE public.dimension OWNER TO admin;

--
-- Name: end_effector; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.end_effector (
    id bigint NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE public.end_effector OWNER TO admin;

--
-- Name: join_connection; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.join_connection (
    id bigint NOT NULL,
    join_connection_type character varying NOT NULL,
    x_pos double precision,
    y_pos double precision,
    z_pos double precision,
    condition character varying DEFAULT 'HEALTHY'::character varying NOT NULL,
    condition_description character varying
);


ALTER TABLE public.join_connection OWNER TO admin;

--
-- Name: measuring_value; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.measuring_value (
    id bigint NOT NULL,
    value character varying NOT NULL
);


ALTER TABLE public.measuring_value OWNER TO admin;

--
-- Name: photo; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.photo (
    id bigint NOT NULL,
    filename character varying NOT NULL,
    name character varying NOT NULL,
    size bigint DEFAULT 0 NOT NULL
);


ALTER TABLE public.photo OWNER TO admin;

--
-- Name: process; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.process (
    id bigint NOT NULL,
    started_at date NOT NULL
);


ALTER TABLE public.process OWNER TO admin;

--
-- Name: process_step; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.process_step (
    id bigint NOT NULL,
    time_consumption double precision NOT NULL,
    type character varying NOT NULL,
    description character varying,
    index integer NOT NULL
);


ALTER TABLE public.process_step OWNER TO admin;

--
-- Data for Name: __internal_key_value__; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.__internal_key_value__ (k, value_bigint, value_varchar) FROM stdin;
initialized	\N	true
gemid	570000	\N
\.


--
-- Data for Name: assoc_battery_pack_battery_pack_all_components_demo_rec_compone; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_battery_pack_battery_pack_all_components_demo_rec_compone (battery_pack_id, all_components_id) FROM stdin;
17	34
17	35
17	93
17	109
17	114
17	118
17	122
17	108
17	79
17	30013
17	94
17	71
17	30021
17	50018
380001	390011
380001	390013
380001	390023
380001	390025
380001	390029
\.


--
-- Data for Name: assoc_battery_pack_battery_pack_all_connections_join_connection; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_battery_pack_battery_pack_all_connections_join_connection (battery_pack_id, all_connections_id) FROM stdin;
17	33
17	44
17	47
17	63
17	78
17	92
17	106
17	113
17	117
17	121
17	125
17	131
17	133
17	30012
17	30020
17	50017
380001	390010
380001	390022
\.


--
-- Data for Name: assoc_battery_pack_inspected_instances_type_battery_pack_type; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_battery_pack_inspected_instances_type_battery_pack_type (inspected_instances_id, type_id) FROM stdin;
17	18
380001	370034
\.


--
-- Data for Name: assoc_demo_rec_component_connects_connected_by_join_connection; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_demo_rec_component_connects_connected_by_join_connection (connects_id, connected_by_id) FROM stdin;
34	33
35	33
34	44
35	44
34	47
35	47
34	63
35	63
71	78
79	78
93	92
94	92
108	106
109	106
114	113
35	113
35	117
118	117
118	121
122	121
35	125
122	125
35	131
122	131
35	133
79	133
35	30012
30013	30012
35	30020
30021	30020
50018	50017
109	50017
50018	390010
380001	390010
390013	390010
109	390010
390011	390010
\.


--
-- Data for Name: assoc_demo_rec_component_demo_rec_component_is_a_component_kind; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_demo_rec_component_demo_rec_component_is_a_component_kind (demo_rec_component_id, is_a_id) FROM stdin;
34	130001
35	130002
93	130003
109	130004
114	130005
118	130006
122	130007
108	130008
79	130009
30013	130010
94	130011
71	130012
30021	130013
50018	130014
17	130015
380001	380002
390011	390012
390013	390014
390023	390024
390025	390026
390029	390024
\.


--
-- Data for Name: assoc_demo_rec_component_demo_rec_component_photo_photo; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_demo_rec_component_demo_rec_component_photo_photo (demo_rec_component_id, photo_id) FROM stdin;
79	480001
108	480002
30013	480003
71	480004
17	480005
17	480006
34	480007
34	480008
34	480009
34	480010
34	480011
34	480012
34	480013
34	480014
34	480015
34	480016
35	480017
94	480018
50018	480019
30021	480020
122	480021
118	480022
\.


--
-- Data for Name: assoc_demo_rec_component_part_of_composed_of_join_connection; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_demo_rec_component_part_of_composed_of_join_connection (part_of_id, composed_of_id) FROM stdin;
17	33
17	44
17	47
17	63
17	78
17	92
17	106
17	113
17	117
35	121
17	125
17	131
17	133
17	30012
17	30020
17	50017
\.


--
-- Data for Name: assoc_measuring_value_measuring_value_dimension_dimension; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_measuring_value_measuring_value_dimension_dimension (measuring_value_id, dimension_id) FROM stdin;
43	0
45	0
62	0
83	0
102	0
111	0
144	137
\.


--
-- Data for Name: assoc_process_process_consists_of_process_step; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_process_process_consists_of_process_step (process_id, consists_of_id) FROM stdin;
16	31
16	42
16	46
16	61
16	65
16	82
16	88
16	101
16	105
16	112
16	129
16	132
16	30010
16	30018
16	50015
380000	390008
380000	390033
\.


--
-- Data for Name: assoc_process_process_deconstruction_of_battery_pack; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_process_process_deconstruction_of_battery_pack (process_id, deconstruction_of_id) FROM stdin;
16	17
380000	380001
\.


--
-- Data for Name: assoc_process_step_process_step_deconstructs_join_connection; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_process_step_process_step_deconstructs_join_connection (process_step_id, deconstructs_id) FROM stdin;
31	33
42	44
46	47
61	63
82	78
88	92
101	92
105	106
107	113
116	117
120	121
112	125
129	131
132	133
30010	30012
30018	30020
50015	50017
390008	390010
390033	390022
\.


--
-- Data for Name: assoc_process_step_process_step_measured_measuring_value; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_process_step_process_step_measured_measuring_value (process_step_id, measured_id) FROM stdin;
42	43
46	45
61	62
82	83
105	102
112	111
\.


--
-- Data for Name: assoc_process_step_process_step_uses_end_effector; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.assoc_process_step_process_step_uses_end_effector (process_step_id, uses_id) FROM stdin;
31	29
42	40
46	40
61	59
65	52
82	59
88	52
101	99
105	29
107	52
116	59
120	59
112	29
129	52
132	29
30010	59
30018	59
50015	59
390008	390006
390033	390006
\.


--
-- Data for Name: battery_pack; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.battery_pack (id, state_of_charge, health, name, x_pos, y_pos, z_pos, condition, condition_description) FROM stdin;
17	0	0	2024_2_22_VW_ID3:17	0	0	0	HEALTHY	\N
380001	0	100	2024_5_21:380001	0	0	0	HEALTHY	\N
\.


--
-- Data for Name: battery_pack_type; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.battery_pack_type (id, capacity, voltage, bms_location, vin, car_brand, car_model, car_year) FROM stdin;
18	77	388	Deckel musste komplett demontiert werden, um zu entladen	288VWAOMEM0073	VW	ID3	2021
370034	0	0	0	0	Renault	Zoe	20XX
\.


--
-- Data for Name: component_kind; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.component_kind (id, name, o_e_number, human_description, width, length, height, weight) FROM stdin;
130001	Deckel	0	\N	1400	1700	7	-1
130002	Rahmen	0	\N	1450	1830	130	0
130003	BMS	0	\N	0	0	0	1
130004	Modul	0	\N	0	0	0	0
130005	Kabel_Haupt_BMS_Sub_BMS	0	\N	0	0	0	1
130006	Schütz	0	\N	0	0	0	1
130007	Hauptstecker	0	\N	0	0	0	1
130008	SubBMS	0Z1915140D	\N	25	350	105	0.338
130009	Modul1	0	\N	590	225	110	30.2
130010	Master_BMS	0Z1915184E	\N	360	60	100	0.531
130011	Kabel_Modul_Sub_BMS	0	\N	0	0	0	24
130012	Modulverbinder	0Z1915139P	\N	20	130	30	75
130013	Schmelzsicherung	123	Schmelzsicherung	0	0	0	0
130014	Modulverbinder_lang	0	\N	20	155	28	0.9
130015	2024_2_22_VW_ID3:17	00	\N	140	1827	1450	497
390012	Renault Zoe Deckel	0	\N	0	0	0	0
390014	RenaultPackOhneDeckel	0	\N	0	0	0	0
390024	KühlungAnsauger	0	\N	0	0	0	0
390026	Renault Zoe Rahmen	0	\N	0	0	0	0
380002	Renault Zoe R135 Edition One	295105928R	\N	1265	1600	330	328
\.


--
-- Data for Name: demo_rec_component; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.demo_rec_component (id, name, x_pos, y_pos, z_pos, condition, condition_description) FROM stdin;
34	Deckel	0	0	0	HEALTHY	\N
35	Rahmen	0	0	0	HEALTHY	\N
93	BMS	0	0	0	HEALTHY	\N
109	Modul	0	0	0	HEALTHY	\N
114	Kabel_Haupt_BMS_Sub_BMS	0	0	0	HEALTHY	\N
118	Schütz	0	0	0	HEALTHY	\N
122	Hauptstecker	0	0	0	HEALTHY	\N
108	SubBMS	1255	650	-10	HEALTHY	\N
79	Modul1	160	40	-30	HEALTHY	\N
30013	Master_BMS	75	950	-28	HEALTHY	\N
94	Kabel_Modul_Sub_BMS	0	0	0	HEALTHY	\N
390011	Decker	0	0	0	HEALTHY	\N
390013	PackOhneDeckel	0	0	0	HEALTHY	\N
71	Modulverbinder	0	0	0	HEALTHY	\N
390023	1	0	0	0	HEALTHY	\N
390025	Rahmen	0	0	0	HEALTHY	\N
390029	BatteryVentilation	0	0	0	HEALTHY	\N
30021	Schmelzsicherung	0	0	0	HEALTHY	\N
50018	Modulverbinder_lang	831	610	-8	HEALTHY	\N
\.


--
-- Data for Name: dimension; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.dimension (id, unit, name) FROM stdin;
0	Nm	Torque
66	N	Force
137	kg	Weight
\.


--
-- Data for Name: end_effector; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.end_effector (id, name) FROM stdin;
28	Screw driver
40	Socket_24
52	Manuell
59	Screwdriver_Torx30
99	Pincers
141	Draht
29	Screwdriver_Torx25
390006	TBD
\.


--
-- Data for Name: join_connection; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.join_connection (id, join_connection_type, x_pos, y_pos, z_pos, condition, condition_description) FROM stdin;
33	SCREW	\N	\N	\N	HEALTHY	\N
44	SCREW	\N	\N	\N	HEALTHY	\N
47	SCREW	\N	\N	\N	HEALTHY	\N
63	SCREW	\N	\N	\N	HEALTHY	\N
78	SCREW	\N	\N	\N	HEALTHY	\N
92	PLUG	\N	\N	\N	HEALTHY	\N
106	SCREW	\N	\N	\N	HEALTHY	\N
113	PLUG	\N	\N	\N	HEALTHY	\N
117	SCREW	\N	\N	\N	HEALTHY	\N
121	SCREW	\N	\N	\N	HEALTHY	\N
125	SCREW	\N	\N	\N	HEALTHY	\N
131	PLUG	\N	\N	\N	HEALTHY	\N
133	SCREW	\N	\N	\N	HEALTHY	\N
30012	SCREW	\N	\N	\N	HEALTHY	\N
30020	SCREW	\N	\N	\N	HEALTHY	\N
50017	SCREW	\N	\N	\N	HEALTHY	\N
390010	SCREW	0	0	0	HEALTHY	\N
390022	SCREW	0	0	0	HEALTHY	\N
\.


--
-- Data for Name: measuring_value; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.measuring_value (id, value) FROM stdin;
43	120
45	280
62	10
83	0
102	5
111	5
144	200
\.


--
-- Data for Name: photo; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.photo (id, filename, name, size) FROM stdin;
480001	Modul1_79_image.jpg	Modul1_79_image.jpg	0
480002	SubBMS_108_image.jpg	SubBMS_108_image.jpg	0
480003	Master_BMS_30013_image.jpg	Master_BMS_30013_image.jpg	0
480004	Modulverbinder_71_image.jpg	Modulverbinder_71_image.jpg	0
480005	2024_2_22_VW_ID3_17_17_image.jpg	2024_2_22_VW_ID3_17_17_image.jpg	0
480006	2024_2_22_VW_ID3_17_17_IMG_4951.jpeg	2024_2_22_VW_ID3_17_17_IMG_4951.jpeg	0
480007	Deckel_34_IMG_4951.jpeg	Deckel_34_IMG_4951.jpeg	0
480017	Rahmen_35_2024_2_22_VW_ID3_17_17_image.jpg	Rahmen_35_2024_2_22_VW_ID3_17_17_image.jpg	0
480018	Kabel_Modul_Sub_BMS_94_IMG_4954.jpeg	Kabel_Modul_Sub_BMS_94_IMG_4954.jpeg	0
480019	Modulverbinder_lang_50018_image.jpg	Modulverbinder_lang_50018_image.jpg	0
480020	Schmelzsicherung_30021_image.jpg	Schmelzsicherung_30021_image.jpg	0
480021	Hauptstecker_122_image.jpg	Hauptstecker_122_image.jpg	0
480022	Sch_tz_118_image.jpg	Sch_tz_118_image.jpg	0
\.


--
-- Data for Name: process; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.process (id, started_at) FROM stdin;
16	2024-02-22
380000	2024-05-21
\.


--
-- Data for Name: process_step; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.process_step (id, time_consumption, type, description, index) FROM stdin;
31	-1	DECONSTRUCTION	\N	0
42	1	DECONSTRUCTION	\N	1
46	1	DECONSTRUCTION	\N	2
61	1	DECONSTRUCTION	\N	3
65	10	OTHER	Deckelabnahme	4
82	3	DECONSTRUCTION	Modulbrücken	5
88	14	DECONSTRUCTION	LV-Stecker klein	6
101	24	DECONSTRUCTION	3 Kabel= 6 Stecker 	7
107	2	DECONSTRUCTION	Rausreißen	9
116	10	DECONSTRUCTION		10
120	10	DECONSTRUCTION	\N	11
129	180	DECONSTRUCTION	Rahmen Hauptstecker	10
50015	0	DECONSTRUCTION	Modulverbinder_lang Modul	14
390008	2.4	DECONSTRUCTION	\N	0
390033	10	DECONSTRUCTION	\N	1
132	5	DECONSTRUCTION	RahmenModul 	11
105	2	DECONSTRUCTION	Sub BMS Modul	8
30010	20	DECONSTRUCTION	Rahmen MasterBMS	12
112	5	DECONSTRUCTION	Rahmen Hauptstecker	9
30018	5	DECONSTRUCTION	RahmenSchmelzsicherung	13
\.


--
-- Name: assoc_battery_pack_battery_pack_all_components_demo_rec_compone pk_assoc_battery_pack_battery_pack_all_components_demo_rec_comp; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_battery_pack_battery_pack_all_components_demo_rec_compone
    ADD CONSTRAINT pk_assoc_battery_pack_battery_pack_all_components_demo_rec_comp PRIMARY KEY (battery_pack_id, all_components_id);


--
-- Name: assoc_battery_pack_battery_pack_all_connections_join_connection pk_assoc_battery_pack_battery_pack_all_connections_join_connect; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_battery_pack_battery_pack_all_connections_join_connection
    ADD CONSTRAINT pk_assoc_battery_pack_battery_pack_all_connections_join_connect PRIMARY KEY (battery_pack_id, all_connections_id);


--
-- Name: assoc_battery_pack_inspected_instances_type_battery_pack_type pk_assoc_battery_pack_inspected_instances_type_battery_pack_typ; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_battery_pack_inspected_instances_type_battery_pack_type
    ADD CONSTRAINT pk_assoc_battery_pack_inspected_instances_type_battery_pack_typ PRIMARY KEY (inspected_instances_id, type_id);


--
-- Name: assoc_demo_rec_component_connects_connected_by_join_connection pk_assoc_demo_rec_component_connects_connected_by_join_connecti; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_demo_rec_component_connects_connected_by_join_connection
    ADD CONSTRAINT pk_assoc_demo_rec_component_connects_connected_by_join_connecti PRIMARY KEY (connects_id, connected_by_id);


--
-- Name: assoc_demo_rec_component_part_of_composed_of_join_connection pk_assoc_demo_rec_component_demo_rec_component_composed_of_join; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_demo_rec_component_part_of_composed_of_join_connection
    ADD CONSTRAINT pk_assoc_demo_rec_component_demo_rec_component_composed_of_join PRIMARY KEY (part_of_id, composed_of_id);


--
-- Name: assoc_demo_rec_component_demo_rec_component_is_a_component_kind pk_assoc_demo_rec_component_demo_rec_component_is_a_component_k; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_demo_rec_component_demo_rec_component_is_a_component_kind
    ADD CONSTRAINT pk_assoc_demo_rec_component_demo_rec_component_is_a_component_k PRIMARY KEY (demo_rec_component_id, is_a_id);


--
-- Name: assoc_demo_rec_component_demo_rec_component_photo_photo pk_assoc_demo_rec_component_demo_rec_component_photo_photo; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_demo_rec_component_demo_rec_component_photo_photo
    ADD CONSTRAINT pk_assoc_demo_rec_component_demo_rec_component_photo_photo PRIMARY KEY (demo_rec_component_id, photo_id);


--
-- Name: assoc_measuring_value_measuring_value_dimension_dimension pk_assoc_measuring_value_measuring_value_dimension_dimension; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_measuring_value_measuring_value_dimension_dimension
    ADD CONSTRAINT pk_assoc_measuring_value_measuring_value_dimension_dimension PRIMARY KEY (measuring_value_id, dimension_id);


--
-- Name: assoc_process_process_consists_of_process_step pk_assoc_process_process_consists_of_process_step; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_process_process_consists_of_process_step
    ADD CONSTRAINT pk_assoc_process_process_consists_of_process_step PRIMARY KEY (process_id, consists_of_id);


--
-- Name: assoc_process_process_deconstruction_of_battery_pack pk_assoc_process_process_deconstruction_of_battery_pack; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_process_process_deconstruction_of_battery_pack
    ADD CONSTRAINT pk_assoc_process_process_deconstruction_of_battery_pack PRIMARY KEY (process_id, deconstruction_of_id);


--
-- Name: assoc_process_step_process_step_deconstructs_join_connection pk_assoc_process_step_process_step_deconstructs_join_connection; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_process_step_process_step_deconstructs_join_connection
    ADD CONSTRAINT pk_assoc_process_step_process_step_deconstructs_join_connection PRIMARY KEY (process_step_id, deconstructs_id);


--
-- Name: assoc_process_step_process_step_measured_measuring_value pk_assoc_process_step_process_step_measured_measuring_value; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_process_step_process_step_measured_measuring_value
    ADD CONSTRAINT pk_assoc_process_step_process_step_measured_measuring_value PRIMARY KEY (process_step_id, measured_id);


--
-- Name: assoc_process_step_process_step_uses_end_effector pk_assoc_process_step_process_step_uses_end_effector; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.assoc_process_step_process_step_uses_end_effector
    ADD CONSTRAINT pk_assoc_process_step_process_step_uses_end_effector PRIMARY KEY (process_step_id, uses_id);


--
-- Name: battery_pack pk_battery_pack; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.battery_pack
    ADD CONSTRAINT pk_battery_pack PRIMARY KEY (id);


--
-- Name: battery_pack_type pk_battery_pack_type; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.battery_pack_type
    ADD CONSTRAINT pk_battery_pack_type PRIMARY KEY (id);


--
-- Name: component_kind pk_component_kind; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.component_kind
    ADD CONSTRAINT pk_component_kind PRIMARY KEY (id);


--
-- Name: demo_rec_component pk_demo_rec_component; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.demo_rec_component
    ADD CONSTRAINT pk_demo_rec_component PRIMARY KEY (id);


--
-- Name: dimension pk_dimension; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dimension
    ADD CONSTRAINT pk_dimension PRIMARY KEY (id);


--
-- Name: end_effector pk_end_effector; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.end_effector
    ADD CONSTRAINT pk_end_effector PRIMARY KEY (id);


--
-- Name: join_connection pk_join_connection; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.join_connection
    ADD CONSTRAINT pk_join_connection PRIMARY KEY (id);


--
-- Name: measuring_value pk_measuring_value; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.measuring_value
    ADD CONSTRAINT pk_measuring_value PRIMARY KEY (id);


--
-- Name: photo pk_photo; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.photo
    ADD CONSTRAINT pk_photo PRIMARY KEY (id);


--
-- Name: process pk_process; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.process
    ADD CONSTRAINT pk_process PRIMARY KEY (id);


--
-- Name: process_step pk_process_step; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.process_step
    ADD CONSTRAINT pk_process_step PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

