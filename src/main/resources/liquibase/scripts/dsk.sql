-- liquibase formatted sql

-- changeset dkarpov:1
CREATE TABLE notification_task (
                       id SERIAL PRIMARY KEY,
                       chat_id BIGINT,
                       time TIMESTAMP,
                       text TEXT
);