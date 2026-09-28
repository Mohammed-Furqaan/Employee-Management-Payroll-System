CREATE TABLE employee (
    employee_id BIGINT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NULL,
    hire_date DATE NOT NULL,
    password VARCHAR(100) NOT NULL,
    department_id BIGINT NULL,
    role_id BIGINT NULL,
    manager_id BIGINT NULL,
    PRIMARY KEY (employee_id),
    CONSTRAINT uq_employee_email UNIQUE (email),
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES department (department_id),
    CONSTRAINT fk_employee_role FOREIGN KEY (role_id) REFERENCES role (role_id),
    CONSTRAINT fk_employee_manager FOREIGN KEY (manager_id) REFERENCES employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Resolve circular dependency by adding foreign key on department table pointing to employee
ALTER TABLE department
ADD CONSTRAINT fk_department_head_employee FOREIGN KEY (head_employee_id) REFERENCES employee (employee_id);
