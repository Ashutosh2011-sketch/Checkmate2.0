CREATE TABLE IF NOT EXISTS checklists (
    id BIGSERIAL PRIMARY KEY,
    checklist_name VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    visibility VARCHAR(20) NOT NULL,
    workflow_type VARCHAR(20) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS sections (
    id BIGSERIAL PRIMARY KEY,
    checklist_id BIGINT NOT NULL REFERENCES checklists(id) ON DELETE CASCADE,
    section_name VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL,
    due_date_days INT NOT NULL,
    depends_on VARCHAR(255) NOT NULL,
    condition_dependent_on VARCHAR(255) NOT NULL,
    condition_expected_outcome VARCHAR(50) NOT NULL,
    remind_before INT NOT NULL,
    escalate_to VARCHAR(100) NOT NULL,
    show_advanced BOOLEAN NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS task_assignees (
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    assignee VARCHAR(255) NOT NULL,
    PRIMARY KEY (task_id, assignee)
);

CREATE INDEX IF NOT EXISTS idx_sections_checklist_id ON sections(checklist_id);
CREATE INDEX IF NOT EXISTS idx_tasks_section_id ON tasks(section_id);

