package be.icteam.demo.pooling

import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.kafka.clients.producer.KafkaProducer

class KafkaProducerPool[K, V](factory: PooledObjectFactory[KafkaProducer[K, V]])
  extends GenericObjectPool[KafkaProducer[K, V]](factory)
