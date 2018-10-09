 Command Line for "LoadFromJDBC"
 
 `dse/bin/dse spark-submit --packages mysql:mysql-connector-java:5.1.6 --class com.datastax.demo.LoadFromJDBC jdbc_2.10-0.1.jar`
 
 Command Line for "LoadFromCSV"
 
 `dse/bin/dse spark-submit --class com.datastax.demo.LoadFromCSV textfiles_2.10-0.1.jar`
 
 Command Line for "WriteToCSV"
  
  `dse/bin/dse spark-submit --class com.datastax.demo.WriteToCSV textfiles_2.10-0.1.jar`