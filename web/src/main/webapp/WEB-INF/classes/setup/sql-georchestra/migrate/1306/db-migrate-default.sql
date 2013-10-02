BEGIN;

-- Version update
UPDATE Settings SET value='13.06' WHERE name='version';
UPDATE Settings SET value='0' WHERE name='subVersion';

COMMIT;
