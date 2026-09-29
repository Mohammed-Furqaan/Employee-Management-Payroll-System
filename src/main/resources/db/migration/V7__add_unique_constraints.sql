-- Migration V7: Add database-level unique constraints to prevent duplicate salary slips and attendance check-ins

ALTER TABLE salary ADD CONSTRAINT uq_salary_employee_month_year UNIQUE (employee_id, month, year);

ALTER TABLE attendance ADD CONSTRAINT uq_attendance_employee_date UNIQUE (employee_id, date);
