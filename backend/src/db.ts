CREATE TABLE SubscriptionStatus (
    report_id SERIAL PRIMARY KEY,
    report_name VARCHAR(255),
    report_domain VARCHAR(255),
    report_date DATE,
    status VARCHAR(50)
);

INSERT INTO SubscriptionStatus (report_name, report_domain, report_date, status)
VALUES
('Customer Analytics Report', 'Finance', '2025-06-01', 'Approved'),
('Credit Risk Assessment', 'Risk Management', '2025-06-10', 'Pending'),
('AML Compliance Report', 'Compliance', '2025-06-15', 'Rejected'),
('Market Trends 2025', 'Research', '2025-06-20', 'Approved'),
('Client Due Diligence', 'Compliance', '2025-06-25', 'Pending');
