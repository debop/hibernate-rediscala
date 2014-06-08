package org.hibernate.cache.rediscala.regions

import java.util.Properties
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.strategy.RedisAccessStrategyFactory
import org.hibernate.cache.rediscala.utils.Promises
import org.hibernate.cache.spi.GeneralDataRegion


class RedisGeneralDataRegion(private[this] val _accessStrategyFactory: RedisAccessStrategyFactory,
                             private[this] val _cache: HibernateRedisCache,
                             private[this] val _regionName: String,
                             private[this] val _props: Properties)
  extends RedisDataRegion(_accessStrategyFactory,
                           _cache,
                           _regionName,
                           _props)
  with GeneralDataRegion {


  override def get(key: Any): AnyRef = {
    try {
      Promises.await(cache.get(regionName, key.toString, expireInSeconds)).asInstanceOf[AnyRef]
    } catch {
      case e: Throwable => null
    }
  }

  override def put(key: Any, value: Any) {
    Promises.await(cache.set(regionName, key.toString, value, expireInSeconds))
  }

  override def evict(key: Any) {
    Promises.await(cache.delete(regionName, key.toString))
  }

  override def evictAll() {
    Promises.await(cache.deleteRegion(regionName))
  }
}
