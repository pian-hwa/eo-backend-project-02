-- 유저 테이블
CREATE TABLE `users` (
                         `id`              BIGINT       NOT NULL AUTO_INCREMENT,
                         `email`           VARCHAR(100) NOT NULL UNIQUE,      -- id 와 같이 사용
                         `password`        VARCHAR(255) NOT NULL,             -- 암호화가 되야함(test 부분에서 작성하여 암호화한뒤 저장)
                         `nickname`        VARCHAR(15)  NOT NULL UNIQUE,
                         `name`            VARCHAR(50)  NOT NULL,
                         `role`            INT          NOT NULL DEFAULT 0,   -- 0-user, 1-manager, 2-admin
                         `status`          VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- pending, active, banned
                         `created_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT `PK_USERS` PRIMARY KEY (`id`)
);

-- 이메일 인증 관리 (회원가입/비번찾기 전용)
CREATE TABLE `email_verifications` (
                                       `id`              BIGINT       NOT NULL AUTO_INCREMENT,
                                       `email`           VARCHAR(100) NOT NULL,
                                       `auth_code`       VARCHAR(10)  NOT NULL,
                                       `verif_type`      VARCHAR(20)  NOT NULL,         -- 'join', 'password'
                                       `is_verified`     BOOLEAN      NOT NULL DEFAULT FALSE,
                                       `created_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       `expires_at`      TIMESTAMP    NOT NULL,

                                       CONSTRAINT `PK_EMAIL_VERIF` PRIMARY KEY (`id`)
);

-- 게시판 테이블
CREATE TABLE `boards` (
                          `id`         BIGINT      NOT NULL AUTO_INCREMENT,
                          `name`       VARCHAR(50) NOT NULL UNIQUE,
                          `created_at` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT `PK_BOARDS` PRIMARY KEY (`id`)
);

-- 게시판별 매니저 지정
CREATE TABLE `board_managers` (
                                  `id`         BIGINT    NOT NULL AUTO_INCREMENT,
                                  `board_id`   BIGINT    NOT NULL,
                                  `user_id`    BIGINT    NOT NULL,
                                  `assigned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT `PK_BOARD_MANAGERS` PRIMARY KEY (`id`),
                                  CONSTRAINT `FK_board_TO_mgr` FOREIGN KEY (`board_id`) REFERENCES `boards` (`id`),
                                  CONSTRAINT `FK_user_TO_mgr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

-- 게시물 (추천수 컬럼 포함)
CREATE TABLE `posts` (
                         `id`               BIGINT      NOT NULL AUTO_INCREMENT,
                         `title`            VARCHAR(80) NOT NULL,
                         `content`          TEXT        NOT NULL,
                         `hits`             INT         NOT NULL DEFAULT 0,
                         `recommend_count`  INT         NOT NULL DEFAULT 0, -- 추천수 확인
                         `user_id`          BIGINT      NOT NULL,
                         `board_id`         BIGINT      NOT NULL,
                         `created_at`       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at`       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT `PK_POSTS` PRIMARY KEY (`id`),
                         CONSTRAINT `FK_user_TO_post` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                         CONSTRAINT `FK_board_TO_post` FOREIGN KEY (`board_id`) REFERENCES `boards` (`id`)
);

-- 추천 중복 방지 테이블
CREATE TABLE `post_recommends` (
                                   `id`         BIGINT    NOT NULL AUTO_INCREMENT,
                                   `post_id`    BIGINT    NOT NULL,
                                   `user_id`    BIGINT    NOT NULL,
                                   `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT `PK_POST_RECOMMENDS` PRIMARY KEY (`id`),
                                   CONSTRAINT `UK_post_user_recommend` UNIQUE (`post_id`, `user_id`),
                                   CONSTRAINT `FK_post_TO_recommends` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
                                   CONSTRAINT `FK_user_TO_recommends` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

-- 댓글
CREATE TABLE `comments` (
                            `id`         BIGINT    NOT NULL AUTO_INCREMENT,
                            `content`    TEXT      NOT NULL,
                            `user_id`    BIGINT    NOT NULL,
                            `post_id`    BIGINT    NOT NULL,
                            `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT `PK_COMMENTS` PRIMARY KEY (`id`),
                            CONSTRAINT `FK_user_TO_comm` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                            CONSTRAINT `FK_post_TO_comm` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
);

-- 쪽지 테이블
CREATE TABLE `messages` (
                            `id`                  BIGINT        NOT NULL AUTO_INCREMENT,
                            `content`             VARCHAR(1000) NOT NULL,
                            `sender_id`           BIGINT        NOT NULL,
                            `receiver_id`         BIGINT        NOT NULL,
                            `sent_at`             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `read_at`             TIMESTAMP     NULL,
                            `is_archived_sender`  BOOLEAN       NOT NULL DEFAULT FALSE, -- 보관 상태
                            `is_archived_receiver` BOOLEAN      NOT NULL DEFAULT FALSE,
                            `is_deleted_sender`   BOOLEAN       NOT NULL DEFAULT FALSE, -- 삭제 상태
                            `is_deleted_receiver` BOOLEAN       NOT NULL DEFAULT FALSE,

                            CONSTRAINT `PK_MESSAGES` PRIMARY KEY (`id`),
                            CONSTRAINT `FK_sender_TO_user` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
                            CONSTRAINT `FK_receiver_TO_user` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
);

-- 사용자별 쪽지 설정
CREATE TABLE `user_message_settings` (
                                         `user_id`           BIGINT NOT NULL,
                                         `max_count`         INT    NOT NULL DEFAULT 100,
                                         `warning_threshold` INT    NOT NULL DEFAULT 80,

                                         CONSTRAINT `PK_MSG_SETTINGS` PRIMARY KEY (`user_id`),
                                         CONSTRAINT `FK_user_TO_settings` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

-- 첨부파일 (용량 추적 포함)
CREATE TABLE `attachments` (
                               `id`          BIGINT       NOT NULL AUTO_INCREMENT,
                               `origin_name` VARCHAR(255) NOT NULL,
                               `stored_name` VARCHAR(255) NOT NULL,
                               `file_size`   BIGINT       NOT NULL, -- 대시보드 통계용 용량 데이터
                               `target_type` VARCHAR(20)  NOT NULL, -- 'post' 또는 'comment'
                               `target_id`   BIGINT       NOT NULL,
                               `uploader_id` BIGINT       NOT NULL,
                               `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT `PK_ATTACHMENTS` PRIMARY KEY (`id`),
                               CONSTRAINT `FK_user_TO_file` FOREIGN KEY (`uploader_id`) REFERENCES `users` (`id`)
);

-- 시스템 전체 설정 (저장공간 한도 등)
CREATE TABLE `system_settings` (
                                   `id`                  INT    PRIMARY KEY,
                                   `total_storage_limit` BIGINT NOT NULL,
                                   `updated_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 신고함 (항목 체크형)
CREATE TABLE `reports` (
                           `id`              BIGINT        NOT NULL AUTO_INCREMENT,
                           `target_type`     VARCHAR(20)   NOT NULL,
                           `target_id`       BIGINT        NOT NULL,
                           `report_category` VARCHAR(50)   NOT NULL, -- 카테고리
                           `content`         VARCHAR(1000),          -- 상세 사유
                           `reporter_id`     BIGINT        NOT NULL,
                           `status`          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
                           `admin_answer`    VARCHAR(1000),          -- 관리자 답변
                           `created_at`      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT `PK_REPORTS` PRIMARY KEY (`id`),
                           CONSTRAINT `FK_reporter_TO_user` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`)
);

-- 1:1 문의함
CREATE TABLE `supports` (
                            `id`          BIGINT    NOT NULL AUTO_INCREMENT,
                            `content`     TEXT      NOT NULL,
                            `user_id`     BIGINT    NOT NULL,
                            `status`      VARCHAR(20) DEFAULT 'OPEN',
                            `answer`      TEXT,
                            `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT `PK_SUPPORTS` PRIMARY KEY (`id`),
                            CONSTRAINT `FK_user_TO_support` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);