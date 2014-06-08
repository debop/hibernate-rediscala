package org.hibernate.cache.rediscala.tests

import java.util.Properties
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}

/**
 * PropertiesTest
 * Created by debop on 2014. 3. 30.
 */
class PropertiesTest extends FunSuite with Matchers with BeforeAndAfter {

  test("load properties by class") {
    val props = new Properties()
    val cachePath = "/hibernate-redis.properties"

    val inputStream = getClass.getResourceAsStream(cachePath)
    assert(inputStream != null)
    props.load(inputStream)
    inputStream.close()

    print("properties... " + props.toString)

  }

  test("load properties by classLoader") {
    val props = new Properties()
    val cachePath = "hibernate-redis.properties"

    val inputStream = getClass.getClassLoader.getResourceAsStream(cachePath)
    assert(inputStream != null)
    props.load(inputStream)
    inputStream.close()

    print("properties... " + props.toString)
  }

}
