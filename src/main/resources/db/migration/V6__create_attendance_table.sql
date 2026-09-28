CREATE TABLE attendance (
    attendance_id BIGINT AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    check_in TIME NULL,
    check_out TIME NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (attendance_id),
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
