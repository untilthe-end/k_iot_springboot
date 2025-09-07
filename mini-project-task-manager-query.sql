CREATE DATABASE `mini-project-task-manager`;

CREATE TABLE `tasks`(
	id          BIGINT PRIMARY KEY AUTO_INCREMENT,
	author_id	BIGINT NULL,
    project_id  BIGINT NOT NULL,
	title		varchar(200) NOT NULL,
    author 		varchar(200) NOT NULL,
    content		TEXT NOT NULL,
    status      varchar(20) NOT NULL DEFAULT 'TODO',
	priority    varchar(50) NOT NULL DEFAULT 'MEDIUM',
	due_date    DATE NULL,
	created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_task_status CHECK (status IN ('TODO','IN_PROGRESS','DONE')),
	CONSTRAINT chk_task_priority CHECK (priority IN ('LOW','MEDIUM','HIGH')),
       
	CONSTRAINT fk_task_project  FOREIGN KEY (project_id) REFERENCES projects(id),
	CONSTRAINT fk_task_user FOREIGN KEY (author_id) REFERENCES users(id),
	INDEX idx_task_status (id, status),				# taskId와 status 기준 검색
	INDEX idx_task_author_due (author_id, due_date)	# author와 due 기준 검색

);







USE `mini-project-task-manager`;