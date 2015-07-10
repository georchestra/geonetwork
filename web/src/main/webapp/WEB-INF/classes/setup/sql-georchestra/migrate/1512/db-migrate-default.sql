BEGIN;
UPDATE Settings SET value='15.12' WHERE name='version';
UPDATE Settings SET value='0' WHERE name='subVersion';
COMMIT;
