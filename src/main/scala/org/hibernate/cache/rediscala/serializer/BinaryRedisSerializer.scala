package org.hibernate.cache.rediscala.serializer

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import org.hibernate.cache.rediscala.utils.Closer._

/**
 * Java Built-in Serializer
 * Created by debop on 2014. 3. 30.
 */
class BinaryRedisSerializer[T] extends RedisSerializer[T] {

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null) return EMPTY_BYTES

    using(new ByteArrayOutputStream()) { bos =>
      using(new ObjectOutputStream(bos)) { oos =>
        oos.writeObject(graph)
      }
      bos.toByteArray
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    using(new ByteArrayInputStream(bytes)) { bis =>
      using(new ObjectInputStream(bis)) { ois =>
        ois.readObject().asInstanceOf[T]
      }
    }
  }
}

object BinaryRedisSerializer {

  def apply[T](): BinaryRedisSerializer[T] = new BinaryRedisSerializer[T]()
}
