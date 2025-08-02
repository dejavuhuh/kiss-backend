-- PostgreSQL Mock Data Generation
-- Generated based on the provided schema.
-- Data volume has been significantly increased with diverse ratings for realistic positive_rating_ratio.
-- All user_id values are set to 1 as requested.

-- =================================================================
-- 1. 品牌 (brand)
-- =================================================================
INSERT INTO brand (id, name) OVERRIDING SYSTEM VALUE
VALUES (1, 'Apple'),
       (2, '华为 (Huawei)'),
       (3, '小米 (Xiaomi)'),
       (4, '三星 (Samsung)'),
       (5, '耐克 (Nike)'),
       (6, '阿迪达斯 (Adidas)'),
       (7, '优衣库 (Uniqlo)'),
       (8, '宜家 (IKEA)'),
       (9, '索尼 (Sony)'),
       (10, '戴尔 (Dell)'),
       (11, 'Lululemon'),
       (12, 'Patagonia'),
       (13, '罗技 (Logitech)'),
       (14, '雷蛇 (Razer)'),
       (15, '赫曼米勒 (Herman Miller)');

-- =================================================================
-- 2. 店铺 (store)
-- =================================================================
INSERT INTO store (id, name) OVERRIDING SYSTEM VALUE
VALUES (1, 'Apple 官方旗舰店'),
       (2, '华为商城'),
       (3, '小米自营旗舰店'),
       (4, '三星电子官方旗舰店'),
       (5, 'Nike 官方旗舰店'),
       (6, 'Adidas 官方旗舰店'),
       (7, '优衣库官方网络旗舰店'),
       (8, 'IKEA宜家家居'),
       (9, 'Sony 官方旗舰店'),
       (10, 'Dell 官方旗舰店'),
       (11, 'Lululemon 官方旗舰店'),
       (12, 'Patagonia 官方旗舰店'),
       (13, '罗技官方旗舰店'),
       (14, 'Razer 雷蛇官方旗舰店'),
       (15, '赫曼米勒官方旗舰店');

-- =================================================================
-- 3. 商品分类 (product_category)
-- =================================================================
-- 插入一级分类
INSERT INTO product_category (id, parent_id, name, is_leaf, sort_order, enabled) OVERRIDING SYSTEM VALUE
VALUES (1, NULL, '电子产品', false, 1, true),
       (2, NULL, '服饰鞋包', false, 2, true),
       (3, NULL, '家居生活', false, 3, true),
       (4, NULL, '户外运动', false, 4, true);

-- 插入二级分类
INSERT INTO product_category (id, parent_id, name, is_leaf, sort_order, enabled) OVERRIDING SYSTEM VALUE
VALUES (101, 1, '手机', false, 1, true),
       (102, 1, '电脑', false, 2, true),
       (103, 1, '影音娱乐', false, 3, true),
       (104, 1, '电脑外设', false, 4, true),
       (201, 2, '运动鞋', true, 1, true),
       (202, 2, 'T恤', true, 2, true),
       (203, 2, '背包', true, 3, true),
       (204, 2, '瑜伽服', true, 4, true),
       (301, 3, '家具', false, 1, true),
       (302, 3, '灯具', true, 2, true),
       (401, 4, '冲锋衣', true, 1, true);

-- 插入三级分类
INSERT INTO product_category (id, parent_id, name, is_leaf, sort_order, enabled) OVERRIDING SYSTEM VALUE
VALUES (1011, 101, '智能手机', true, 1, true),
       (1021, 102, '笔记本电脑', true, 1, true),
       (1022, 102, '台式机', true, 2, true),
       (1031, 103, '耳机', true, 1, true),
       (1032, 103, '相机', true, 2, true),
       (1041, 104, '键盘', true, 1, true),
       (1042, 104, '鼠标', true, 2, true),
       (3011, 301, '沙发', true, 1, true),
       (3012, 301, '椅子', true, 2, true),
       (3013, 301, '床垫', true, 3, true);


-- =================================================================
-- 4. 商品评价维度规格 (spu_comment_dimension_spec)
-- =================================================================
INSERT INTO spu_comment_dimension_spec (id, name) OVERRIDING SYSTEM VALUE
VALUES (1, '外观设计'),
       (2, '性能表现'),
       (3, '性价比'),
       (4, '电池续航'),
       (5, '物流速度'),
       (6, '客服态度'),
       (7, '舒适度'),
       (8, '耐用性'),
       (9, '安装体验');

-- =================================================================
-- 5. 商品 (spu)
-- =================================================================
INSERT INTO spu (id, category_id, brand_id, store_id, title, price, banner) OVERRIDING SYSTEM VALUE
VALUES (1, 1011, 1, 1, 'iPhone 15 Pro Max 512G 原色钛金属', 11999.00, CAST(floor(random() * 50) + 1 AS text)),
       (2, 1011, 2, 2, '华为 Mate 60 Pro 12+512GB 雅川青', 6999.00, CAST(floor(random() * 50) + 1 AS text)),
       (3, 1011, 3, 3, '小米 14 Ultra 16GB+1TB 龙晶蓝', 6999.00, CAST(floor(random() * 50) + 1 AS text)),
       (4, 1021, 1, 1, 'MacBook Pro 14英寸 M3 Pro芯片 18G 512G 深空黑', 19999.00,
        CAST(floor(random() * 50) + 1 AS text)),
       (5, 1021, 10, 10, 'Dell XPS 15 笔记本电脑 i9处理器 32G 1T', 22999.00, CAST(floor(random() * 50) + 1 AS text)),
       (6, 1031, 9, 9, 'Sony WH-1000XM5 头戴式无线降噪耳机', 2599.00, CAST(floor(random() * 50) + 1 AS text)),
       (7, 1031, 1, 1, 'Apple AirPods Pro (第二代)', 1899.00, CAST(floor(random() * 50) + 1 AS text)),
       (8, 201, 5, 5, 'Nike Air Force 1 ''07 男子空军一号运动鞋', 799.00, CAST(floor(random() * 50) + 1 AS text)),
       (9, 201, 6, 6, 'Adidas SUPERSTAR 经典贝壳头男女运动鞋', 829.00, CAST(floor(random() * 50) + 1 AS text)),
       (10, 202, 7, 7, '优衣库 AIRism棉混纺圆领T恤(短袖) 465181', 79.00, CAST(floor(random() * 50) + 1 AS text)),
       (11, 3011, 8, 8, '宜家 维姆勒 三人位沙发, 带贵妃椅', 4999.00, CAST(floor(random() * 50) + 1 AS text)),
       (12, 302, 8, 8, '宜家 特提亚 工作灯, 白色', 69.00, CAST(floor(random() * 50) + 1 AS text)),
       (13, 1032, 9, 9, 'Sony Alpha 7 IV 全画幅微单相机', 16999.00, CAST(floor(random() * 50) + 1 AS text)),
       (14, 203, 5, 5, 'Nike Brasilia JDI 儿童迷你背包', 199.00, CAST(floor(random() * 50) + 1 AS text)),
       (15, 204, 11, 11, 'Lululemon Align 系列女士高腰瑜伽裤', 850.00, CAST(floor(random() * 50) + 1 AS text)),
       (16, 401, 12, 12, 'Patagonia Triolet Jacket 女士冲锋衣', 3299.00, CAST(floor(random() * 50) + 1 AS text)),
       (17, 1041, 13, 13, '罗技 MX Keys S 无线蓝牙键盘', 899.00, CAST(floor(random() * 50) + 1 AS text)),
       (18, 1042, 13, 13, '罗技 MX Master 3S 无线鼠标', 899.00, CAST(floor(random() * 50) + 1 AS text)),
       (19, 1042, 14, 14, '雷蛇 毒蝰V2 Pro 无线游戏鼠标', 1099.00, CAST(floor(random() * 50) + 1 AS text)),
       (20, 3012, 15, 15, 'Herman Miller Aeron 人体工学椅', 12888.00, CAST(floor(random() * 50) + 1 AS text)),
       (21, 1011, 4, 4, '三星 Galaxy S24 Ultra 12GB+512GB', 9699.00, CAST(floor(random() * 50) + 1 AS text));

-- =================================================================
-- 6. 商品评价维度 (spu_comment_dimension)
-- =================================================================
INSERT INTO spu_comment_dimension (id, spu_id, spec_id, weight) OVERRIDING SYSTEM VALUE
VALUES (1, 1, 1, 0.30),
       (2, 1, 2, 0.40),
       (3, 1, 3, 0.20),
       (4, 1, 4, 0.10),
       (5, 2, 1, 0.25),
       (6, 2, 2, 0.45),
       (7, 2, 3, 0.15),
       (8, 2, 4, 0.15),
       (9, 3, 1, 0.30),
       (10, 3, 2, 0.50),
       (11, 3, 3, 0.10),
       (12, 3, 4, 0.10),
       (13, 4, 1, 0.35),
       (14, 4, 2, 0.50),
       (15, 4, 3, 0.15),
       (16, 5, 1, 0.20),
       (17, 5, 2, 0.50),
       (18, 5, 3, 0.20),
       (19, 5, 8, 0.10),
       (20, 6, 1, 0.30),
       (21, 6, 2, 0.40),
       (22, 6, 7, 0.30),
       (23, 7, 1, 0.20),
       (24, 7, 2, 0.40),
       (25, 7, 7, 0.40),
       (26, 8, 1, 0.40),
       (27, 8, 7, 0.40),
       (28, 8, 3, 0.20),
       (29, 9, 1, 0.50),
       (30, 9, 7, 0.30),
       (31, 9, 3, 0.20),
       (32, 10, 7, 0.60),
       (33, 10, 3, 0.20),
       (34, 10, 8, 0.20),
       (35, 11, 1, 0.30),
       (36, 11, 7, 0.50),
       (37, 11, 3, 0.20),
       (38, 11, 9, 0.10),
       (39, 12, 3, 0.50),
       (40, 12, 8, 0.50),
       (41, 13, 1, 0.20),
       (42, 13, 2, 0.60),
       (43, 13, 3, 0.20),
       (44, 14, 1, 0.50),
       (45, 14, 8, 0.50),
       (46, 15, 7, 0.70),
       (47, 15, 3, 0.15),
       (48, 15, 8, 0.15),
       (49, 16, 7, 0.40),
       (50, 16, 8, 0.60),
       (51, 17, 1, 0.20),
       (52, 17, 2, 0.30),
       (53, 17, 7, 0.30),
       (54, 17, 8, 0.20),
       (55, 18, 1, 0.20),
       (56, 18, 2, 0.30),
       (57, 18, 7, 0.50),
       (58, 19, 2, 0.60),
       (59, 19, 7, 0.20),
       (60, 19, 8, 0.20),
       (61, 20, 7, 0.60),
       (62, 20, 8, 0.30),
       (63, 20, 3, 0.10),
       (64, 21, 1, 0.30),
       (65, 21, 2, 0.40),
       (66, 21, 4, 0.20),
       (67, 21, 3, 0.10);

-- =================================================================
-- 7. 商品摘要 (spu_tag)
-- =================================================================
INSERT INTO spu_tag (id, spu_id, content) OVERRIDING SYSTEM VALUE
VALUES (1, 1, '旗舰机型'),
       (2, 1, '拍照神器'),
       (3, 1, 'A17 Pro芯片'),
       (4, 2, '卫星通话'),
       (5, 2, '麒麟芯片'),
       (6, 4, '生产力工具'),
       (7, 4, 'M3 Pro'),
       (8, 6, '降噪天花板'),
       (9, 8, '经典百搭'),
       (10, 11, '舒适'),
       (11, 11, '可定制'),
       (12, 15, '裸感体验'),
       (13, 15, '高弹力'),
       (14, 17, '办公神器'),
       (15, 18, '人体工学'),
       (16, 19, '电竞优选'),
       (17, 19, '轻量化'),
       (18, 20, '顶级坐感'),
       (19, 20, '健康办公'),
       (20, 21, 'AI手机'),
       (21, 21, '长焦拍摄');

-- =================================================================
-- 8. 商品评价 (spu_comment)
-- =================================================================
INSERT INTO spu_comment (id, spu_id, text) OVERRIDING SYSTEM VALUE
VALUES
-- SPU 1: iPhone 15 Pro Max (High-end phone, mixed reviews)
(1, 1, '手机非常流畅，拍照效果惊人，特别是夜景模式，清晰度很高。钛金属边框手感一流，就是价格有点小贵。'),
(2, 1, '从安卓换过来的，系统丝滑流畅，应用生态没得说。电池续航比想象中好，一天使用下来没问题。'),
(3, 1, '物流很快，第二天就到了。手机外观很漂亮，屏幕显示效果顶级。但是充电速度确实有点慢了。'),
(4, 1, '用了几天，发现发热有点严重，特别是玩游戏的时候。续航也没有宣传的那么好，有点失望。'),
(5, 1, '信号一般般，在地下室偶尔会没服务。对比我之前的安卓机差了点。'),
(6, 1, '总体还行吧，就是个手机。没感觉比上一代有多大提升，创新乏力。'),

-- SPU 2: 华为 Mate 60 Pro (High-end phone, mostly positive)
(7, 2, '国产之光！信号非常好，在地下车库都有信号。系统运行流畅，鸿蒙生态越来越好了。'),
(8, 2, '外观设计很有辨识度，相机拍照色彩真实，非常喜欢。支持国货！'),
(9, 2, '卫星通话功能在紧急情况下可能真的能救命，冲这个就值了。'),
(10, 2, '屏幕素质很高，昆仑玻璃确实耐摔，不小心摔了几次都没事。'),

-- SPU 8: Nike Air Force 1 (Classic shoes, mixed reviews on size/comfort)
(11, 8, '经典款就是不一样，百搭又好看，走路也很舒服。'),
(12, 8, '鞋子有点偏大，建议买小半码。不过质量很好，是正品。'),
(13, 8, '刚开始穿有点硬，磨脚后跟，穿几天适应了就好了。'),
(14, 8, '穿了不到半年就开胶了，质量感觉不如以前了，有点失望。'),
(15, 8, '纯白色的太容易脏了，不好打理。'),

-- SPU 10: 优衣库 T-shirt (Basic apparel, mixed on durability)
(16, 10, '优衣库的T恤性价比很高，面料舒服，夏天穿很凉快。'),
(17, 10, '基础款，随便穿穿，价格不贵，买了好几件换着穿。'),
(18, 10, '领口洗两次就变形了，变得松松垮垮的，只能当睡衣穿了。'),
(19, 10, '白色款有点透，需要穿浅色内衣。'),

-- SPU 11: IKEA Sofa (Furniture, mixed on assembly/comfort)
(20, 11, '沙发坐感偏硬，不是我喜欢的类型，但是外观还不错。'),
(21, 11, '安装太费劲了，说明书跟天书一样，自己装了整整一个下午。'),
(22, 11, '布套可以拆下来洗，这点很方便。颜色也和家里很搭。'),
(23, 11, '坐垫用了一年有点塌陷了，感觉支撑力不如刚买的时候。'),

-- SPU 15: Lululemon Align (Premium apparel, mostly positive)
(24, 15, 'Lululemon的瑜伽裤名不虚传，穿上跟没穿一样，非常舒服，运动无束缚。'),
(25, 15, '虽然贵，但是物有所值，面料和做工都很好，希望能穿久一点。'),
(26, 15, '颜色很正，显瘦。穿着去健身房好几个人问链接。'),

-- SPU 17: Logitech MX Keys S (Premium peripheral, mostly positive)
(27, 17, '键盘手感太棒了，打字是一种享受。多设备切换非常方便，办公效率大大提升。'),
(28, 17, '背光很智能，手靠近就亮，离开就灭，很省电。'),
(29, 17, '蓝牙连接偶尔会断开，不知道是不是我电脑的问题。'),

-- SPU 20: Herman Miller Aeron (Premium furniture, mostly positive but expensive)
(30, 20, '坐过的最舒服的椅子，没有之一。腰部支撑感很强，调节选项很多，完美贴合身体。'),
(31, 20, '价格劝退，但是为了老腰，还是咬牙买了。希望能改善我的腰椎间盘突出问题。'),
(32, 20, '确实是好东西，一分钱一分货。坐久了也不会腰酸背痛。'),
(33, 20, '网面材质夏天坐着很凉快，透气性很好。');

-- =================================================================
-- 9. 商品评价媒体 (spu_comment_media)
-- =================================================================
INSERT INTO spu_comment_media (id, comment_id, type, resource) OVERRIDING SYSTEM VALUE
VALUES (1, 1, 'image', 'https://example.com/images/iphone_pic1.jpg'),
       (2, 1, 'image', 'https://example.com/images/iphone_pic2.jpg'),
       (3, 4, 'image', 'https://example.com/images/iphone_heat.jpg'),
       (4, 7, 'image', 'https://example.com/images/mate60_pic1.jpg'),
       (5, 11, 'video', 'https://example.com/videos/af1_video.mp4'),
       (6, 14, 'image', 'https://example.com/images/af1_broken.jpg'),
       (7, 21, 'image', 'https://example.com/images/ikea_sofa_manual.jpg'),
       (8, 24, 'image', 'https://example.com/images/align_pant.jpg'),
       (9, 30, 'image', 'https://example.com/images/aeron_chair.jpg');


-- =================================================================
-- 10. 商品评价维度评分 (spu_comment_dimension_rating)
-- =================================================================
-- Comment 1-6 (SPU 1: iPhone 15 Pro Max)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (1, 1, 1, 5.0),
       (2, 2, 1, 5.0),
       (3, 3, 1, 4.0),
       (4, 4, 1, 4.5),
       (5, 1, 2, 4.5),
       (6, 2, 2, 5.0),
       (7, 3, 2, 4.0),
       (8, 4, 2, 4.5),
       (9, 1, 3, 5.0),
       (10, 2, 3, 5.0),
       (11, 3, 3, 3.5),
       (12, 4, 3, 4.0),
       (13, 1, 4, 4.0),
       (14, 2, 4, 3.0),
       (15, 3, 4, 3.5),
       (16, 4, 4, 3.0), -- Bad review
       (17, 1, 5, 4.0),
       (18, 2, 5, 2.5),
       (19, 3, 5, 4.0),
       (20, 4, 5, 4.0), -- Bad review
       (21, 1, 6, 4.0),
       (22, 2, 6, 4.0),
       (23, 3, 6, 3.5),
       (24, 4, 6, 4.0);
-- Neutral review

-- Comment 7-10 (SPU 2: 华为 Mate 60 Pro)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (25, 5, 7, 5.0),
       (26, 6, 7, 5.0),
       (27, 7, 7, 4.5),
       (28, 8, 7, 4.5),
       (29, 5, 8, 5.0),
       (30, 6, 8, 5.0),
       (31, 7, 8, 5.0),
       (32, 8, 8, 4.5),
       (33, 5, 9, 5.0),
       (34, 6, 9, 5.0),
       (35, 7, 9, 5.0),
       (36, 8, 9, 5.0),
       (37, 5, 10, 5.0),
       (38, 6, 10, 5.0),
       (39, 7, 10, 5.0),
       (40, 8, 10, 5.0);

-- Comment 11-15 (SPU 8: Nike Air Force 1)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (41, 26, 11, 5.0),
       (42, 27, 11, 4.5),
       (43, 28, 11, 4.0),
       (44, 26, 12, 4.0),
       (45, 27, 12, 4.0),
       (46, 28, 12, 4.5),
       (47, 26, 13, 4.0),
       (48, 27, 13, 3.0),
       (49, 28, 13, 4.0), -- Neutral review
       (50, 26, 14, 3.0),
       (51, 27, 14, 2.0),
       (52, 28, 14, 3.0), -- Bad review
       (53, 26, 15, 3.5),
       (54, 27, 15, 4.0),
       (55, 28, 15, 3.5);
-- Neutral review

-- Comment 16-19 (SPU 10: 优衣库 T-shirt)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (56, 32, 16, 4.5),
       (57, 33, 16, 4.5),
       (58, 34, 16, 4.0),
       (59, 32, 17, 4.0),
       (60, 33, 17, 4.0),
       (61, 34, 17, 4.0),
       (62, 32, 18, 3.0),
       (63, 33, 18, 4.0),
       (64, 34, 18, 2.0), -- Bad review
       (65, 32, 19, 3.0),
       (66, 33, 19, 4.5),
       (67, 34, 19, 3.5);
-- Neutral review

-- Comment 20-23 (SPU 11: IKEA Sofa)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (68, 35, 20, 4.0),
       (69, 36, 20, 3.0),
       (70, 37, 20, 3.5),
       (71, 38, 20, 4.0),
       (72, 35, 21, 4.5),
       (73, 36, 21, 4.0),
       (74, 37, 21, 4.0),
       (75, 38, 21, 2.0), -- Bad review on assembly
       (76, 35, 22, 5.0),
       (77, 36, 22, 4.5),
       (78, 37, 22, 4.0),
       (79, 38, 22, 4.5),
       (80, 35, 23, 4.0),
       (81, 36, 23, 2.5),
       (82, 37, 23, 3.5),
       (83, 38, 23, 4.0);
-- Bad review on comfort

-- Comment 24-26 (SPU 15: Lululemon Align)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (84, 46, 24, 5.0),
       (85, 47, 24, 4.0),
       (86, 48, 24, 4.5),
       (87, 46, 25, 5.0),
       (88, 47, 25, 3.5),
       (89, 48, 25, 5.0),
       (90, 46, 26, 5.0),
       (91, 47, 26, 4.5),
       (92, 48, 26, 5.0);

-- Comment 27-29 (SPU 17: Logitech MX Keys S)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (93, 51, 27, 5.0),
       (94, 52, 27, 5.0),
       (95, 53, 27, 5.0),
       (96, 54, 27, 4.5),
       (97, 51, 28, 5.0),
       (98, 52, 28, 5.0),
       (99, 53, 28, 5.0),
       (100, 54, 28, 5.0),
       (101, 51, 29, 4.0),
       (102, 52, 29, 3.0),
       (103, 53, 29, 4.0),
       (104, 54, 29, 4.0);
-- Neutral review on connectivity

-- Comment 30-33 (SPU 20: Herman Miller Aeron)
INSERT INTO spu_comment_dimension_rating (id, dimension_id, comment_id, rating) OVERRIDING SYSTEM VALUE
VALUES (105, 61, 30, 5.0),
       (106, 62, 30, 5.0),
       (107, 63, 30, 4.5),
       (108, 61, 31, 5.0),
       (109, 62, 31, 5.0),
       (110, 63, 31, 3.0), -- Bad review on price
       (111, 61, 32, 5.0),
       (112, 62, 32, 5.0),
       (113, 63, 32, 5.0),
       (114, 61, 33, 5.0),
       (115, 62, 33, 5.0),
       (116, 63, 33, 5.0);

-- =================================================================
-- 11. 刷新物化视图 (Materialized Views)
-- =================================================================
-- 插入数据后，您需要手动刷新物化视图来更新它们的内容
REFRESH MATERIALIZED VIEW spu_positive_rating_ratio_mv;
REFRESH MATERIALIZED VIEW spu_comment_count_mv;

