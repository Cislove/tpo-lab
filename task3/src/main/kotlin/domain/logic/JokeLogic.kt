package domain.logic

object JokeLogic {

    val rules: List<Rule> = listOf(
        Rule(
            name = "matches_implies_smokes",
            premises = setOf(Fact.HasMatches),
            conclusions = setOf(Fact.Smokes)
        ),
        Rule(
            name = "smokes_implies_drinks",
            premises = setOf(Fact.Smokes),
            conclusions = setOf(Fact.DrinkAlcohol)
        ),
        Rule(
            name = "drinks_implies_money",
            premises = setOf(Fact.DrinkAlcohol),
            conclusions = setOf(Fact.HasMoney)
        ),
        Rule(
            name = "money_implies_sex",
            premises = setOf(Fact.HasMoney),
            conclusions = setOf(Fact.HasSex)
        ),
        Rule(
            name = "sex_implies_not_gay",
            premises = setOf(Fact.HasSex),
            conclusions = setOf(Fact.Labeled(Label.NOT_GAY))
        ),
        Rule(
            name = "no_matches_implies_gay",
            premises = setOf(Fact.Labeled(Label.GAY)),
            conclusions = emptySet()
        )
    )

    val engine: InferenceEngine = InferenceEngine(
        rules = rules.filterNot { it.name == "no_matches_implies_gay" }
    )

    fun demonstrate(hasMatches: Boolean): InferenceResult {
        return if (!hasMatches) {
            InferenceResult(
                facts = setOf(Fact.Labeled(Label.GAY)),
                trace = InferenceTrace(
                    steps = listOf(
                        InferenceStep(
                            ruleName = "no_matches_implies_gay",
                            produced = setOf(Fact.Labeled(Label.GAY))
                        )
                    )
                )
            )
        } else {
            engine.infer(setOf(Fact.HasMatches))
        }
    }
}

