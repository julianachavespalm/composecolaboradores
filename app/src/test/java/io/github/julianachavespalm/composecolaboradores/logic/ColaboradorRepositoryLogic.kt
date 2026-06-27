package io.github.julianachavespalm.composecolaboradores.logic

import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import org.junit.Assert


class ColaboradorRepositoryLogic(private val repository: ColaboradorRepository) {


    fun salvar(colaborador: Colaborador) = apply {
        repository.salvar(colaborador)
    }

    fun salvarMassa(colaboradores: List<Colaborador>) = apply {
        colaboradores.forEach { repository.salvar(it) }
    }

    fun remover(id: Int) = apply {
        repository.remover(id)
    }

    fun cadastrar(
        nome: String = "Original",
        email: String = "original@email.com",
        nivel: Nivel = Nivel.SUPORTE,
        onCriado: (Colaborador) -> Unit = {}
    ) = apply {
        val novo = umColaborador(nome = nome, email = email, nivel = nivel)
        repository.salvar(novo)
        onCriado(repository.colaboradores.value.last())
    }

    fun obterEstadoAtual() = repository.colaboradores.value.toList()

    fun verificarTamanhoDaLista(esperado: Int) = apply {
        Assert.assertEquals("O tamanho da lista está incorreto", esperado, repository.colaboradores.value.size)
    }

    fun verificarColaboradorNaPosicao(index: Int, asserts: Colaborador.() -> Unit) = apply {
        val lista = repository.colaboradores.value
        Assert.assertTrue("Lista deveria ter pelo menos ${index + 1} itens", lista.size > index)
        lista[index].asserts()
    }

    fun verificarListaVazia() = verificarTamanhoDaLista(0)

    fun verificarEstadoDaListaNaoMudou(estadoAnterior: List<Colaborador>) = apply {
        Assert.assertEquals(
            "A lista foi alterada indevidamente",
            estadoAnterior,
            repository.colaboradores.value
        )
    }

    companion object {
        fun umColaborador(
            id: Int = 0,
            nome: String = "Colaborador Teste",
            email: String = "teste@email.com",
            nivel: Nivel = Nivel.SUPORTE
        ) = Colaborador(id, nome, email, nivel)

        object Massa {
            val valido = umColaborador(nome = "Valido", email = "valido@email.com", nivel = Nivel.GERENCIA)

            val invalidos = listOf(
                umColaborador(nome = "", email = "email@email.com", nivel = Nivel.SUPORTE),
                umColaborador(nome = "Teste", email = "", nivel = Nivel.SUPORTE),
                umColaborador(nome = "Teste", email = "teste@email.com", nivel = Nivel.NENHUM),
                umColaborador(nome = "Erro", email = "user @email.com", nivel = Nivel.FINANCEIRO),
                umColaborador(nome = "Invalido", email = "email-sem-formato", nivel = Nivel.SUPORTE),
                umColaborador(nome = "   ", email = "espacos@email.com", nivel = Nivel.SUPORTE)
            )

            val lista = listOf(
                umColaborador(nome = "Admin", email = "admin@email.com", nivel = Nivel.ADMINISTRATIVO),
                umColaborador(nome = "Finance", email = "fin@email.com", nivel = Nivel.FINANCEIRO),
                umColaborador(nome = "Dev", email = "dev@email.com", nivel = Nivel.SUPORTE)
            )

            val niveisValidos = Nivel.entries.filter { it != Nivel.NENHUM }

            val emailsValidos = listOf(
                "teste@email.com",
                "user.name@domain.co.uk",
                "123@xyz.com",
                "user+filter@gmail.com"
            )

            val emailsInvalidos = listOf(
                "invalido",
                "usuario@",
                "@dominio.com",
                "usuario @dominio.com",
                "usuario@dominio",
                ""
            )

            val repetido = umColaborador(nome = "Repetido", email = "repetido@email.com")
        }
    }
}
