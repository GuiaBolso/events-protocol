package br.com.guiabolso.events.detekt

import br.com.guiabolso.events.detekt.rules.DuplicateEvent
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class EventProtocolProvider : RuleSetProvider {

    override val ruleSetId: String = "events-protocol"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            DuplicateEvent()
        )
    )
}
