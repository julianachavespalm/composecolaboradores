package io.github.julianachavespalm.composecolaboradores

import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários do [ColaboradorRepository].
 *
 * Valida operações de:
 * - Cadastro
 * - Atualização
 * - Remoção
 * - Tratamento de registros inexistentes
 */
class ColaboradorRepositoryTest {

    private lateinit var repository: ColaboradorRepository

    @Before
    fun setUp() {
        repository = ColaboradorRepository()
    }

    @Test
    fun `deve cadastrar colaborador atribuindo id automaticamente`() {
        val colaborador = criarColaborador(
            nome = "Ralf",
            email = "ralf@email.com",
            nivel = Nivel.SUPORTE
        )

        repository.salvar(colaborador)

        val resultado = repository.colaboradores.value

        assertEquals(1, resultado.size)
        assertEquals(1, resultado.first().id)
        assertEquals("Ralf", resultado.first().nome)
        assertEquals("ralf@email.com", resultado.first().email)
        assertEquals(Nivel.SUPORTE, resultado.first().nivel)
    }

    @Test
    fun `deve atualizar colaborador existente quando id informado`() {
        val colaboradorSalvo = cadastrarColaborador()

        val colaboradorAtualizado = colaboradorSalvo.copy(
            nome = "Editado",
            email = "editado@email.com",
            nivel = Nivel.GERENCIA
        )

        repository.salvar(colaboradorAtualizado)

        val resultado = repository.colaboradores.value.first()

        assertEquals("Editado", resultado.nome)
        assertEquals("editado@email.com", resultado.email)
        assertEquals(Nivel.GERENCIA, resultado.nivel)
        assertEquals(colaboradorSalvo.id, resultado.id)
    }

    @Test
    fun `deve remover colaborador existente`() {
        val colaboradorSalvo = cadastrarColaborador()

        repository.remover(colaboradorSalvo.id)

        assertTrue(repository.colaboradores.value.isEmpty())
    }

    @Test
    fun `nao deve alterar lista ao atualizar colaborador inexistente`() {
        cadastrarColaborador()

        val estadoInicial = repository.colaboradores.value.toList()

        repository.salvar(
            Colaborador(
                id = 99,
                nome = "Editado",
                email = "editado@email.com",
                nivel = Nivel.GERENCIA
            )
        )

        assertEquals(estadoInicial, repository.colaboradores.value)
    }

    @Test
    fun `nao deve alterar lista ao remover colaborador inexistente`() {
        cadastrarColaborador()

        val estadoInicial = repository.colaboradores.value.toList()

        repository.remover(99)

        assertEquals(estadoInicial, repository.colaboradores.value)
    }

    @Test
    fun `nao deve salvar colaborador sem nome`() {
        val colaborador = criarColaborador("", "email@email.com", Nivel.ADMINISTRATIVO)
        repository.salvar(colaborador)
        val resultado = repository.colaboradores.value
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `nao deve salvar colaborador com nivel NENHUM`() {
        val colaborador = criarColaborador("Ralf", "ralf@email.com", Nivel.NENHUM)
        repository.salvar(colaborador)
        val resultado = repository.colaboradores.value
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `nao deve salvar colaborador sem email`() {
        val colaborador = criarColaborador("Ralf", "", Nivel.ADMINISTRATIVO)
        repository.salvar(colaborador)
        val resultado = repository.colaboradores.value
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `nao deve salvar cadastro com todos dados em branco`() {
        val colaborador = criarColaborador("", "", Nivel.NENHUM)
        repository.salvar(colaborador)
        val resultado = repository.colaboradores.value
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `nao deve salvar colaborador com email contendo espaco`() {
        val colaborador = criarColaborador("Ralf", "ralf @email.com", Nivel.ADMINISTRATIVO)
        repository.salvar(colaborador)
        val resultado = repository.colaboradores.value
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `nao deve salvar colaborador com email invalido sem ponto`() {
        val colaborador = criarColaborador("Ralf", "ralf@email", Nivel.ADMINISTRATIVO)
        repository.salvar(colaborador)
        assertTrue(repository.colaboradores.value.isEmpty())
    }

    @Test
    fun `nao deve salvar colaborador com email invalido sem arroba`() {
        val colaborador = criarColaborador("Ralf", "ralf.email.com", Nivel.ADMINISTRATIVO)
        repository.salvar(colaborador)
        assertTrue(repository.colaboradores.value.isEmpty())
    }

    @Test
    fun `nao deve salvar colaborador com email invalido caracteres proibidos`() {
        val colaborador = criarColaborador("Ralf", "ralf!#$@email.com", Nivel.ADMINISTRATIVO)
        repository.salvar(colaborador)
        assertTrue(repository.colaboradores.value.isEmpty())
    }

    private fun cadastrarColaborador(): Colaborador {
        repository.salvar(
            criarColaborador(
                nome = "Original",
                email = "original@email.com",
                nivel = Nivel.ADMINISTRATIVO
            )
        )

        return repository.colaboradores.value.first()
    }

    private fun criarColaborador(
        nome: String,
        email: String,
        nivel: Nivel
    ) = Colaborador(
        id = 0,
        nome = nome,
        email = email,
        nivel = nivel
    )
}
