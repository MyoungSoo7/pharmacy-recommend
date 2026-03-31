package com.lms.pharmacyrecommend.direction.service

import spock.lang.Specification

class Base62ServiceTest extends Specification {

    Base62Service base62Service = new Base62Service()

    def "ID를 인코딩하고 디코딩하면 원래 값"() {
        given:
        Long original = 12345L

        when:
        String encoded = base62Service.encodeDirectionId(original)
        Long decoded = base62Service.decodeDirectionId(encoded)

        then:
        decoded == original
    }

    def "다른 ID는 다른 인코딩"() {
        when:
        String enc1 = base62Service.encodeDirectionId(1L)
        String enc2 = base62Service.encodeDirectionId(2L)

        then:
        enc1 != enc2
    }

    def "인코딩 결과는 URL-safe 문자열"() {
        when:
        String encoded = base62Service.encodeDirectionId(999999L)

        then:
        encoded ==~ /[A-Za-z0-9]+/
    }

    def "큰 숫자도 인코딩 가능"() {
        when:
        String encoded = base62Service.encodeDirectionId(Long.MAX_VALUE)

        then:
        encoded != null
        !encoded.isEmpty()
    }
}
