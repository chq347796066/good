package be.icteam.demo.pooling

import java.util.Properties

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}
import org.apache.kafka.clients.producer.KafkaProducer

class KafkaProducerFactory[K, V](props: Properties) extends BasePooledObjectFactory[KafkaProducer[K, V]] with StrictLogging {

  @Override
  override def wrap(t: KafkaProducer[K, V]): PooledObject[KafkaProducer[K, V]] =
    new DefaultPooledObject[KafkaProducer[K, V]](t)

  @Override
  override def create(): KafkaProducer[K, V] = {
    logger.info("creating a kafka producer...")
    val producer = new KafkaProducer[K, V](props)
    logger.info("created a kafka producer")
    producer
  }

}
