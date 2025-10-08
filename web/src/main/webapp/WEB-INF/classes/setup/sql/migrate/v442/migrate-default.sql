UPDATE Settings SET value='4.4.2' WHERE name='system/platform/version';
UPDATE Settings SET value='0' WHERE name='system/platform/subVersion';

DELETE FROM Settings WHERE name = 'system/index/indexingTimeRecordLink';
