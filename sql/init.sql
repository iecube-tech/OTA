CREATE TABLE `ota`.`firmware`  (
                                   `id` bigint(0) NOT NULL AUTO_INCREMENT,
                                   `node_id` bigint(0) NOT NULL,
                                   `resource_id` bigint(0) NOT NULL,
                                   `timestamp` bigint(0) NOT NULL,
                                   `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `ota`.`product_tree`  (
                                   `id` bigint(0) NOT NULL AUTO_INCREMENT,
                                   `p_id` bigint(0) NOT NULL,
                                   `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `type` bigint(0) DEFAULT NULL,
                                   `edit` bigint(0) DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `ota`.`product_tree` (id, p_id, name, type, edit) values (NULL, 0, 'IECUBE', 1, 0);

CREATE TABLE `ota`.`resources`  (
                                   `id` bigint(0) NOT NULL AUTO_INCREMENT,
                                   `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `origin_filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `size` bigint(0) DEFAULT NULL,
                                   `md5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   `creator` bigint(0) DEFAULT NULL,
                                   `create_time` datetime(0) DEFAULT NULL,
                                   `last_modified_user` bigint(0) DEFAULT NULL,
                                   `last_modified_time` datetime(0) DEFAULT NULL,
                                   `link` longtext CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `ota`.`terminal`  (
                                   `did` varchar(255) NOT NULL,
                                   `product_id` bigint(0) NOT NULL,
                                   `name` varchar(255),
                                   `fun` varchar(255),
                                   `version` varchar(255),
                                   `time_stamp` bigint(0),
                                   `connecting` int(8) ZEROFILL NOT NULL,
                                   `active_disconnection` int(8) ZEROFILL NOT NULL,
                                   `status` int(8),
                                   PRIMARY KEY (`did`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

