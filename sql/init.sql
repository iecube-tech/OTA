/*
 Navicat Premium Dump SQL

 Source Server         : 192.168.1.5
 Source Server Type    : MySQL
 Source Server Version : 50739 (5.7.39)
 Source Host           : 192.168.1.5:3306
 Source Schema         : ota

 Target Server Type    : MySQL
 Target Server Version : 50739 (5.7.39)
 File Encoding         : 65001

 Date: 14/11/2024 17:21:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_members
-- ----------------------------
DROP TABLE IF EXISTS `admin_members`;
CREATE TABLE `admin_members`  (
                                  `id` bigint(22) NOT NULL AUTO_INCREMENT,
                                  `union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `avatar` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `creator_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `creator_union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `creator_avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for firmware
-- ----------------------------
DROP TABLE IF EXISTS `firmware`;
CREATE TABLE `firmware`  (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `node_id` bigint(20) NOT NULL,
                             `resource_id` bigint(20) NOT NULL,
                             `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                             `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `is_full` int(1) NOT NULL DEFAULT 0,
                             `timestamp` bigint(20) NOT NULL,
                             `cdn` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
                             `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                             `examine_union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `examine_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `examine_message_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `examine_time` datetime NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for p_manage
-- ----------------------------
DROP TABLE IF EXISTS `p_manage`;
CREATE TABLE `p_manage`  (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `node_id` bigint(20) NOT NULL,
                             `pm_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `manage_id` varbinary(255) NULL DEFAULT NULL,
                             `developer_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_tree
-- ----------------------------
DROP TABLE IF EXISTS `product_tree`;
CREATE TABLE `product_tree`  (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `p_id` bigint(20) NOT NULL,
                                 `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                 `type` bigint(20) NOT NULL,
                                 `edit` bigint(20) NOT NULL,
                                 `deep` bigint(20) NOT NULL COMMENT '节点在整棵树中的深度\r\n',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

INSERT INTO `ota`.`product_tree` (id, p_id, name, type, edit) values (NULL, 0, 'IECUBE产品', 1, 0);

-- ----------------------------
-- Table structure for production
-- ----------------------------
DROP TABLE IF EXISTS `production`;
CREATE TABLE `production`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `product_id` bigint(20) NOT NULL,
                               `firmware_id` bigint(20) NOT NULL,
                               `examine_id` bigint(20) NOT NULL,
                               PRIMARY KEY (`id`, `product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for production_examine
-- ----------------------------
DROP TABLE IF EXISTS `production_examine`;
CREATE TABLE `production_examine`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `product_id` bigint(20) NOT NULL,
                                       `firmware_id` bigint(20) NOT NULL,
                                       `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `create_time` datetime NOT NULL,
                                       `examine_union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `examine_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `examine_message_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `examine_time` datetime NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for production_members
-- ----------------------------
DROP TABLE IF EXISTS `production_members`;
CREATE TABLE `production_members`  (
                                       `id` bigint(22) NOT NULL AUTO_INCREMENT,
                                       `union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `avatar` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `creator_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `creator_union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `creator_avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resources
-- ----------------------------
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `origin_filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `size` bigint(20) NULL DEFAULT NULL,
                              `md5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `create_time` datetime NULL DEFAULT NULL,
                              `last_modified_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `last_modified_time` datetime NULL DEFAULT NULL,
                              `link` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for terminal
-- ----------------------------
DROP TABLE IF EXISTS `terminal`;
CREATE TABLE `terminal`  (
                             `did` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                             `product_id` bigint(20) NOT NULL,
                             `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `fun` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `time_stamp` bigint(20) NULL DEFAULT NULL,
                             `connecting` int(8) UNSIGNED ZEROFILL NOT NULL,
                             `active_disconnection` int(8) UNSIGNED ZEROFILL NOT NULL,
                             `status` int(8) NULL DEFAULT NULL,
                             PRIMARY KEY (`did`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
