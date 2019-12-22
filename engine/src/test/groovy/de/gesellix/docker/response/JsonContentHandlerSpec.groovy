package de.gesellix.docker.response

import spock.lang.Specification

class JsonContentHandlerSpec extends Specification {

    def jsonContentHandler = new JsonContentHandler()

    def "should convert simple json"() {
        given:
        def inputStream = new ByteArrayInputStream("{'key':'a-value'}".bytes)

        when:
        def content = jsonContentHandler.getContent(inputStream)

        then:
        content == ["key": "a-value"]
    }

    def "should convert Integer values as integers"() {
        given:
        def inputStream = new ByteArrayInputStream("{'integer':42, 'double':1.000001}".bytes)

        when:
        def content = jsonContentHandler.getContent(inputStream)

        then:
        content["integer"] == 42
        Math.rint(content["integer"]) == content["integer"].intValue()
    }

    def "should convert Double values as doubles"() {
        given:
        def inputStream = new ByteArrayInputStream("{'integer':42, 'double':1.000001}".bytes)

        when:
        def content = jsonContentHandler.getContent(inputStream)

        then:
        content["double"] == 1.000001d
    }

    def "should convert json chunks from InputStream to an array of json chunks"() {
        given:
        def inputStream = new ByteArrayInputStream("{'key':'a-value'}\n{'2nd':'chunk', 'another':'value'}".bytes)

        when:
        def result = jsonContentHandler.getContent(inputStream)

        then:
        result == [["key": "a-value"], ["2nd": "chunk", "another": "value"]]
    }
}
