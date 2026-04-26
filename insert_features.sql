INSERT INTO features (id, name, parent_id, order_by, path, icon, default_child_id) VALUES 
('1', 'Dashboard', NULL, 1, '/dashboard', 'dashboard', NULL),
('2', 'Location Management', NULL, 2, '/locations', 'location_on', '2-1'),
('2-1', 'Locations', '2', 1, '/locations', 'pin_drop', NULL),
('2-2', 'Buildings', '2', 2, '/buildings', 'business', NULL),
('3', 'Inventory', NULL, 3, '/floors', 'inventory', '3-1'),
('3-1', 'Floors', '3', 1, '/floors', 'layers', NULL),
('3-2', 'Rooms', '3', 2, '/rooms', 'meeting_room', NULL),
('4', 'Tenants', NULL, 4, '/tenants', 'people', '4-1'),
('4-1', 'Active Tenants', '4', 1, '/tenants', 'person', NULL),
('4-2', 'Payments', '4', 2, '/payments', 'payments', NULL);
