-- حذف الجداول القديمة
DROP TABLE IF EXISTS payments, maintenance_requests, technicians, tenants, apartments, owners, admins CASCADE;

-- إنشاء الجداول الجديدة
CREATE TABLE owners (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200)
);

CREATE TABLE apartments (
    id SERIAL PRIMARY KEY,
    owner_id INTEGER REFERENCES owners(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    town VARCHAR(100),
    location VARCHAR(200),
    description TEXT,
    image_path VARCHAR(200),
    status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE tenants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    occupation VARCHAR(100),
    address VARCHAR(200),
    status VARCHAR(20) DEFAULT 'Active'
);

CREATE TABLE technicians (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    specialty VARCHAR(100),
    availability VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE maintenance_requests (
    id SERIAL PRIMARY KEY,
    apartment_id INTEGER REFERENCES apartments(id) ON DELETE CASCADE,
    tenant_id INTEGER REFERENCES tenants(id) ON DELETE CASCADE,
    technician_id INTEGER REFERENCES technicians(id) ON DELETE SET NULL,
    request_date DATE NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'Pending',
    cost NUMERIC(10,2) DEFAULT 0
);

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    tenant_id INTEGER REFERENCES tenants(id) ON DELETE CASCADE,
    apartment_id INTEGER REFERENCES apartments(id) ON DELETE CASCADE,
    payment_date DATE NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    month VARCHAR(20),
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'Paid',
    description TEXT
);

CREATE TABLE admins (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- إدخال بيانات تجريبية (5 صفوف لكل جدول)
INSERT INTO owners (name, phone, email, address) VALUES
('Ali Ahmad', '0591234567', 'ali@example.com', 'Ramallah, Palestine'),
('Sara Nairat', '0592345678', 'sara@example.com', 'Nablus, Palestine'),
('Omar Khalil', '0593456789', 'omar@example.com', 'Hebron, Palestine'),
('Lina Saeed', '0594567890', 'lina@example.com', 'Jenin, Palestine'),
('Hani Zayed', '0595678901', 'hani@example.com', 'Bethlehem, Palestine');

INSERT INTO apartments (owner_id, name, type, town, location, description, image_path, status) VALUES
(1, 'Al-Quds Flat', 'Flat', 'Ramallah', 'Al-Quds St.', 'Nice flat in city center', 'images/flat1.jpg', 'Available'),
(2, 'Nablus Studio', 'Studio', 'Nablus', 'Al-Madina St.', 'Cozy studio for students', 'images/studio1.jpg', 'Rented'),
(3, 'Hebron Villa', 'Villa', 'Hebron', 'Al-Khalil St.', 'Luxury villa with garden', 'images/villa1.jpg', 'Available'),
(4, 'Jenin Bungalow', 'Bungalow', 'Jenin', 'Al-Jenan St.', 'Quiet bungalow', 'images/bungalow1.jpg', 'Available'),
(5, 'Bethlehem Apartment', 'Flat', 'Bethlehem', 'Al-Mahd St.', 'Modern apartment', 'images/flat2.jpg', 'Rented');

INSERT INTO tenants (name, email, phone, occupation, address, status) VALUES
('Mohammad Saleh', 'mohammad@example.com', '0596789012', 'Engineer', 'Ramallah, Palestine', 'Active'),
('Mona Fathi', 'mona@example.com', '0597890123', 'Teacher', 'Nablus, Palestine', 'Active'),
('Yousef Hasan', 'yousef@example.com', '0598901234', 'Student', 'Hebron, Palestine', 'Active'),
('Rania Odeh', 'rania@example.com', '0599012345', 'Doctor', 'Jenin, Palestine', 'Inactive'),
('Khaled Jaber', 'khaled@example.com', '0590123456', 'Designer', 'Bethlehem, Palestine', 'Active');

INSERT INTO technicians (first_name, last_name, phone, email, specialty, availability) VALUES
('Fadi', 'Suleiman', '0591239876', 'fadi@example.com', 'Electrician', 'Available'),
('Huda', 'Barakat', '0592348765', 'huda@example.com', 'Plumber', 'Busy'),
('Samir', 'Awad', '0593457654', 'samir@example.com', 'Carpenter', 'Available'),
('Nisreen', 'AbuShams', '0594566543', 'nisreen@example.com', 'Painter', 'Available'),
('Majed', 'Khalaf', '0595675432', 'majed@example.com', 'Mechanic', 'Busy');

INSERT INTO maintenance_requests (apartment_id, tenant_id, technician_id, request_date, description, status, cost) VALUES
(1, 1, 1, '2025-08-01', 'Fix electricity issue', 'Pending', 100.00),
(2, 2, 2, '2025-08-02', 'Plumbing problem', 'Completed', 150.00),
(3, 3, 3, '2025-08-03', 'Broken door', 'Pending', 80.00),
(4, 4, 4, '2025-08-04', 'Paint walls', 'Pending', 120.00),
(5, 5, 5, '2025-08-05', 'Car repair', 'Completed', 200.00);

INSERT INTO payments (tenant_id, apartment_id, payment_date, amount, month, payment_method, payment_status, description) VALUES
(1, 1, '2025-08-01', 500.00, 'August', 'Cash', 'Paid', 'Rent payment'),
(2, 2, '2025-08-02', 350.00, 'August', 'Bank Transfer', 'Paid', 'Rent payment'),
(3, 3, '2025-08-03', 1200.00, 'August', 'Credit Card', 'Pending', 'Villa rent'),
(4, 4, '2025-08-04', 400.00, 'August', 'Cash', 'Paid', 'Bungalow rent'),
(5, 5, '2025-08-05', 600.00, 'August', 'Bank Transfer', 'Paid', 'Apartment rent');

INSERT INTO admins (email, password) VALUES
('admin1@email.com', 'admin123'),
('admin2@email.com', 'admin456'),
('admin3@email.com', 'admin789'),
('admin4@email.com', 'admin000'),
('admin5@email.com', 'admin555');
