javac -cp "C:/iSuite/extlib/*" -d out MemoryMonitorAgent.java
jar cmf MANIFEST.MF MemoryMonitorAgent.jar -C out . 
