CREATE TABLE department (
    department_id BIGINT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    head_employee_id BIGINT NULL,
    PRIMARY KEY (department_id),
    CONSTRAINT uq_department_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
