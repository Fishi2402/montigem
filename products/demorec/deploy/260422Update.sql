CREATE TABLE assoc_joint_connection_joint_connection_photo_photo(
    joint_connection_id BIGINT NOT NULL,
    photo_id BIGINT NOT NULL,
    CONSTRAINT pk_assoc_joint_connection_joint_connection_photo_photo PRIMARY KEY (joint_connection_id, photo_id)
);
