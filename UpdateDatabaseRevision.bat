@echo off
java -cp utilities\RevisionUpdater\bin DatabaseRevisionUpdater "%CD%\database\DatabaseVersion.sql" "%CD%\.svn\entries"
pause