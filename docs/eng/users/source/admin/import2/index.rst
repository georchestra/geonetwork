.. _import2:

Import / export tools
=====================

Introduction
------------

Using GAST, you can import and export metadata at will. It allows you to:

#.  Create a *backup* of the entire metadata set.
    Each metadata has its own file, including maps and other data files.
    Once you have the backup, you can decide to import all or only some of them.

#.  *Move your metadata* from one GeoNetwork catalogue to another. This
    can be done to mirror your metadata or to upgrade an old installation.
    In the last case, you export your metadata from your old installation
    and then re-import them into the new one.

#.  Fill the system with *test data*. Using the ’skip UUID’ option, you can
    re-import the same metadata over and over again. This is useful, for
    example, if you want to perform stress tests.

Metadata are exported in the MEF format.

.. warning:: **Ownership** - Please, consider that the MEF format version 1.0 does not take into account user and group ownership. When exporting metadata, you loose this information. When importing metadata, the new owner becomes the user that is performing the import while the group ownership is set to null.

Import
------

This tool is located under Management tools on the left panel and
allows you to import a set of metadata that have been previously exported using
the export facility. Selecting the Import tool opens the option
panel.

.. figure:: gast-mef-import.png

    *The metadata import panel*

- **Input folder** - The source folder in your system that GAST will scan to collect metadata to import. GAST will try to import all files with the MEF extension.

- **Browse button** - Navigate through your file system to choose an output location or enter it manually into the text field.

- **Import** - This will start the process. A progress dialogue will be opened to show the import status.

.. note:: Sub-folders are not scanned.

Export
------

This tool is located under the Management tool on the left panel and
allows you to export a set of metadata using the MEF format. Selecting the Export
tool opens the option panel.

.. figure:: gast-mef-export.png

    *The metadata export panel*
    
- **Output folder** - 
  The target folder in your file system where GAST will put the
  exported metadata. You can either select the Browse button to navigate through
  your file system to choose a better location or enter it manually in the
  text field.

- **Format** - 
  Here you can specify the metadata’s output format. See the MEF
  specification for more information.

- **Skip UUID** - 
  Normally this option is not required. If you
  select it, you will loose the metadata’s unique identifier (UUID) but you will
  be able to re-import that metadata over and over again. This is useful to fill
  the system with test data.

- **Search** - Allows to specify free text search criteria to limit the set of exported records.
  
- **Export** - This will start the export process. A progress dialogue will be opened to show the export status.

.. note:: The result of the export will depend on the access privileges of the user. If you do not authenticate, you will get only public metadata.

.. warning::
   Skipping the UUID on import or export can cause metadata to be duplicated.
   This should normally always be avoided
