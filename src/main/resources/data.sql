-- 관리자 계정 생성
INSERT INTO users (email, password, nickname, name, role, status)
VALUES ('admin@imprint.com', 'admin123', 'MasterAdmin', '관리자', 2, 'ACTIVE');

-- 시스템 설정값
INSERT INTO system_settings (id, total_storage_limit)
VALUES (1, 10737418240);