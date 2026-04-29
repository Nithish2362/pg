-- 0. Create Features Table (if Hibernate failed)
CREATE TABLE IF NOT EXISTS features (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id VARCHAR(255),
    order_by INTEGER,
    path VARCHAR(255),
    icon VARCHAR(255),
    default_child_id VARCHAR(255)
);

-- Insert Default Features (Navigation Items)
INSERT INTO features (id, name, parent_id, order_by, path, icon, default_child_id) VALUES 
('DASHBOARD', 'Dashboard', NULL, 1, '/dashboard', 'LayoutDashboard', NULL),
('TENANTS', 'Tenants', NULL, 2, '/dashboard/tenants', 'Users', NULL),
('COMPLAINTS', 'Complaints', NULL, 3, '/dashboard/complaints', 'AlertTriangle', NULL),
('VISITORS', 'Visitors', NULL, 4, '/dashboard/visitors', 'UserPlus', NULL),
('NOTICES', 'Notices', NULL, 5, '/dashboard/notices', 'Bell', NULL);

-- 1. Insert Sample Notices
INSERT INTO notices (title, content, created_at, active) VALUES 
('Welcome to StayPro', 'Welcome to our premium co-living community! We hope you enjoy your stay.', NOW(), true),
('Water Maintenance', 'The water supply will be briefly interrupted on Sunday from 10 AM to 12 PM for tank cleaning.', NOW(), true),
('New Gym Rules', 'The gym is now open 24/7. Please ensure you carry your resident card.', NOW(), true);

-- 2. Insert Sample Complaints (Assumes PG-0001 and PG-0002 exist)
INSERT INTO complaints (pg_number, issue, status, created_at, admin_remark) VALUES 
('PG-0001', 'AC in my room is making a loud noise.', 'OPEN', NOW(), NULL),
('PG-0002', 'The bathroom tap is leaking slightly.', 'IN_PROGRESS', NOW(), 'Plumber scheduled for tomorrow.'),
('PG-0001', 'Wifi connectivity is slow in the balcony.', 'RESOLVED', NOW(), 'Added a new range extender.');

-- 3. Insert Sample Visitors
INSERT INTO visitors (pg_number, visitor_name, phone, purpose, request_date, status, in_time, out_time) VALUES 
('PG-0001', 'John Doe', '9876543210', 'Meeting a friend', NOW(), 'APPROVED', NOW(), NULL),
('PG-0002', 'Jane Smith', '9123456789', 'Project discussion', NOW(), 'PENDING', NULL, NULL),
('PG-0001', 'Mike Tyson', '8888888888', 'Delivery', NOW(), 'APPROVED', NOW(), NOW());

-- 4. Insert Sample Tenants (If table exists and is empty)
-- Note: You may need to create rooms and beds first if you want valid foreign keys.
