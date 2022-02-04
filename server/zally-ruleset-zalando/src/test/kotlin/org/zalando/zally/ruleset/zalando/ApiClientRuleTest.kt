package org.zalando.zally.ruleset.zalando

import org.zalando.zally.core.rulesConfig
import org.zalando.zally.test.ZallyAssertions.assertThat
import org.zalando.zally.core.DefaultContextFactory
import org.zalando.zally.rule.api.Context
import org.junit.jupiter.api.Test

class ApiClientRuleTest {

    private val rule = ApiClientRule(rulesConfig)

    @Test
    fun correctApiClientIsSet() {
        val context = withClient("bbva")

        val violation = rule.validate(context)

        assertThat(violation)
            .isNull()
    }

    @Test
    fun incorrectClientIsSet() {
        val context = withClient("santander")
        val violation = rule.validate(context)

        assertThat(violation)
            .pointerEqualTo("/info/x-client")
            .descriptionMatches(".*doesn't match.*")
    }

    @Test
    fun noApiClientIsSet() {
        val context = withClient("null")
        val violation = rule.validate(context)

        assertThat(violation)
            .pointerEqualTo("/info/x-client")
            .descriptionMatches(".*Client must be provided.*")
    }

    private fun withClient(apiClient: String): Context {
        val content = """
            openapi: '3.0.0'
            info:
              x-client: $apiClient
              title: Lorem Ipsum
              version: 1.0.0
            paths: {}
            """.trimIndent()

        return DefaultContextFactory().getOpenApiContext(content)
    }
}
