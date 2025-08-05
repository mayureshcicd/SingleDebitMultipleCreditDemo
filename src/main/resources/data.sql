-- Clear existing data
TRUNCATE TABLE account;
TRUNCATE TABLE account_transaction;

-- Insert fresh records
INSERT INTO account (name, balance) VALUES ('Customer A', 1000000.00);
INSERT INTO account (name, balance) VALUES ('Customer B', 1.00);
INSERT INTO account (name, balance) VALUES ('Customer C', 1.00);


INSERT INTO account (name, balance) VALUES ('Customer D', 2000000.00);
INSERT INTO account (name, balance) VALUES ('Customer E', 1500000.00);
INSERT INTO account (name, balance) VALUES ('Customer F', 1300000.00);
