CREATE TABLE salary (
    salary_id BIGINT AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    basic_pay DECIMAL(12, 2) NOT NULL,
    allowances DECIMAL(12, 2) NOT NULL,
    deductions DECIMAL(12, 2) NOT NULL,
    net_pay DECIMAL(12, 2) NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    PRIMARY KEY (salary_id),
    CONSTRAINT fk_salary_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
