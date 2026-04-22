-- Create GEM ID Sequence
CREATE SEQUENCE gem_id_seq AS BIGINT MINVALUE 130001;

-- Create ComponentKind table and isA assoc
CREATE TABLE "component_kind"(
                               id BIGINT NOT NULL,
                               CONSTRAINT pk_component_kind PRIMARY KEY (id)
  ,"name" VARCHAR NOT NULL
  ,"o_e_number" VARCHAR NOT NULL
  ,"human_description" VARCHAR,"width" DOUBLE PRECISION NOT NULL
  ,"length" DOUBLE PRECISION NOT NULL
  ,"height" DOUBLE PRECISION NOT NULL
  ,"weight" DOUBLE PRECISION NOT NULL
);

CREATE TABLE assoc_demo_rec_component_demo_rec_component_is_a_component_kind(
                                                                              demo_rec_component_id BIGINT NOT NULL,
                                                                              is_a_id BIGINT NOT NULL,
                                                                              CONSTRAINT pk_assoc_demo_rec_component_demo_rec_component_is_a_component_kind PRIMARY KEY (demo_rec_component_id, is_a_id)
);

-- For each DemoRecComponent create a ComponentKind

INSERT INTO "component_kind" ("id", "name","o_e_number","human_description","width", "length", "height", "weight")
SELECT
  nextval('gem_id_seq'),
  "name",
  "o_e_number",
  "human_description",
  "width", -- You need to provide appropriate values for width, length, height, and weight
  "length",
  "height",
  "weight"
FROM
  "demo_rec_component";


-- For each DemoRecComponent add its ComponentKind based their name
INSERT INTO assoc_demo_rec_component_demo_rec_component_is_a_component_kind (demo_rec_component_id, is_a_id)
SELECT
  drc.id AS demo_rec_component_id,
  ck.id AS is_a_id
FROM
  "demo_rec_component" AS drc
    JOIN
  "component_kind" AS ck ON drc.name = ck.name;


-- Drop Columns from Demorec Component
ALTER TABLE demo_rec_component DROP COLUMN "o_e_number";
ALTER TABLE demo_rec_component DROP COLUMN "human_description";
ALTER TABLE demo_rec_component DROP COLUMN "width";
ALTER TABLE demo_rec_component DROP COLUMN "length";
ALTER TABLE demo_rec_component DROP COLUMN "height";
ALTER TABLE demo_rec_component DROP COLUMN "weight";


-- Same as above for BatteryPacks: For each BatteryPack create a ComponentKind

INSERT INTO "component_kind" ("id", "name","o_e_number","human_description","width", "length", "height", "weight")
SELECT
  nextval('gem_id_seq'),
  "name",
  "o_e_number",
  "human_description",
  "width", -- You need to provide appropriate values for width, length, height, and weight
  "length",
  "height",
  "weight"
FROM
  "battery_pack";


-- Same as above for BatteryPacks: For each DemoRecComponent add its ComponentKind based their name
INSERT INTO assoc_demo_rec_component_demo_rec_component_is_a_component_kind (demo_rec_component_id, is_a_id)
SELECT
  drc.id AS demo_rec_component_id,
  ck.id AS is_a_id
FROM
  "battery_pack" AS drc
    JOIN
  "component_kind" AS ck ON drc.name = ck.name;

-- Same as above for BatteryPacks: Drop Columns from Demorec Component
ALTER TABLE battery_pack DROP COLUMN "o_e_number";
ALTER TABLE battery_pack DROP COLUMN "human_description";
ALTER TABLE battery_pack DROP COLUMN "width";
ALTER TABLE battery_pack DROP COLUMN "length";
ALTER TABLE battery_pack DROP COLUMN "height";
ALTER TABLE battery_pack DROP COLUMN "weight";


-- Delete Gem ID Sequence and remove it from table

ALTER TABLE "component_kind" ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE gem_id_seq;

