package org.hibernate.cache.rediscala.serializer

/**
 * RedisSerializer
 * Created by debop on 2014. 3. 30.
 */
trait RedisSerializer[T] {

  val EMPTY_BYTES = Array[Byte]()

  def serialize(graph: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T

}
