package model.player

import isel.tds.damas.model.player.Player
import isel.tds.damas.model.player.toPlayer
import isel.tds.damas.model.player.toPlayerOrNull
import kotlin.test.*

class TestPlayer {

    // verificação da função other
    @Test
    fun `Test Other Player`() {
        assertEquals(Player.B, Player.W.other, "Se o jogador atual é w, o outro jogador deve ser b")
        assertEquals(Player.W, Player.B.other, "Se o jogador atual é b, o outro jogador deve ser w")
    }

   // verficação da função toPlayer()
    @Test
    fun `Test String toPlayer with Valid Input`() {
        assertEquals(Player.W, "W".toPlayer(), "A string 'w' deve ser convertida para Player.W")
        assertEquals(Player.B, "B".toPlayer(), "A string 'b' deve ser convertida para Player.B")
        assertEquals(Player.W, "W".toPlayer(), "A string 'W' deve ser convertida para Player.W")
        assertEquals(Player.B, "B".toPlayer(), "A string 'B' deve ser convertida para Player.B")
    }

    // verificação da função toPlayerOrNull()
    @Test
    fun `Test String toPlayerOrNull with Valid Input`() {
        assertEquals(Player.W, "W".toPlayerOrNull(), "A string 'W' deve ser convertida para Player.W")
        assertEquals(Player.B, "B".toPlayerOrNull(), "A string 'B' deve ser convertida para Player.B")
        assertEquals(Player.W, "W".toPlayerOrNull(), "A string 'W' deve ser convertida para Player.W")
        assertEquals(Player.B, "B".toPlayerOrNull(), "A string 'B' deve ser convertida para Player.B")
    }

    // Verificação de uma string inválida, logo não é jogador
    @Test
    fun `Test String toPlayerOrNull with Invalid Input`() {
        assertNull("x".toPlayerOrNull(), "A string inválida deve retornar null com toPlayerOrNull")
    }
}