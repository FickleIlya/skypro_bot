-- liquibase formatted sql

-- changeset root:1
CREATE TABLE notification_task
(
    id serial NOT NULL,
    chat_id bigint NOT NULL,
    text text NOT NULL,
    send_time timestamp without time zone NOT NULL,
    CONSTRAINT notification_task_pkey PRIMARY KEY (id)
);

-- changeset root:2
ALTER TABLE notification_task
    ADD COLUMN is_sent boolean NOT NULL DEFAULT false;