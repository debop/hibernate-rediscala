package org.hibernate.cache.rediscala.tests.domain

import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import scala.beans.BeanProperty


/**
 * Account
 * Created by debop on 2014. 3. 30.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
@SerialVersionUID(6662300674854084326L)
class Account extends Serializable {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  @ManyToOne
  @JoinColumn(name = "personId")
  @BeanProperty
  var person: Person = _
}

