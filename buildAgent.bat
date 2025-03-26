@echo off

echo Compiling Java files...
javac -cp "C:/iSuite/extlib/*" -d out MemoryMonitorAgent.java MemoryMonitorWatchdog.java MemoryMonitorLogger.java

echo Creating JAR file...
jar cmf MANIFEST.MF MemoryMonitorAgent.jar -C out . 
echo Build process completed.



