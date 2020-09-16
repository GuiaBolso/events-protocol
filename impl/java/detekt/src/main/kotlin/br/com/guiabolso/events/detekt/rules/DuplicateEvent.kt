package br.com.guiabolso.events.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames

/**
 * Events rule
 * @active
 */
class DuplicateEvent : Rule() {

    private val events = mutableListOf<Pair<String, Int>>()

    override val issue = Issue(
        "DuplicateEvent",
        Severity.Defect,
        "This rule reports two events with the same name+version defined.",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if ("EventHandler" !in klass.getSuperNames()) return
        val eventProp = klass.findPropertyByName("eventName")!!
        val versionProp = klass.findPropertyByName("eventVersion")!!

        val event = eventProp.getChildOfType<KtStringTemplateExpression>()!!.entries.joinToString("") { it.text }
        val version = versionProp.getChildOfType<KtConstantExpression>()!!.text.toInt()

        if (event to version in events) {
            report(CodeSmell(issue, Entity.from(eventProp), "The class ${klass.name} is trying to redeclare event $event"))
        } else {
            events += event to version
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        Regex("event\\(.*\".+\".*,.*\\d+.*\\)") // event(XXXX "abc" XXXX, XXXX 4 XXXXX)
            .findAll(function.text).toList().flatMap { it.groups }.filterNotNull().forEach {
                val event = it.value.substringAfter("\"").substringBeforeLast("\"")
                val version = it.value.substringAfterLast("\"").filter { it.isDigit() }.toInt()

                if (event to version in events) {
                    report(CodeSmell(issue, Entity.from(function), "The function ${function.name} is trying to redeclare event $event"))
                } else {
                    events += event to version
                }
            }
    }
}
