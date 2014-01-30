BEGIN;

-- Version update
UPDATE Settings SET value='14.01' WHERE name='version';
UPDATE Settings SET value='0' WHERE name='subVersion';

COMMIT;
