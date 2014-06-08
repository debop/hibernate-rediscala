package org.hibernate.cache.rediscala.regions

import java.util
import java.util.Properties
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.rediscala.utils.{HibernateRedisUtil, Promises}
import org.hibernate.cache.spi.Region
import org.slf4j.LoggerFactory


abstract class RedisDataRegion(protected val accessStrategyFactory: RedisAccessStrategyFactory,
                               val cache: HibernateRedisCache,
                               val regionName: String,
                               val props: Properties) extends Region {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val expireInSeconds = HibernateRedisUtil.expireInSeconds(regionName)

  override def getName: String = regionName

  var regionDeleted = false

  override def destroy(): Unit = synchronized {
    // NOTE: 분산 캐시 서버에서는 굳이 cache를 삭제할 필요없습니다.
    // NOTE: 다만 분산 캐시 서버에서 꼭 Expire 를 검사한 후 cache 값을 제공해야 합니다.
    log.debug(s"destroy region... region=$regionName")

    //    if (regionDeleted)
    //      return
    //    try {
    //      Promises.await(cache.deleteRegion(regionName))
    //    } finally {
    //      regionDeleted = true
    //    }
  }

  override def contains(key: Any): Boolean = {
    Promises.await(cache.exists(regionName, key.toString))
  }

  override def getSizeInMemory: Long = {
    Promises.await(cache.dbSize)
  }

  override def getElementCountInMemory: Long = {
    Promises.await(cache.keySizeInRegion(regionName))
  }

  override def getElementCountOnDisk: Long = -1L

  override def toMap: util.Map[Any, Any] = {
    val map = Promises.await(cache.getAll(regionName))
    val results = new util.HashMap[Any, Any]()
    map.foreach(x => results.put(x._1.asInstanceOf[Any], x._2.asInstanceOf[Any]))
    results
  }

  override def nextTimestamp: Long = HibernateRedisUtil.nextTimestamp()

  override def getTimeout: Int = 0

}
