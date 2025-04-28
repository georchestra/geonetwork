Only if you upgrade geonetwork to 4.4.x and laters, verify that config-editor are up-to-date like in here :
https://github.com/geonetwork/core-geonetwork/pull/8701 
cf issue https://github.com/geonetwork/core-geonetwork/issues/8700

Connect to the database

Select one uuid to test if this script works
for the example we use d9bf9a9b-cb41-4f95-a7b4-a536e05c2222

execute this sql : 
```
UPDATE
  geonetwork.metadata
SET
  data = REGEXP_REPLACE(data, '<cit:citedResponsibleParty>[\S*\s*.*]* uuid="(.*)" xsi[\S*\s*.*]*</cit:citedResponsibleParty>', '<cit:citedResponsibleParty xlink:href="local://srv/api/registries/entries/\1?lang=fre&amp;process=cit:role/cit:CI_RoleCode/@codeListValue~originator&amp;schema=iso19115-3.2018" />')
WHERE
 "uuid" = 'd9bf9a9b-cb41-4f95-a7b4-a536e05c2222';

UPDATE
  geonetwork.metadata
SET
  data = REGEXP_REPLACE(data, '<mdb:contact>[\S*\s*.*]* uuid="(.*)" xsi[\S*\s*.*]*</mdb:contact>', '<mdb:contact xlink:href="local://srv/api/registries/entries/\1?lang=fre&amp;process=cit:role/cit:CI_RoleCode/@codeListValue~pointOfContact&amp;schema=iso19115-3.2018" />')
WHERE
 "uuid" = 'd9bf9a9b-cb41-4f95-a7b4-a536e05c2222';

UPDATE
  geonetwork.metadata
SET
  data = REGEXP_REPLACE(data, '<mri:pointOfContact>[\S*\s*.*]* uuid="(.*)" xsi[\S*\s*.*]*</mri:pointOfContact>', '<mri:pointOfContact xlink:href="local://srv/api/registries/entries/\1?lang=fre&amp;process=cit:role/cit:CI_RoleCode/@codeListValue~pointOfContact&amp;schema=iso19115-3.2018" />')
WHERE
 "uuid" = 'd9bf9a9b-cb41-4f95-a7b4-a536e05c2222';
```

you need to run an indexation to update the metadata.
Now the metadata should be connected to your contacts (you can see that in /geonetwork/srv/eng/catalog.edit#/directory)
If yes you can try removing the WHERE condition to apply it to all metadata.

note that if you update a contact the metadata will not be up-to-date untill a reindexation
