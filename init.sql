-- ============================================================
-- 图书借阅管理系统 · 数据库初始化脚本
-- 用法：mysql -uroot -p < init.sql
-- 注意：会删除 book 库中相关业务表后重建（含演示数据）
-- ============================================================

CREATE DATABASE IF NOT EXISTS book
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE book;

-- ----------------------------
-- 重建业务表
-- ----------------------------
DROP TABLE IF EXISTS `t_borrow_record`;
DROP TABLE IF EXISTS `t_reader`;
DROP TABLE IF EXISTS `t_book`;
DROP TABLE IF EXISTS `t_category`;
DROP TABLE IF EXISTS `t_admin_user`;

-- ----------------------------
-- 1. 管理员
-- ----------------------------
CREATE TABLE `t_admin_user`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `username`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '用户名',
    `password`        VARCHAR(100) NOT NULL DEFAULT '' COMMENT '密码',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) COMMENT ='后台管理员';

INSERT INTO `t_admin_user` (`id`, `username`, `password`, `last_login_time`)
VALUES (1, 'admin', '111111', NOW());

-- ----------------------------
-- 2. 图书分类
-- ----------------------------
CREATE TABLE `t_category`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL DEFAULT '' COMMENT '分类名称',
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) COMMENT ='图书分类';

INSERT INTO `t_category` (`id`, `name`)
VALUES (1, '计算机科学'),
       (2, '古典名著'),
       (3, '文学小说'),
       (4, '历史地理'),
       (5, '科普百科'),
       (6, '少儿读物');

-- ----------------------------
-- 3. 图书
-- cover 统一使用本地封面：/file/5793c405e8ef467c929519b39d9d92dc.png
-- available_count 已按下方演示借阅数据扣减
-- ----------------------------
CREATE TABLE `t_book`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `isbn`            VARCHAR(64)  NOT NULL DEFAULT '' COMMENT 'ISBN/编号',
    `name`            VARCHAR(100) NOT NULL DEFAULT '' COMMENT '书名',
    `cover`           VARCHAR(512) NOT NULL DEFAULT '' COMMENT '封面地址',
    `category_id`     BIGINT                DEFAULT NULL COMMENT '分类ID',
    `author`          VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '作者',
    `press`           VARCHAR(100) NOT NULL DEFAULT '' COMMENT '出版社',
    `description`     VARCHAR(500) NOT NULL DEFAULT '' COMMENT '简介',
    `price`           BIGINT       NOT NULL DEFAULT 0 COMMENT '单价（分）',
    `count`           INT          NOT NULL DEFAULT 0 COMMENT '馆藏总数',
    `available_count` INT          NOT NULL DEFAULT 0 COMMENT '可借数量',
    `status`          CHAR(1)      NOT NULL DEFAULT '1' COMMENT '1上架 0下架',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_name` (`name`)
) COMMENT ='图书';

INSERT INTO `t_book`
(`id`, `isbn`, `name`, `cover`, `category_id`, `author`, `press`, `description`, `price`, `count`, `available_count`, `status`)
VALUES
(1,  '9780131872486', 'Java编程思想（第4版）',                 '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, 'Bruce Eckel',           '机械工业出版社', 'Java 学习经典著作。', 10800, 20, 18, '1'),
(2,  '9787115151476', 'MySQL必知必会',                        '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, 'Ben Forta',             '人民邮电出版社', '实用性强的 MySQL 入门书。', 3900, 15, 15, '1'),
(3,  '9787115428028', '深入浅出Vue.js',                       '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, '刘博文',                 '人民邮电出版社', '讲解 Vue.js 原理与设计思想。', 6900, 12, 11, '1'),
(4,  '9787020008728', '水浒传',                               '/file/5793c405e8ef467c929519b39d9d92dc.png', 2, '施耐庵',                 '人民文学出版社', '中国四大名著之一。', 4500, 25, 24, '1'),
(5,  '9787121385551', 'Kubernetes权威指南',                   '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, '龚正 等',                '电子工业出版社', 'K8s 运维与开发指南。', 12800, 10, 10, '1'),
(6,  '9787302150831', 'C程序设计（第4版）',                     '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, '谭浩强',                 '清华大学出版社', 'C 语言经典教材。', 3200, 30, 30, '1'),
(7,  '9787020008729', '三国演义',                             '/file/5793c405e8ef467c929519b39d9d92dc.png', 2, '罗贯中',                 '人民文学出版社', '长篇章回体历史演义小说。', 4800, 25, 24, '1'),
(8,  '9787020008735', '红楼梦',                               '/file/5793c405e8ef467c929519b39d9d92dc.png', 2, '曹雪芹',                 '人民文学出版社', '中国古典小说巅峰之作。', 5200, 20, 19, '1'),
(9,  '9787020008742', '西游记',                               '/file/5793c405e8ef467c929519b39d9d92dc.png', 2, '吴承恩',                 '人民文学出版社', '神魔小说代表作。', 4600, 22, 22, '1'),
(10, '9787544258999', '百年孤独',                             '/file/5793c405e8ef467c929519b39d9d92dc.png', 3, '加西亚·马尔克斯',         '南海出版公司',   '魔幻现实主义文学代表作。', 5500, 15, 14, '1'),
(11, '9787020125784', '活着',                                 '/file/5793c405e8ef467c929519b39d9d92dc.png', 3, '余华',                   '作家出版社',     '当代文学经典。', 2800, 18, 17, '1'),
(12, '9787532776771', '挪威的森林',                           '/file/5793c405e8ef467c929519b39d9d92dc.png', 3, '村上春树',               '上海译文出版社', '青春与成长的故事。', 3900, 16, 16, '1'),
(13, '9787101003048', '史记（精选）',                          '/file/5793c405e8ef467c929519b39d9d92dc.png', 4, '司马迁',                 '中华书局',       '中国第一部纪传体通史。', 6800, 12, 12, '1'),
(14, '9787108060952', '人类简史',                             '/file/5793c405e8ef467c929519b39d9d92dc.png', 4, '尤瓦尔·赫拉利',           '中信出版社',     '从认知革命到科学革命。', 6800, 14, 13, '1'),
(15, '9787535794529', '时间简史',                             '/file/5793c405e8ef467c929519b39d9d92dc.png', 5, '史蒂芬·霍金',             '湖南科技出版社', '探索宇宙起源的科普经典。', 4500, 20, 20, '1'),
(16, '9787111544937', '深入理解计算机系统',                     '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, 'Randal E. Bryant',      '机械工业出版社', 'CSAPP 殿堂级教材。', 13900, 8, 7, '1'),
(17, '9787111213826', '设计模式',                             '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, 'Erich Gamma 等',        '机械工业出版社', 'GoF 设计模式经典。', 4500, 10, 10, '1'),
(18, '9787115428029', '算法（第4版）',                          '/file/5793c405e8ef467c929519b39d9d92dc.png', 1, 'Robert Sedgewick',      '人民邮电出版社', '算法与数据结构教材。', 9900, 12, 11, '1'),
(19, '9787020116133', '小王子',                               '/file/5793c405e8ef467c929519b39d9d92dc.png', 6, '安托万·德·圣埃克苏佩里',   '人民文学出版社', '写给大人的童话。', 2200, 30, 29, '1'),
(20, '9787532768530', '哈利·波特与魔法石',                     '/file/5793c405e8ef467c929519b39d9d92dc.png', 6, 'J.K.罗琳',               '上海译文出版社', '魔法世界的开端。', 3600, 25, 24, '1');

-- ----------------------------
-- 4. 读者
-- ----------------------------
CREATE TABLE `t_reader`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `card_no`     VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '借阅卡号',
    `name`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '姓名',
    `phone`       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    `gender`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '0女 1男',
    `status`      CHAR(1)      NOT NULL DEFAULT '1' COMMENT '1正常 0冻结',
    `max_borrow`  INT          NOT NULL DEFAULT 5 COMMENT '最大可借册数',
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_card_no` (`card_no`)
) COMMENT ='读者';

INSERT INTO `t_reader` (`id`, `card_no`, `name`, `phone`, `gender`, `status`, `max_borrow`)
VALUES (1, 'R20260001', '张三', '13800138001', '1', '1', 5),
       (2, 'R20260002', '李四', '13800138002', '1', '1', 5),
       (3, 'R20260003', '王芳', '13800138003', '0', '1', 5),
       (4, 'R20260004', '赵敏', '13800138004', '0', '1', 5),
       (5, 'R20260005', '陈伟', '13800138005', '1', '1', 3);

-- ----------------------------
-- 5. 借阅记录
-- status: 0借阅中 1已归还 2逾期
-- 默认借期 30 天
-- ----------------------------
CREATE TABLE `t_borrow_record`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `reader_id`   BIGINT       NOT NULL COMMENT '读者ID',
    `book_id`     BIGINT       NOT NULL COMMENT '图书ID',
    `borrow_time` DATETIME     NOT NULL COMMENT '借出时间',
    `due_time`    DATETIME     NOT NULL COMMENT '应还时间',
    `return_time` DATETIME              DEFAULT NULL COMMENT '实际归还时间',
    `status`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '0借阅中 1已归还 2逾期',
    `remark`      VARCHAR(255)          DEFAULT '' COMMENT '备注',
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_reader` (`reader_id`),
    KEY `idx_book` (`book_id`),
    KEY `idx_status` (`status`)
) COMMENT ='借阅记录';

INSERT INTO `t_borrow_record`
(`reader_id`, `book_id`, `borrow_time`, `due_time`, `return_time`, `status`, `remark`)
VALUES
-- 借阅中
(1, 1,  DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 10 DAY), INTERVAL 30 DAY), NULL, '0', '借期30天'),
(1, 11, DATE_SUB(NOW(), INTERVAL 5 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(2, 3,  DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 8 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(3, 4,  DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 15 DAY), INTERVAL 30 DAY), NULL, '0', '借期30天'),
(3, 8,  DATE_SUB(NOW(), INTERVAL 3 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(4, 7,  DATE_SUB(NOW(), INTERVAL 2 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(4, 10, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 12 DAY), INTERVAL 30 DAY), NULL, '0', '借期30天'),
(4, 14, DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 9 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(5, 16, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 20 DAY), INTERVAL 30 DAY), NULL, '0', '借期30天'),
(5, 19, DATE_SUB(NOW(), INTERVAL 1 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(5, 20, DATE_SUB(NOW(), INTERVAL 7 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 7 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
(2, 18, DATE_SUB(NOW(), INTERVAL 6 DAY),  DATE_ADD(DATE_SUB(NOW(), INTERVAL 6 DAY), INTERVAL 30 DAY),  NULL, '0', '借期30天'),
-- 逾期（借出 40 天前，应还 10 天前）
(2, 1,  DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, '2', '已逾期'),
-- 已归还
(3, 4,  DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY), '1', '借期30天'),
(1, 6,  DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), '1', '借期30天');

-- ----------------------------
-- 演示账号
-- 管理端：admin / 111111
-- 读者卡号：R20260001 ~ R20260005，密码 123456
-- ----------------------------
