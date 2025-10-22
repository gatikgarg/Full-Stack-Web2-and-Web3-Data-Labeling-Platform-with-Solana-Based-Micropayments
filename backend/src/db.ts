CREATE TABLE SubscriptionStatus (
    report_id SERIAL PRIMARY KEY,
    report_name VARCHAR(255),
    organization VARCHAR(255),
    report_date DATE,
    department VARCHAR(255),
    status VARCHAR(50)
);
INSERT INTO SubscriptionStatus (report_name, organization, report_date, department, status)
VALUES
('Financial Performance Report Q4 2024', 'Standard Chartered Bank', '2024-03-15', 'Finance Department', 'Approved'),
('Market Analysis Report - Asia Pacific', 'Standard Chartered Bank', '2024-03-15', 'Finance Department', 'Pending'),
('Risk Assessment Report 2024', 'Standard Chartered Bank', '2024-03-15', 'Finance Department', 'Approved'),
('Customer Satisfaction Survey Results', 'Standard Chartered Bank', '2024-03-15', 'Finance Department', 'Rejected'),
('Technology Infrastructure Review', 'Standard Chartered Bank', '2024-03-15', 'Finance Department', 'Pending');
