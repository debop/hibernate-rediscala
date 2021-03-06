package org.hibernate.cache.rediscala.serializer

import org.xerial.snappy.Snappy

/**
 * RedisSerializer by Snappy compress library
 * Created by debop on 2014. 3. 30.
 */
class SnappyRedisSerializer[T](val inner: RedisSerializer[T]) extends RedisSerializer[T] {

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null || graph == None)
      return EMPTY_BYTES

    Snappy.compress(inner.serialize(graph))
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    inner.deserialize(Snappy.uncompress(bytes))
  }
}

object SnappyRedisSerializer {

  def apply[T](inner: RedisSerializer[T] = new FstRedisSerializer[T]()): SnappyRedisSerializer[T] = {
    new SnappyRedisSerializer[T](inner)
  }
}
