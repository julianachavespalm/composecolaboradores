package io.github.julianachavespalm.composecolaboradores.page

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.platform.app.InstrumentationRegistry
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.ui.TestTags

class ComposeColaboradoresPage(private val composeTestRule: ComposeContentTestRule) {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun res(id: Int) = context.getString(id)

    private val fieldNome = hasTestTag(TestTags.CAMPO_NOME)
    private val fieldEmail = hasTestTag(TestTags.CAMPO_EMAIL)
    private val btnCancelar = hasTestTag(TestTags.BOTAO_CANCELAR)
    private val btnSalvar = hasTestTag(TestTags.BOTAO_SALVAR)
    private val btnExcluirForm = hasText(res(R.string.acao_excluir))
    private val tagListaColaboradores = TestTags.LISTA_COLABORADORES

    private val tagCardColaborador = TestTags.CARD_COLABORADOR

    private fun node(matcher: SemanticsMatcher) = composeTestRule.onNode(matcher)
    private fun node(resId: Int) = composeTestRule.onNodeWithText(res(resId))
    private fun scrollPara(matcher: SemanticsMatcher) =
        composeTestRule.onNodeWithTag(tagListaColaboradores).performScrollToNode(matcher)
    private fun scrollPara(resId: Int) = scrollPara(hasText(res(resId)))

    data class Usuario(val nome: String, val email: String, val nivel: Nivel)

    fun preencherFormulario(u: Usuario) = apply {
        node(fieldNome).performTextReplacement(u.nome)
        node(fieldEmail).performTextReplacement(u.email)
        node(fieldEmail).performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("menu_item_${u.nivel.name}").performClick()
        composeTestRule.waitForIdle()
    }

    fun clicarSalvar() = apply {
        node(btnSalvar).performClick()
        composeTestRule.waitForIdle()
    }

    fun clicarAbrirCadastro() = apply {
        scrollPara(hasText(res(R.string.label_cadastrar)) or hasText(res(R.string.label_editar)))
    }

    fun cadastrar(u: Usuario) = clicarAbrirCadastro().preencherFormulario(u).clicarSalvar()

    fun clicarEditarColaborador(u: Usuario) = apply {
        val cardMatcher = matcherCardColaborador(u)
        scrollPara(cardMatcher)
        node(hasContentDescription(res(R.string.label_editar)) and hasAnyAncestor(cardMatcher))
            .performClick()
    }

    fun clicarExcluirColaborador(u: Usuario) = apply {
        val cardMatcher = matcherCardColaborador(u)
        scrollPara(cardMatcher)
        node(hasContentDescription(res(R.string.acao_excluir)) and hasAnyAncestor(cardMatcher))
            .performClick()
    }

    fun confirmarExclusao() = apply { 
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodes(isDialog()).fetchSemanticsNodes().isNotEmpty()
        }
        node(btnExcluirForm and hasAnyAncestor(isDialog())).performClick() 
    }
    fun clicarCancelar() = apply { 
        scrollPara(btnCancelar and !hasAnyAncestor(isDialog()))
        node(btnCancelar and !hasAnyAncestor(isDialog())).performClick()
        composeTestRule.waitForIdle()
    }
    fun cancelarExclusao() = apply { 
        node(btnCancelar and hasAnyAncestor(isDialog())).performClick() 
        composeTestRule.waitForIdle()
    }

    fun verificar(block: Assercoes.() -> Unit) = Assercoes().apply(block)

    private fun matcherCardColaborador(u: Usuario) = hasTestTag(tagCardColaborador) and
            hasAnyDescendant(hasText(u.nome)) and
            hasAnyDescendant(hasText(u.email)) and
            hasAnyDescendant(hasText(res(u.nivel.descricao)))


    inner class Assercoes {
        fun colaboradorNaLista(u: Usuario) {
            val matcher = matcherCardColaborador(u)
            scrollPara(matcher)
            node(matcher).assertIsDisplayed()
        }
        fun colaboradorApareceApenasUmaVez(u: Usuario) {
            val matcher = matcherCardColaborador(u)
            scrollPara(matcher)
            composeTestRule.onAllNodes(matcher and !hasAnyAncestor(isDialog()))
                .assertCountEquals(1)
        }
        fun colaboradorNaoEstaNaLista(u: Usuario) = node(matcherCardColaborador(u)).assertDoesNotExist()

        fun erroColaboradorJaCadastrado() = node(R.string.erro_colaborador_ja_cadastrado).assertIsDisplayed()
        fun erroEmailComEspaco() = node(R.string.erro_email_espaco).assertIsDisplayed()
        fun botaoSalvarDesabilitado() = node(btnSalvar).assertIsNotEnabled()
        fun botoesAcaoVisiveis() { 
            node(btnSalvar).assertIsDisplayed()
            node(btnCancelar and !hasAnyAncestor(isDialog())).assertIsDisplayed() 
        }
        fun formularioVazio() {
            node(fieldNome).assertTextContains(res(R.string.label_nome))
            node(fieldEmail).assertTextContains(res(R.string.label_email))
            node(hasTestTag(TestTags.CAMPO_NIVEL)).assertTextContains(res(R.string.label_nivel))
        }
        fun dialogoExclusaoSumiu() = node(R.string.confirmacao_excluir_titulo).assertDoesNotExist()
    }

    companion object {
        fun onGerenciadorScreen(rule: ComposeContentTestRule, block: ComposeColaboradoresPage.() -> Unit) = 
            ComposeColaboradoresPage(rule).apply(block)

        object Massa {
            val valida = Usuario("Colaborador", "colaborador@email.com", Nivel.GERENCIA)
            val validaParcialmenteDiferente = Usuario("Colaborador", "colaborador@email.com", Nivel.FINANCEIRO)
            val comEspaco = Usuario("Erro", "user @email.com", Nivel.FINANCEIRO)
            val invalida = Usuario("Invalido", "email-sem-formato", Nivel.SUPORTE)
            val lista = listOf(
                Usuario("Admin", "admin@email.com", Nivel.ADMINISTRATIVO),
                Usuario("Finance", "fin@email.com", Nivel.FINANCEIRO),
                Usuario("Dev", "dev@email.com", Nivel.SUPORTE),
                Usuario("Ger", "ger@email.com", Nivel.GERENCIA)
            )
        }
    }
}
