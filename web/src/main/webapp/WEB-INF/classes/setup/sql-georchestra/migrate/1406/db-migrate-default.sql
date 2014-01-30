BEGIN;
UPDATE Settings SET value='14.06' WHERE name='version';
UPDATE Settings SET value='0' WHERE name='subVersion';
COMMIT;
