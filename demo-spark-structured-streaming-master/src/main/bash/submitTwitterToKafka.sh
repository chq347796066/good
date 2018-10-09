#!/usr/bin/env bash
#pwd
#cd ../../..
#pwd

# --files <location_to_your_app.conf>
# --conf 'spark.executor.extraJavaOptions=-Dconfig.resource=app'
# --conf 'spark.driver.extraJavaOptions=-Dconfig.resource=app'

extraJavaOptions="-DappEnv=dvp"

spark-submit \
--master yarn \
--deploy-mode client \
--conf "spark.executor.extraJavaOptions=${extraJavaOptions}" \
--conf "spark.driver.extraJavaOptions=${extraJavaOptions}" \
--class be.icteam.demo.TwitterToKafkaApp \
--files ./target/classes/log4j.xml \
./target/demo-spark-structured-streaming-0.1.0-SNAPSHOT-jar-with-dependencies.jar
