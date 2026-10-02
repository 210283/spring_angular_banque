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

INSERT INTO linked_savings_accounts (savings_account_number, current_account_number)
SELECT savings.account_number, current.account_number
FROM beneficiaries current_link
JOIN accounts current ON current.account_number = current_link.owner_account_number
JOIN accounts savings ON savings.account_number = current_link.target_account_number
JOIN beneficiaries reverse_link
		ON reverse_link.owner_account_number = savings.account_number
		AND reverse_link.target_account_number = current.account_number
WHERE current.account_type = 'CURRENT'
	AND savings.account_type <> 'CURRENT'
	AND current.owner = savings.owner
ON CONFLICT (savings_account_number) DO NOTHING;
