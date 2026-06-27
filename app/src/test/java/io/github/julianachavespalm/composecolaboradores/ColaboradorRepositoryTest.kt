package io.github.julianachavespalm.composecolaboradores

import io.github.julianachavespalm.composecolaboradores.data.repository.InMemoryColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.logic.ColaboradorRepositoryLogic
import io.github.julianachavespalm.composecolaboradores.logic.ColaboradorRepositoryLogic.Companion.Massa
import io.github.julianachavespalm.composecolaboradores.logic.ColaboradorRepositoryLogic.Companion.umColaborador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColaboradorRepositoryTest {

    private val repository = InMemoryColaboradorRepository()
    private val logic = ColaboradorRepositoryLogic(repository)

    @Test
    fun `Deve iniciar com lista vazia`() {
        logic.verificarListaVazia()
    }

    @Test
    fun `Deve cadastrar colaborador com sucesso`() {
        logic.salvar(Massa.valido)
             .verificarColaboradorNaPosicao(0) {
                assertEquals(1, id)
                assertEquals(Massa.valido.nome, nome)
                assertEquals(Massa.valido.email, email)
                assertEquals(Massa.valido.nivel, nivel)
            }
    }

    @Test
    fun `Deve aceitar todos os níveis válidos de colaborador`() {
        Massa.niveisValidos.forEachIndexed { index, nivel ->
            logic.salvar(umColaborador(nome = "Nome $index", nivel = nivel))
                 .verificarColaboradorNaPosicao(index) {
                    assertEquals(nivel, this.nivel)
                }
        }
        logic.verificarTamanhoDaLista(Massa.niveisValidos.size)
    }

    @Test
    fun `Deve incrementar ID a cada novo cadastro e manter sequência após remoções`() {
        logic.salvar(umColaborador(nome = "Primeiro"))
             .salvar(umColaborador(nome = "Segundo"))
             .remover(1)
             .salvar(umColaborador(nome = "Terceiro"))
             .verificarTamanhoDaLista(2)
             .verificarColaboradorNaPosicao(0) { assertEquals(2, id) }
             .verificarColaboradorNaPosicao(1) { assertEquals(3, id) }
    }

    @Test
    fun `Deve atualizar dados de um colaborador existente mantendo sua posição na lista`() {
        logic.salvar(umColaborador(nome = "Item 1"))
             .salvar(umColaborador(nome = "Item 2"))
             .salvar(umColaborador(nome = "Item 3"))
        
        val editado = logic.obterEstadoAtual()[1].copy(nome = "Item 2 Editado", nivel = Nivel.FINANCEIRO)

        logic.salvar(editado)
             .verificarTamanhoDaLista(3)
             .verificarColaboradorNaPosicao(1) {
                assertEquals("Item 2 Editado", nome)
                assertEquals(Nivel.FINANCEIRO, nivel)
                assertEquals(2, id)
            }
             .verificarColaboradorNaPosicao(0) { assertEquals("Item 1", nome) }
             .verificarColaboradorNaPosicao(2) { assertEquals("Item 3", nome) }
    }

    @Test
    fun `Não deve cadastrar ou atualizar colaborador com dados inválidos`() {
        Massa.invalidos.forEach { colaborador ->
            logic.salvar(colaborador).verificarListaVazia()
        }

        logic.cadastrar(nome = "Valido")
        val salvo = logic.obterEstadoAtual().first()
        val invalido = salvo.copy(nome = "")
        
        logic.salvar(invalido)
             .verificarColaboradorNaPosicao(0) {
                assertEquals("Valido", nome)
            }
    }

    @Test
    fun `Nao deve alterar a lista ao tentar atualizar um id inexistente`() {
        logic.cadastrar()
        val estadoAntes = logic.obterEstadoAtual()

        logic.salvar(umColaborador(id = 99, nome = "Fantasma"))
             .verificarEstadoDaListaNaoMudou(estadoAntes)
    }

    @Test
    fun `Deve remover colaborador por id`() {
        logic.cadastrar { salvo ->
            logic.remover(salvo.id)
                 .verificarListaVazia()
        }
    }

    @Test
    fun `Nao deve alterar a lista ao remover id inexistente`() {
        logic.cadastrar()
        val estadoAntes = logic.obterEstadoAtual()

        logic.remover(99)
             .verificarEstadoDaListaNaoMudou(estadoAntes)
    }

    @Test
    fun `Nao deve permitir novo cadastro se todos os campos forem 100% iguais a um existente`() {
        logic.salvar(Massa.valido)
             .salvar(Massa.valido)
             .verificarTamanhoDaLista(1)
             .verificarColaboradorNaPosicao(0) { assertEquals(1, id) }
    }

    @Test
    fun `Deve permitir cadastros com dados parcialmente repetidos`() {
        val base = Massa.valido
        val emailDiferente = base.copy(email = "outro@email.com")
        val nomeDiferente = base.copy(nome = "Outro Nome")
        val nivelDiferente = base.copy(nivel = Nivel.SUPORTE)

        logic.salvar(base)
             .salvar(emailDiferente)
             .salvar(nomeDiferente)
             .salvar(nivelDiferente)
             .verificarTamanhoDaLista(4)
    }

    @Test
    fun `Deve validar e-mails`() {
        Massa.emailsValidos.forEach { email ->
            assertTrue("Deveria ser válido: $email", repository.isEmailValido(email))
        }
        
        Massa.emailsInvalidos.forEach { email ->
            assertFalse("Deveria ser inválido: $email", repository.isEmailValido(email))
        }
    }
    
    @Test
    fun `Deve manter ordem de cadastro na lista ao usar salvar novo cadastro`() {
        logic.salvarMassa(Massa.lista)
             .verificarTamanhoDaLista(Massa.lista.size)
             .verificarColaboradorNaPosicao(0) { assertEquals(Massa.lista[0].nome, nome) }
             .verificarColaboradorNaPosicao(1) { assertEquals(Massa.lista[1].nome, nome) }
             .verificarColaboradorNaPosicao(2) { assertEquals(Massa.lista[2].nome, nome) }

    }
}
