@echo off
java -cp utilities\RevisionUpdater\bin ServerRevisionUpdater "%CD%\SWGCombined\src\Constants.java" "%CD%\.svn\entries"
pause