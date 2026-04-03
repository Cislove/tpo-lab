package domain.logic

data class InferenceStep(
    val ruleName: String,
    val produced: Set<Fact>
)

data class InferenceTrace(
    val steps: List<InferenceStep>
)

data class InferenceResult(
    val facts: Set<Fact>,
    val trace: InferenceTrace
)

data class Rule(
    val name: String,
    val premises: Set<Fact>,
    val conclusions: Set<Fact>
)

class InferenceEngine(private val rules: List<Rule>) {

    fun infer(initialFacts: Set<Fact>): InferenceResult {
        var facts = initialFacts.toMutableSet()
        val steps = mutableListOf<InferenceStep>()

        var progressed: Boolean
        do {
            progressed = false

            for (rule in rules) {
                val applicable = facts.containsAll(rule.premises) && !facts.containsAll(rule.conclusions)
                if (!applicable) continue

                facts.addAll(rule.conclusions)
                steps += InferenceStep(ruleName = rule.name, produced = rule.conclusions)
                progressed = true
            }
        } while (progressed)

        return InferenceResult(facts = facts.toSet(), trace = InferenceTrace(steps))
    }
}

