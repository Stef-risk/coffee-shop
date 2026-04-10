-- Coffee Shop Management System - Database Initialization Script
-- MySQL 8.0+
-- Run this script once to initialize the database and seed default data.

CREATE DATABASE IF NOT EXISTS coffeeshop
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE coffeeshop;

-- ==============================================================
-- Default Admin User (password: Admin@123456)
-- BCrypt hash of "Admin@123456"
-- ==============================================================
INSERT IGNORE INTO users (username, password, full_name, phone, email, role, active, created_at, updated_at)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000000', 'admin@coffeeshop.com', 'ADMIN', true, NOW(), NOW());

INSERT IGNORE INTO users (username, password, full_name, phone, email, role, active, created_at, updated_at)
VALUES ('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '店长', '13800000001', 'manager@coffeeshop.com', 'MANAGER', true, NOW(), NOW());

INSERT IGNORE INTO users (username, password, full_name, phone, email, role, active, created_at, updated_at)
VALUES ('cashier01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '收银员小王', '13800000002', NULL, 'CASHIER', true, NOW(), NOW());

INSERT IGNORE INTO users (username, password, full_name, phone, email, role, active, created_at, updated_at)
VALUES ('kitchen01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吧台师傅老李', '13800000003', NULL, 'KITCHEN', true, NOW(), NOW());

-- ==============================================================
-- Menu Categories
-- ==============================================================
INSERT IGNORE INTO menu_categories (name, description, display_order, active, created_at, updated_at)
VALUES
    ('精品咖啡', '手工精制意式咖啡系列', 10, true, NOW(), NOW()),
    ('特调饮品', '创意特调，四季限定', 9, true, NOW(), NOW()),
    ('茶饮系列', '甄选茗茶，清新怡人', 8, true, NOW(), NOW()),
    ('鲜果饮品', '新鲜水果现榨', 7, true, NOW(), NOW()),
    ('轻食简餐', '精选轻食，健康美味', 6, true, NOW(), NOW()),
    ('甜点烘焙', '现烤甜点，每日新鲜', 5, true, NOW(), NOW());

-- ==============================================================
-- Menu Items (use category IDs based on above inserts)
-- ==============================================================
-- 精品咖啡
INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '美式咖啡', '经典美式，浓郁醇香', 22.00, id, NULL, true, 10, NOW(), NOW()
FROM menu_categories WHERE name = '精品咖啡';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '拿铁', '意式浓缩搭配丝滑牛奶', 28.00, id, NULL, true, 9, NOW(), NOW()
FROM menu_categories WHERE name = '精品咖啡';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '卡布奇诺', '经典意式，奶泡绵密', 28.00, id, NULL, true, 8, NOW(), NOW()
FROM menu_categories WHERE name = '精品咖啡';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '摩卡', '巧克力与咖啡的完美融合', 30.00, id, NULL, true, 7, NOW(), NOW()
FROM menu_categories WHERE name = '精品咖啡';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '澳白', '双份Ristretto，微量牛奶', 32.00, id, NULL, true, 6, NOW(), NOW()
FROM menu_categories WHERE name = '精品咖啡';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '馥芮白', '顺滑浓缩，优选单品', 32.00, id, NULL, true, 5, NOW(), NOW()
FROM menu_categories WHERE name = '精品咖啡';

-- 特调饮品
INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '生椰拿铁', '椰浆与浓缩的热带风情', 32.00, id, NULL, true, 10, NOW(), NOW()
FROM menu_categories WHERE name = '特调饮品';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '厚乳拿铁', '醇厚牛乳，浓郁香甜', 30.00, id, NULL, true, 9, NOW(), NOW()
FROM menu_categories WHERE name = '特调饮品';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '燕麦拿铁', '燕麦奶植物基，清爽健康', 32.00, id, NULL, true, 8, NOW(), NOW()
FROM menu_categories WHERE name = '特调饮品';

-- 茶饮系列
INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '茉莉花茶', '鲜花窨制，清香淡雅', 22.00, id, NULL, true, 10, NOW(), NOW()
FROM menu_categories WHERE name = '茶饮系列';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '玫瑰荔枝', '玫瑰花茶配新鲜荔枝', 26.00, id, NULL, true, 9, NOW(), NOW()
FROM menu_categories WHERE name = '茶饮系列';

-- 轻食简餐
INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '牛油果吐司', '优质牛油果搭配全麦吐司', 32.00, id, NULL, true, 10, NOW(), NOW()
FROM menu_categories WHERE name = '轻食简餐';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '鸡胸肉沙拉', '低卡高蛋白，健康之选', 38.00, id, NULL, true, 9, NOW(), NOW()
FROM menu_categories WHERE name = '轻食简餐';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '三明治套餐', '全麦三明治+饮品', 45.00, id, NULL, true, 8, NOW(), NOW()
FROM menu_categories WHERE name = '轻食简餐';

-- 甜点烘焙
INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '提拉米苏', '经典意式甜点', 36.00, id, NULL, true, 10, NOW(), NOW()
FROM menu_categories WHERE name = '甜点烘焙';

INSERT IGNORE INTO menu_items (name, description, price, category_id, image_url, available, display_order, created_at, updated_at)
SELECT '曲奇饼干', '现烤黄油曲奇，酥香松脆', 18.00, id, NULL, true, 9, NOW(), NOW()
FROM menu_categories WHERE name = '甜点烘焙';

-- ==============================================================
-- Dining Tables
-- ==============================================================
INSERT IGNORE INTO dining_tables (table_number, capacity, status, location, notes, created_at, updated_at)
VALUES
    ('A01', 2, 'AVAILABLE', '一楼大厅', '靠窗位置', NOW(), NOW()),
    ('A02', 2, 'AVAILABLE', '一楼大厅', '靠窗位置', NOW(), NOW()),
    ('A03', 4, 'AVAILABLE', '一楼大厅', NULL, NOW(), NOW()),
    ('A04', 4, 'AVAILABLE', '一楼大厅', NULL, NOW(), NOW()),
    ('A05', 6, 'AVAILABLE', '一楼大厅', '圆桌', NOW(), NOW()),
    ('B01', 2, 'AVAILABLE', '二楼雅座', '安静区域', NOW(), NOW()),
    ('B02', 4, 'AVAILABLE', '二楼雅座', NULL, NOW(), NOW()),
    ('B03', 4, 'AVAILABLE', '二楼雅座', NULL, NOW(), NOW()),
    ('VIP01', 8, 'AVAILABLE', 'VIP包间', '含独立音响', NOW(), NOW()),
    ('VIP02', 10, 'AVAILABLE', 'VIP包间', '含投影仪', NOW(), NOW()),
    ('BAR01', 1, 'AVAILABLE', '吧台', '吧台座位1', NOW(), NOW()),
    ('BAR02', 1, 'AVAILABLE', '吧台', '吧台座位2', NOW(), NOW()),
    ('BAR03', 1, 'AVAILABLE', '吧台', '吧台座位3', NOW(), NOW()),
    ('BAR04', 1, 'AVAILABLE', '吧台', '吧台座位4', NOW(), NOW());
