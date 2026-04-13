package domain

import domain.dialog.Dialogue
import domain.dialog.DialogueTurn
import domain.logic.InferenceResult
import domain.logic.JokeLogic
import domain.logic.JokeLogic.demonstrate

object JokeScenario {

    val hare = Character("Заяц")
    val wolf = Character("Волк")
    val bear = Character("Медведь")

    fun hareTeachesWolfDialogue(): Dialogue = Dialogue(
        turns = listOf(
            DialogueTurn(wolf, "Что за книгу читаем?"),
            DialogueTurn(hare, "Логику"),
            DialogueTurn(wolf, "А что это значит?"),
            DialogueTurn(hare, "Покажу на примере. Спички у тебя есть?"),
            DialogueTurn(wolf, "Есть"),
            DialogueTurn(hare, "Значит, ты куришь..."),
            DialogueTurn(hare, "...и т.д. вывод: ты НЕ ГЕЙ")
        )
    )

    fun wolfTalksToBearDialogue(): Dialogue = Dialogue(
        turns = listOf(
            DialogueTurn(bear, "Что читаем?"),
            DialogueTurn(wolf, "Логику"),
            DialogueTurn(bear, "А что это значит?"),
            DialogueTurn(wolf, "Покажу на примере. Спички у тебя есть?"),
            DialogueTurn(bear, "Нет"),
            DialogueTurn(wolf, "Хм... тогда ТЫ ГЕЙ")
        )
    )

    fun hareTeachesWolf(): InferenceResult = demonstrate(hasMatches = true)

    fun wolfTalksToBear(): InferenceResult = demonstrate(hasMatches = false)
}

