package org.hibernate.cache.rediscala.serializer

import org.hibernate.cache.rediscala.utils.Closer._
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import de.ruedigermoeller.serialization.FSTConfiguration
import scala.util.{Failure, Success, Try}
import org.slf4j.LoggerFactory

/**
 * FstRedisSerializer
 * Created by debop on 2014. 3. 30.
 */
private[rediscala] class FstRedisSerializer[T] extends RedisSerializer[T] {

    private lazy val log = LoggerFactory.getLogger(getClass)

    override def serialize(graph: T): Array[Byte] = {
        if (graph == null || graph == None)
            return EMPTY_BYTES

        using(new ByteArrayOutputStream()) { bos =>
            Try(FstRedisSerializer.DefaultCfg.getObjectOutput(bos)) match {
                case Success(oos) =>
                    oos.writeObject(graph, Seq[Class[_]](): _*)
                    oos.flush()
                    bos.toByteArray
                case Failure(e) =>
                    log.error(s"Fail to serialize graph. $graph", e)
                    EMPTY_BYTES
            }
        }
    }

    override def deserialize(bytes: Array[Byte]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        using(new ByteArrayInputStream(bytes)) { bis =>
            Try(FstRedisSerializer.DefaultCfg.getObjectInput(bis)) match {
                case Success(ois) =>
                    ois.readObject.asInstanceOf[T]

                case Failure(e) =>
                    log.error(s"Fail to deserialize data.", e)
                    null.asInstanceOf[T]
            }
        }
    }
}

private[rediscala] object FstRedisSerializer {

    lazy val DefaultCfg = FSTConfiguration.createDefaultConfiguration()

    def apply[T](): FstRedisSerializer[T] = new FstRedisSerializer[T]()
}
