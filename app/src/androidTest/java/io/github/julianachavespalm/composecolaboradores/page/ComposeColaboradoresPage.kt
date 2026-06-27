package io.github.julianachavespalm.composecolaboradores.page

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
class ComposeColaboradoresPage(private val composeTestRule: ComposeContentTestRule) {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val labelNome by lazy { context.getString(R.string.label_nome) }
    private val labelEmail by lazy { context.getString(R.string.label_email) }
    private val acaoSalvar by lazy { context.getString(R.string.acao_salvar) }
    private val acaoAtualizar by lazy { context.getString(R.string.acao_atualizar) }
    private val acaoCancelar by lazy { context.getString(R.string.acao_cancelar) }
    private val acaoExcluir by lazy { context.getString(R.string.acao_excluir) }
    private val erroEspaco by lazy { context.getString(R.string.erro_email_espaco) }
    private val confirmacaoExcluirTitulo by lazy { context.getString(R.string.confirmacao_excluir_titulo) }
    
    private val labelEditar by lazy { context.getString(R.string.label_editar)}

    data class Usuario(val nome: String, val email: String, val nivel: Nivel)

    companion object {
        fun onGerenciadorScreen(
            rule: ComposeContentTestRule,
            block: ComposeColaboradoresPage.() -> Unit
        ) = ComposeColaboradoresPage(rule).apply(block)

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
    
    fun preencherFormulario(usuario: Usuario) = apply {
        node(labelNome).performTextReplacement(usuario.nome)
        node(labelEmail).performTextReplacement(usuario.email)
        node(labelEmail).performImeAction()
        
        composeTestRule.onNodeWithTag("menu_item_${usuario.nivel.name}").performClick()
    }

    fun clicarSalvar() = apply {
        try {
            node(acaoSalvar).performClick()
        } catch (e: Throwable) {
            node(acaoAtualizar).performClick()
        }
    }

    fun cadastrar(usuario: Usuario) = apply {
        preencherFormulario(usuario)
        clicarSalvar()
    }

    fun clicarEditarColaborador(nome: String) = apply {
        composeTestRule.onNodeWithTag("lista_colaboradores")
            .performScrollToNode(hasText(nome))

        composeTestRule.onNode(
            hasContentDescription(labelEditar) and hasAnyAncestor(hasText(nome))
        ).performClick()
    }

    fun clicarExcluirColaborador(nome: String) = apply {
        composeTestRule.onNodeWithTag("lista_colaboradores")
            .performScrollToNode(hasText(nome))

        composeTestRule.onNode(
            hasContentDescription(acaoExcluir) and hasAnyAncestor(hasText(nome))
        ).performClick()
    }

    fun confirmarExclusao() = apply {
        // Clica no botão de excluir do diálogo de confirmação
        node(acaoExcluir).performClick()
    }

    fun clicarCancelar() = apply {
        node(acaoCancelar).performClick()
    }

    fun cancelarExclusao() = apply {
        // Clica no botão de cancelar do diálogo de confirmação
        node(acaoCancelar).performClick()
    }

    fun limparCampos() = apply {
        node(labelNome).performTextInput("") // Limpa o texto existente
        node(labelEmail).performTextInput("")
    }

    fun verificar(bloco: Assercoes.() -> Unit) = Assercoes().apply(bloco)

    inner class Assercoes {
        fun colaboradorNaLista(nome: String) {
            composeTestRule.onNodeWithTag("lista_colaboradores")
                .performScrollToNode(hasText(nome))
            
            node(nome).assertIsDisplayed()
        }

        fun colaboradorNaoEstaNaLista(nome: String) {
            composeTestRule.onNodeWithText(nome).assertDoesNotExist()
        }

        fun erroEmailComEspaco() {
            node(erroEspaco).assertIsDisplayed()
        }

        fun botaoSalvarDesabilitado() {
            node(acaoSalvar).assertIsNotEnabled()
        }

        fun botoesAcaoVisiveis() {
            node(acaoSalvar).assertIsDisplayed()
            node(acaoCancelar).assertIsDisplayed()
        }

        fun formularioVazio() {
            composeTestRule.onNodeWithText(labelNome).assertIsDisplayed()
            composeTestRule.onNodeWithText(labelEmail).assertIsDisplayed()
            
            node(Massa.valido.nome).assertDoesNotExist()
            node(Massa.valido.email).assertDoesNotExist()
        }

        fun dialogoExclusaoSumiu() {
            node(confirmacaoExcluirTitulo).assertDoesNotExist()
        }
    }

    private fun node(text: String) = composeTestRule.onNodeWithText(text)
}
