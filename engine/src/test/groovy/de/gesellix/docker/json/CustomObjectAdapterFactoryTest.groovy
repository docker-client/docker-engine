package de.gesellix.docker.json

import com.squareup.moshi.Moshi
import spock.lang.Specification
import spock.lang.Unroll

class CustomObjectAdapterFactoryTest extends Specification {

  Moshi moshi

  def setup() {
    moshi = new Moshi.Builder().add(new CustomObjectAdapterFactory()).build()
  }

  def "should deserialize numeric"() {
    when:
    def result = moshi.adapter(Map).fromJson('{"number":1.2}')
    then:
    result.number == 1.2
  }

  @Unroll
  def "should serialize BigDecimal with value #number"() {
    when:
    def result = moshi.adapter(Map).toJson([number: new BigDecimal("$number")])
    then:
    result == "{\"number\":$expectedValue}"
    where:
    number   | expectedValue
    0        | "0"
    1.2      | "1.2"
    1        | "1"
    0.000001 | "0.000001"
  }
}
