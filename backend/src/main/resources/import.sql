INSERT INTO accounts (account_number, owner, balance, version)
VALUES ('FR761234567', 'Alice', 1000.00, 0)
ON CONFLICT (account_number) DO NOTHING;

INSERT INTO accounts (account_number, owner, balance, version)
VALUES ('FR769876567', 'Bob', 500.00, 0)
ON CONFLICT (account_number) DO NOTHING;

INSERT INTO accounts (account_number, owner, balance, version)
VALUES ('FR769876589', 'John', 600.00, 0)
ON CONFLICT (account_number) DO NOTHING;

INSERT INTO beneficiaries (id, label, target_account_number, owner_account_number, version)
VALUES ('benef-001', 'John', 'FR769876589', 'FR761234567', 0)
ON CONFLICT (id) DO NOTHING;
