CREATE TABLE "process_model"(
   id BIGINT NOT NULL,
   CONSTRAINT pk_process_model PRIMARY KEY (id),
   "name" VARCHAR NOT NULL,
   "description" VARCHAR NOT NULL
);
CREATE TABLE assoc_process_model_process_model_underlying_process_process(
   process_model_id BIGINT NOT NULL,
   underlying_process_id BIGINT NOT NULL,
   CONSTRAINT pk_assoc_process_model_process_model_underlying_process_process PRIMARY KEY (process_model_id, underlying_process_id)
);
CREATE TABLE assoc_process_model_process_model_sub_tasks_task(
   process_model_id BIGINT NOT NULL,
   sub_tasks_id BIGINT NOT NULL,
   CONSTRAINT pk_assoc_process_model_process_model_sub_tasks_task PRIMARY KEY (process_model_id, sub_tasks_id)
);

CREATE TABLE "task"(
  id BIGINT NOT NULL,
  CONSTRAINT pk_task PRIMARY KEY (id)
  ,"name" VARCHAR NOT NULL
  ,"description" VARCHAR NOT NULL
  ,"time_consumption" DOUBLE PRECISION NOT NULL
);
CREATE TABLE "process_model_front_end_commands"(
  id BIGINT NOT NULL,
  CONSTRAINT pk_process_model_front_end_commands PRIMARY KEY (id)
);

CREATE TABLE assoc_task_task_underlying_process_step_process_step(
  task_id BIGINT NOT NULL,
  underlying_process_step_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_task_task_underlying_process_step_process_step PRIMARY KEY (task_id, underlying_process_step_id)
);
CREATE TABLE assoc_task_task_depends_on_task(
  task_id BIGINT NOT NULL,
  depends_on_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_task_task_depends_on_task PRIMARY KEY (task_id, depends_on_id)
);
CREATE TABLE assoc_task_task_uses_end_effector(
  task_id BIGINT NOT NULL,
  uses_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_task_task_uses_end_effector PRIMARY KEY (task_id, uses_id)
);
CREATE TABLE assoc_task_task_deconstructs_join_connection(
  task_id BIGINT NOT NULL,
  deconstructs_id BIGINT NOT NULL,
  CONSTRAINT pk_assoc_task_task_deconstructs_join_connection PRIMARY KEY (task_id, deconstructs_id)
);
