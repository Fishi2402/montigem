CREATE TABLE "bounding_box"(id BIGINT NOT NULL, CONSTRAINT pk_bounding_box PRIMARY KEY (id),"width" DOUBLE PRECISION NOT NULL,"length" DOUBLE PRECISION NOT NULL,"height" DOUBLE PRECISION NOT NULL);
CREATE TABLE "screw_driver"(id BIGINT NOT NULL,CONSTRAINT pk_screw_driver PRIMARY KEY (id),"max_torque" DOUBLE PRECISION NOT NULL,"name" VARCHAR NOT NULL);
CREATE TABLE "gripper"(id BIGINT NOT NULL,CONSTRAINT pk_gripper PRIMARY KEY (id),"name" VARCHAR NOT NULL);
CREATE TABLE "two_jaws_gripper"(id BIGINT NOT NULL,CONSTRAINT pk_two_jaws_gripper PRIMARY KEY (id),"min_span" DOUBLE PRECISION NOT NULL,"max_span" DOUBLE PRECISION NOT NULL,"name" VARCHAR NOT NULL);
CREATE TABLE "vacuum_gripper"(id BIGINT NOT NULL,CONSTRAINT pk_vacuum_gripper PRIMARY KEY (id),"name" VARCHAR NOT NULL);
CREATE TABLE "magnetic_gripper"(id BIGINT NOT NULL,CONSTRAINT pk_magnetic_gripper PRIMARY KEY (id),"name" VARCHAR NOT NULL);
CREATE TABLE "specific_gripper"(id BIGINT NOT NULL,CONSTRAINT pk_specific_gripper PRIMARY KEY (id),"name" VARCHAR NOT NULL);
CREATE TABLE "pincers"(id BIGINT NOT NULL,CONSTRAINT pk_pincers PRIMARY KEY (id),"name" VARCHAR NOT NULL);
CREATE TABLE "rolling_knife"(id BIGINT NOT NULL,CONSTRAINT pk_rolling_knife PRIMARY KEY (id),"name" VARCHAR NOT NULL);

CREATE TABLE assoc_end_effector_end_effector_bounding_box_bounding_box(end_effector_id BIGINT NOT NULL,bounding_box_id BIGINT NOT NULL,CONSTRAINT pk_assoc_end_effector_end_effector_bounding_box_bounding_box PRIMARY KEY (end_effector_id, bounding_box_id));
CREATE TABLE assoc_specific_gripper_specific_gripper_designed_for_component_kind(specific_gripper_id BIGINT NOT NULL,designed_for_id BIGINT NOT NULL,CONSTRAINT pk_assoc_specific_gripper_specific_gripper_designed_for_component_kind PRIMARY KEY (specific_gripper_id, designed_for_id));
