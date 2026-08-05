@echo off
echo Starting RepSync...
call mvnw.cmd clean compile exec:java "-Dexec.mainClass=com.repsync.Main"
pause
