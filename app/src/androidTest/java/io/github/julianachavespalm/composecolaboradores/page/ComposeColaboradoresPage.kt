package io.github.julianachavespalm.composecolaboradores.page

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.platform.app.InstrumentationRegistry
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel

class ComposeColaboradoresPage(private val composeTestRule: ComposeContentTestRule) {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun res(id: Int) = context.getString(id)


    private val fieldNome = hasText(res(R.string.label_nome))
    private val fieldEmail = hasText(res(R.string.label_email))
    private val btnSalvar = hasText(res(R.string.acao_salvar))
    private val btnAtualizar = hasText(res(R.string.acao_atualizar))
    private val btnCancelar = hasText(res(R.string.acao_cancelar))
    private val btnExcluir = hasText(res(R.string.acao_excluir))
    private val tagListaColaboradores = "lista_colaboradores"

    private fun node(matcher: SemanticsMatcher) = composeTestRule.onNode(matcher)
    private fun node(text: String) = composeTestRule.onNodeWithText(text)
    private fun scrollPara(text: String) = composeTestRule.onNodeWithTag(tagListaColaboradores).performScrollToNode(hasText(text))

    data class Usuario(val nome: String, val email: String, val nivel: Nivel)

    fun preencherFormulario(u: Usuario) = apply {
        node(fieldNome).performTextReplacement(u.nome)
        node(fieldEmail).performTextReplacement(u.email)
        node(fieldEmail).performImeAction()
        composeTestRule.onNodeWithTag("menu_item_${u.nivel.name}").performClick()
    }

    fun clicarSalvar() = apply {
        try { node(btnSalvar).performClick() } 
        catch (e: Throwable) { node(btnAtualizar).performClick() }
    }

    fun clicarAbrirCadastro() = apply {
        composeTestRule.onNodeWithTag(tagListaColaboradores).performScrollToNode(
            hasText(res(R.string.label_cadastrar)) or hasText(res(R.string.label_editar))
        )
    }

    fun cadastrar(u: Usuario) = clicarAbrirCadastro().preencherFormulario(u).clicarSalvar()

    fun clicarEditarColaborador(nome: String) = apply {
        scrollPara(nome)
        node(hasContentDescription(res(R.string.label_editar)) and hasAnyAncestor(hasText(nome))).performClick()
    }

    fun clicarExcluirColaborador(nome: String) = apply {
        scrollPara(nome)
        node(hasContentDescription(res(R.string.acao_excluir)) and hasAnyAncestor(hasText(nome))).performClick()
    }

    fun confirmarExclusao() = apply { node(btnExcluir and hasAnyAncestor(isDialog())).performClick() }
    fun clicarCancelar() = apply { node(btnCancelar and !hasAnyAncestor(isDialog())).performClick() }
    fun cancelarExclusao() = apply { node(btnCancelar and hasAnyAncestor(isDialog())).performClick() }

    fun verificar(block: Assercoes.() -> Unit) = Assercoes().apply(block)

    inner class Assercoes {
        fun colaboradorNaLista(n: String) { scrollPara(n); node(n).assertIsDisplayed() }
        fun colaboradorNaoEstaNaLista(n: String) = node(n).assertDoesNotExist()
        fun erroEmailComEspaco() = node(res(R.string.erro_email_espaco)).assertIsDisplayed()
        fun botaoSalvarDesabilitado() = node(btnSalvar).assertIsNotEnabled()
        fun botoesAcaoVisiveis() { node(btnSalvar).assertIsDisplayed(); node(btnCancelar and !hasAnyAncestor(isDialog())).assertIsDisplayed() }
        fun formularioVazio() {
            node(res(R.string.label_nome)).assertIsDisplayed()
            node(res(R.string.label_email)).assertIsDisplayed()
            node(Massa.valido.nome).assertDoesNotExist()
        }
        fun dialogoExclusaoSumiu() = node(res(R.string.confirmacao_excluir_titulo)).assertDoesNotExist()
    }

    companion object {
        fun onGerenciadorScreen(rule: ComposeContentTestRule, block: ComposeColaboradoresPage.() -> Unit) = 
            ComposeColaboradoresPage(rule).apply(block)

        object Massa {
            val valido = Usuario("Valido", "valido@email.com", Nivel.GERENCIA)
            val comEspaco = Usuario("Erro", "user @email.com", Nivel.FINANCEIRO)
            val invalido = Usuario("Invalido", "email-sem-formato", Nivel.SUPORTE)
            val lista = listOf(
                Usuario("Admin", "admin@email.com", Nivel.ADMINISTRATIVO),
                Usuario("Finance", "fin@email.com", Nivel.FINANCEIRO),
                Usuario("Dev", "dev@email.com", Nivel.SUPORTE),
                Usuario("Ger", "ger@email.com", Nivel.GERENCIA)
            )
        }
    }
}
