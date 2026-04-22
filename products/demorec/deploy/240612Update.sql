-- Component Kind Changes
ALTER TABLE component_kind ALTER COLUMN o_e_number DROP NOT NULL;
ALTER TABLE component_kind ALTER COLUMN width DROP NOT NULL;
ALTER TABLE component_kind ALTER COLUMN length DROP NOT NULL;
ALTER TABLE component_kind ALTER COLUMN height DROP NOT NULL;
ALTER TABLE component_kind ALTER COLUMN weight DROP NOT NULL;

-- Join Connection Changes
ALTER TABLE join_connection DROP COLUMN state;
ALTER TABLE join_connection
ADD COLUMN "condition" VARCHAR NOT NULL DEFAULT 'HEALTHY',
ADD COLUMN "condition_description" VARCHAR;

-- Photos
CREATE TABLE "photo"(
  id BIGINT NOT NULL,
  CONSTRAINT pk_photo PRIMARY KEY (id)
  ,"filename" VARCHAR NOT NULL
  ,"name" VARCHAR NOT NULL
  ,"size" BIGINT NOT NULL DEFAULT 0
);

-- Photo Assoc
CREATE TABLE assoc_demo_rec_component_demo_rec_component_photo_photo(
  demo_rec_component_id BIGINT NOT NULL,
  photo_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_demo_rec_component_demo_rec_component_photo_photo PRIMARY KEY (demo_rec_component_id, photo_id)
);

-- Create GEM ID Sequence
CREATE SEQUENCE gem_id_seq AS BIGINT MINVALUE 480001;

-- Create Photos based on Photo Lists
INSERT INTO "photo" ("id", "name","filename")
SELECT
    nextval('gem_id_seq'),
    "_value",
	"_value"
FROM
    "demo_rec_component$photo_locations";

-- For each Photo connect it to its DemoRec Component
INSERT INTO assoc_demo_rec_component_demo_rec_component_photo_photo (demo_rec_component_id, photo_id)
SELECT
    drc.id AS demo_rec_component_id,
    p.id AS photo_id
FROM
    "demo_rec_component$photo_locations" AS drc
JOIN
    "photo" AS p ON drc._value = p.name;

-- 	Delete Photo and 3D Location Lists
DROP TABLE demo_rec_component$photo_locations;
DROP TABLE demo_rec_component$three_d_file_locations;


-- DemoRecComponent make position optional
ALTER TABLE demo_rec_component ALTER COLUMN x_pos DROP NOT NULL;
ALTER TABLE demo_rec_component ALTER COLUMN y_pos DROP NOT NULL;
ALTER TABLE demo_rec_component ALTER COLUMN z_pos DROP NOT NULL;

-- Same for Battery Pack
ALTER TABLE battery_pack ALTER COLUMN x_pos DROP NOT NULL;
ALTER TABLE battery_pack ALTER COLUMN y_pos DROP NOT NULL;
ALTER TABLE battery_pack ALTER COLUMN z_pos DROP NOT NULL;


-- DemoRecComponent create condition and condition description
ALTER TABLE demo_rec_component
DROP COLUMN "condition";
ALTER TABLE demo_rec_component
ADD COLUMN IF NOT EXISTS "condition" VARCHAR NOT NULL DEFAULT 'HEALTHY',
ADD COLUMN IF NOT EXISTS "condition_description" VARCHAR;

-- Same for Battery Pack
ALTER TABLE battery_pack
DROP COLUMN "condition";
ALTER TABLE battery_pack
ADD COLUMN IF NOT EXISTS "condition" VARCHAR NOT NULL DEFAULT 'HEALTHY',
ADD COLUMN IF NOT EXISTS "condition_description" VARCHAR;

-- Create new associations
CREATE TABLE assoc_battery_pack_battery_pack_all_components_demo_rec_component(
  battery_pack_id BIGINT NOT NULL,
  all_components_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_battery_pack_battery_pack_all_components_demo_rec_component PRIMARY KEY (battery_pack_id, all_components_id)
);
CREATE TABLE assoc_battery_pack_battery_pack_all_connections_join_connection(
  battery_pack_id BIGINT NOT NULL,
  all_connections_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_battery_pack_battery_pack_all_connections_join_connection PRIMARY KEY (battery_pack_id, all_connections_id)
);

-- Connect Components to their Packs // We only have two packs currently so this is pretty much handwritten
INSERT INTO assoc_battery_pack_battery_pack_all_components_demo_rec_component (battery_pack_id, all_components_id)
SELECT
	17 AS battery_pack_id,
    drc.id AS component_id
FROM
    "demo_rec_component" AS drc
WHERE
	drc.id < 380000;

INSERT INTO assoc_battery_pack_battery_pack_all_components_demo_rec_component (battery_pack_id, all_components_id)
SELECT
	380001 AS battery_pack_id,
    drc.id AS all_components_id
FROM
    "demo_rec_component" AS drc
WHERE
	drc.id >= 380000;

-- Connect JoinConnections to their Packs // We only have two packs currently so this is pretty much handwritten
INSERT INTO assoc_battery_pack_battery_pack_all_connections_join_connection (battery_pack_id, all_connections_id)
SELECT
	17 AS battery_pack_id,
    drc.id AS all_connections_id
FROM
    "join_connection" AS drc
WHERE
	drc.id < 380000;

INSERT INTO assoc_battery_pack_battery_pack_all_connections_join_connection (battery_pack_id, all_connections_id)
SELECT
	380001 AS battery_pack_id,
    drc.id AS all_connections_id
FROM
    "join_connection" AS drc
WHERE
	drc.id >= 380000;

-- Delete Gem ID Sequence and remove it from table
ALTER TABLE "photo" ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE gem_id_seq;
