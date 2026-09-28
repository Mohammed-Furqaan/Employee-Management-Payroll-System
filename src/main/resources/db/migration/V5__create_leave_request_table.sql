CREATE TABLE leave_request (
    leave_id BIGINT AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    approved_by BIGINT NULL,
    PRIMARY KEY (leave_id),
    CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_leave_approved_by FOREIGN KEY (approved_by) REFERENCES employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
