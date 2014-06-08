package org.hibernate.cache.rediscala.serializer

import akka.util.ByteString
import redis.ByteStringFormatter

abstract class CacheEntryFormatter[T](val serializer: RedisSerializer[T]) extends ByteStringFormatter[T] {

  override def serialize(data: T): ByteString = {
    data match {
      case null => ByteString.empty
      case _ => ByteString(serializer.serialize(data))
    }

  }

  override def deserialize(bs: ByteString): T = {
    bs match {
      case null => null.asInstanceOf[T]
      case _ => serializer.deserialize(bs.toArray)
    }

  }
}

class BinaryCacheEntryFormatter[T]
  extends CacheEntryFormatter[T](new BinaryRedisSerializer[T]()) {}

class FstCacheEntryFormatter[T]
  extends CacheEntryFormatter[T](new FstRedisSerializer[T]()) {}


class SnappyFstCacheEntryFormatter[T]
  extends CacheEntryFormatter[T](SnappyRedisSerializer[T](new FstRedisSerializer[T]())) {}

class SnappyBinaryCacheEntryFormatter[T]
  extends CacheEntryFormatter[T](SnappyRedisSerializer[T](new BinaryRedisSerializer[T]())) {}