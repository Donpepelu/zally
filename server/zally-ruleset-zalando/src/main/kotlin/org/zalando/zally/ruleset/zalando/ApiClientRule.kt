package org.zalando.zally.ruleset.zalando

import com.typesafe.config.Config
import org.zalando.zally.core.toJsonPointer
import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

@Rule(
    ruleSet = ZalandoRuleSet::class,
    id = "299",
    severity = Severity.MUST,
    title = "Provide API Client"
)
class ApiClientRule(rulesConfig: Config) {
    private val validClients = rulesConfig.getStringList("${javaClass.simpleName}.clients").toSet()

    private val noApiClientDesc = "API Client must be provided"
    private val invalidApiClientDesc = "API Client doesn't match $validClients"
    private val extensionName = "x-client"
    private val extensionPointer = "/info/$extensionName".toJsonPointer()

    @Check(severity = Severity.MUST)
    fun validate(context: Context): Violation? {
        val client = context.api.info?.extensions?.get(extensionName)

        return when (client) {
            null, !is String -> context.violation(noApiClientDesc, extensionPointer)
            !in validClients -> context.violation(invalidApiClientDesc, extensionPointer)
            else -> null
        }
    }
}
