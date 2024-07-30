-- Remove connection_id later
CREATE TABLE "join_connection_kind"(id BIGINT NOT NULL, CONSTRAINT pk_join_connection_kind PRIMARY KEY (id),"join_connection_type" VARCHAR NOT NULL, "connection_id" BIGINT);

-- Component-ConnectionKind Assoc: Has to be renamed later on
CREATE TABLE connects_connected_by_join_connection_kind(connects_id BIGINT NOT NULL, connected_by_id BIGINT NOT NULL);

-- Kind-Connection
CREATE TABLE assoc_join_connection_join_connection_join_connection_kind_join_connection_kind(join_connection_id BIGINT NOT NULL, join_connection_kind_id BIGINT NOT NULL, CONSTRAINT pk_assoc_join_connection_join_connection_join_connection_kind_join_connection_kind PRIMARY KEY (join_connection_id, join_connection_kind_id));

-- Kind-Pack Assoc: Has to be renamed later on
CREATE TABLE rename_pack_kind(battery_pack_id BIGINT NOT NULL, all_connections_id BIGINT NOT NULL);

-- Create GEM ID Sequence
CREATE SEQUENCE gem_id_seq AS BIGINT MINVALUE 480001;

-- Create Join Connection Kinds
INSERT INTO "join_connection_kind" ("id", "join_connection_type", "connection_id") SELECT nextval('gem_id_seq'), "join_connection_type", "id" FROM join_connection;

-- Connect join_connection_kinds to join_connections
INSERT INTO assoc_join_connection_join_connection_join_connection_kind_join_connection_kind (join_connection_id, join_connection_kind_id) SELECT kind.connection_id AS join_connection_kind_id, kind.id AS join_connection_id FROM join_connection_kind AS kind;

-- Connect join_connection_kinds to components
INSERT INTO connects_connected_by_join_connection_kind (connects_id, connected_by_id) SELECT component.connects_id AS connects_id, kind.id AS connected_by_id  FROM join_connection_kind AS kind JOIN assoc_demo_rec_component_connects_connected_by_join_connection AS component ON kind.connection_id=component.connected_by_id;

-- Connect join_connection_kinds to batterypack
INSERT INTO rename_pack_kind (battery_pack_id, all_connections_id) SELECT pack.battery_pack_id AS battery_pack_id, kind.id AS all_connections_id FROM join_connection_kind AS kind JOIN assoc_battery_pack_battery_pack_all_connections_join_connection AS pack ON kind.connection_id=pack.all_connections_id;

-- delete connection <-> component table
DROP TABLE assoc_demo_rec_component_connects_connected_by_join_connection;

-- Rename Table Component-ConnectionKind
ALTER TABLE connects_connected_by_join_connection_kind RENAME TO assoc_demo_rec_component_connects_connected_by_join_connection_kind;

-- delete pack <-> connection table
DROP TABLE assoc_battery_pack_battery_pack_all_connections_join_connection;

-- Rename Kind-Pack table
ALTER TABLE rename_pack_kind RENAME TO assoc_battery_pack_battery_pack_all_connections_join_connection_kind;

-- Drop connection_id from join_connection_kind
ALTER TABLE join_connection_kind DROP COLUMN connection_id;

-- Drop connection_id from join_connection_kind
ALTER TABLE join_connection DROP COLUMN join_connection_type;

-- Add primary key constraints
ALTER TABLE assoc_demo_rec_component_connects_connected_by_join_connection_kind ADD CONSTRAINT pk_assoc_demo_rec_component_connects_connected_by_join_connection_kind PRIMARY KEY (connects_id, connected_by_id);
ALTER TABLE assoc_battery_pack_battery_pack_all_connections_join_connection_kind ADD CONSTRAINT pk_assoc_battery_pack_battery_pack_all_connections_join_connection_kind PRIMARY KEY (battery_pack_id, all_connections_id);
