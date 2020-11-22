package nl.juraji.intellij.formatter

import org.junit.Assert.assertNotEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JavaToStringToJsonFormatterTest {

    private val formatter = JavaToStringToJsonFormatter()

    @Test
    internal fun `should format Map#toString output`() {
        val input = "{key1={subKey1=value, subKey2=other value}, key2=Value &^%, no=1234}"
        val expected = """
            {
              "key1": {
                "subKey1": "value",
                "subKey2": "other value"
              },
              "key2": "Value &^%",
              "no": "1234"
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should format Collection#toString output`() {
        val input = "[{subKey1=value, subKey2=other value}, Value &^%, 1234]"
        val expected = """
            [
              {
                "subKey1": "value",
                "subKey2": "other value"
              },
              "Value &^%",
              "1234"
            ]
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should ignore assignments in List`() {
        val input = "[Value=&^%, 1234]"
        val expected = """
            [
              "Value=&^%",
              "1234"
            ]
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should ignore assignments in Map after assignment`() {
        val input = "{key1=Value=&^%, key2=1234}"
        val expected = """
            {
              "key1": "Value=&^%",
              "key2": "1234"
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should create object when starting with key`() {
        val input = "object={key1=Value=&^%, key2=1234}"
        val expected = """
            {
              "object": {
                "key1": "Value=&^%",
                "key2": "1234"
              }
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should support complex structures`() {
        val input =
            "object={key1={subKey1=value, subKey2=[[item], [item], [item]]}, key2=Value &^%, no=[1234, #43g34dfg=asd, %#^&#&, [dfggfds-fddfs, dsfgsjdsf ds gfsdf, dfgs]]}"
        val expected = """
            {
              "object": {
                "key1": {
                  "subKey1": "value",
                  "subKey2": [
                    [
                      "item"
                    ],
                    [
                      "item"
                    ],
                    [
                      "item"
                    ]
                  ]
                },
                "key2": "Value &^%",
                "no": [
                  "1234",
                  "#43g34dfg=asd",
                  "%#^&#&",
                  [
                    "dfggfds-fddfs",
                    "dsfgsjdsf ds gfsdf",
                    "dfgs"
                  ]
                ]
              }
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should support Kotlin data class toString`() {
        val input = "DataClass(map={key=value}, list=[item, other item], value=Some value)"
        val expected = """
            {
              "DataClass": {
                "map": {
                  "key": "value"
                },
                "list": [
                  "item",
                  "other item"
                ],
                "value": "Some value"
              }
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should support Kotlin data class toString in list`() {
        val input = "[DataClass(map={key=value}, value=Some value), DataClass(map={key=value}, value=Some value), DataClass(map={key=value}, value=Some value)]"
        val expected = """
            [
              "DataClass": {
                "map": {
                  "key": "value"
                },
                "value": "Some value"
              },
              "DataClass": {
                "map": {
                  "key": "value"
                },
                "value": "Some value"
              },
              "DataClass": {
                "map": {
                  "key": "value"
                },
                "value": "Some value"
              }
            ]
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should support Kotlin data class toString in map`() {
        val input = "{data=DataClass(map={key=value}, value=Some value)}"
        val expected = """
            {
              "data": "DataClass": {
                "map": {
                  "key": "value"
                },
                "value": "Some value"
              }
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `should support Kotlin data class toString in data class`() {
        val input = "DataClass(value=DataClass(map={key=value}, value=Some value))"
        val expected = """
            {
              "DataClass": {
                "value": "DataClass": {
                  "map": {
                    "key": "value"
                  },
                  "value": "Some value"
                }
              }
            }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertEquals(expected, result)
    }

    @Test
    internal fun `does not support structure characters in map values`() {
        val input = "{key=Value (with) structure [characters] = working}"
        val expected = """
           {
             "key": "Value (with) structure [characters] = working"
           }
        """.trimIndent()

        val result: String = formatter.format(input)

        assertNotEquals(expected, result)
    }
}
