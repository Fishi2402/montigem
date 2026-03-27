ALTER TABLE photo RENAME TO local_photo;
ALTER TABLE local_photo RENAME CONSTRAINT pk_photo TO pk_local_photo;

CREATE TABLE "external_photo"(
  id BIGINT NOT NULL,
  CONSTRAINT pk_external_photo PRIMARY KEY (id)
  ,"external_url" VARCHAR NOT NULL
);

CREATE TABLE "photo"(
  id BIGINT NOT NULL,
  CONSTRAINT pk_photo PRIMARY KEY (id)
);
