import domain.JokeScenario
import domain.logic.Fact
import domain.logic.Label
import domain.logic.JokeLogic
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class DomainModelTest : FunSpec({

    test("сценарий: заяц демонстрирует логику волку (спички есть) -> цепочка фактов и NOT_GAY") {
        val result = JokeScenario.hareTeachesWolf()

        result.facts.shouldContainAll(
            Fact.HasMatches,
            Fact.Smokes,
            Fact.DrinkAlcohol,
            Fact.HasMoney,
            Fact.HasSex,
            Fact.Labeled(Label.NOT_GAY)
        )

        val ruleNames = result.trace.steps.map { it.ruleName }
        ruleNames shouldBe listOf(
            "matches_implies_smokes",
            "smokes_implies_drinks",
            "drinks_implies_money",
            "money_implies_sex",
            "sex_implies_not_gay",
        )
    }

    test("сценарий: волк пытается применить логику к медведю (спичек нет) -> GAY") {
        val result = JokeScenario.wolfTalksToBear()

        result.facts shouldBe setOf(Fact.Labeled(Label.GAY))
        result.trace.steps.map { it.ruleName } shouldBe listOf("no_matches_implies_gay")
    }

    test("движок вывода: если добавить HasMoney вручную, выводится HasSex и NOT_GAY") {
        val engine = JokeLogic.engine
        val result = engine.infer(setOf(Fact.HasMoney))

        result.facts.shouldContain(Fact.HasSex)
        result.facts.shouldContain(Fact.Labeled(Label.NOT_GAY))
    }

    test("диалоги как доменная модель: не пустые и содержат ожидаемых персонажей") {
        val d1 = JokeScenario.hareTeachesWolfDialogue()
        d1.turns.first().speaker.name shouldBe "Волк"

        val d2 = JokeScenario.wolfTalksToBearDialogue()
        d2.turns.first().speaker.name shouldBe "Медведь"
    }
})

