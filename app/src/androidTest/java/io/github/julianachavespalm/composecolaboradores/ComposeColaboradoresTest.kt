package io.github.julianachavespalm.composecolaboradores

import androidx.compose.ui.test.junit4.createComposeRule
import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.domain.usecase.GetColaboradoresUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.RemoverColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.SalvarColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.ValidarEmailUseCase
import io.github.julianachavespalm.composecolaboradores.page.ComposeColaboradoresPage.Companion.onGerenciadorScreen
import io.github.julianachavespalm.composecolaboradores.page.ComposeColaboradoresPage.Companion.Massa
import io.github.julianachavespalm.composecolaboradores.ui.ColaboradorViewModel
import io.github.julianachavespalm.composecolaboradores.ui.GerenciadorColaboradoresScreen
import io.github.julianachavespalm.composecolaboradores.ui.theme.ComposeColaboradoresTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComposeColaboradoresTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = ColaboradorRepository()
    private val viewModel = ColaboradorViewModel(
        GetColaboradoresUseCase(repository),
        SalvarColaboradorUseCase(repository),
        RemoverColaboradorUseCase(repository),
        ValidarEmailUseCase(repository)
    )

    @Before
    fun setup() {
        composeTestRule.setContent {
            ComposeColaboradoresTheme {
                GerenciadorColaboradoresScreen(viewModel)
            }
        }
    }
    
    @Test
    fun deveSalvarCadastroValidoNaLista() {
        onGerenciadorScreen(composeTestRule) {
            cadastrar(Massa.valida)
            
            verificar { colaboradorNaLista(Massa.valida.nome) }
        }
    }
    
    @Test
    fun naoDevePermitirCadastroComDadosRepetidos() {
        onGerenciadorScreen(composeTestRule) {
            cadastrar(Massa.valida)
            
            clicarAbrirCadastro()
            preencherFormulario(Massa.valida)

            verificar {
                erroColaboradorJaCadastrado()
                botaoSalvarDesabilitado()
                colaboradorApareceApenasUmaVez(Massa.valida.nome)
            }
        }
    }
    
    @Test
    fun devePermitirCadastroComDadosParcialmenteRepetidos() {
        onGerenciadorScreen(composeTestRule) {
            cadastrar(Massa.valida)
            cadastrar(Massa.validaParcialmenteDiferente)

            verificar {
                colaboradorApareceApenasUmaVez(Massa.valida.nome)
                colaboradorApareceApenasUmaVez(Massa.validaParcialmenteDiferente.nome)
            }
        }
    }
    
    @Test
    fun deveExibirErroAoDigitarEmailComEspaco() {
        onGerenciadorScreen(composeTestRule) {
            clicarAbrirCadastro()
            preencherFormulario(Massa.comEspaco)
            
            verificar { erroEmailComEspaco() }
        }
    }

    @Test
    fun botaoSalvarDeveFicarDesabilitadoParaEmailInvalido() {
        onGerenciadorScreen(composeTestRule) {
            clicarAbrirCadastro()
            preencherFormulario(Massa.invalida)
            
            verificar { botaoSalvarDesabilitado() }
        }
    }

    @Test
    fun preenchimentoParcialDeveExibirBotoesDeAcao() {
        onGerenciadorScreen(composeTestRule) {
            clicarAbrirCadastro()
            preencherFormulario(Massa.valida)
            
            verificar { botoesAcaoVisiveis() }
        }
    }
    
    
    @Test
    fun devePermitirVariosCadastrosSequenciais() {
        onGerenciadorScreen(composeTestRule) {
            Massa.lista.forEach { usuario ->
                cadastrar(usuario)
            }

            verificar {
                Massa.lista.forEach { colaboradorNaLista(it.nome) }
            }
        }
    }
    
    @Test 
    fun devePermitirEditarCadastro() {
        val usuarioOriginal = Massa.valida
        val nomeEditado = "Usuario Atualizado"

        onGerenciadorScreen(composeTestRule) {
            cadastrar(usuarioOriginal)
            
            clicarEditarColaborador(usuarioOriginal.nome)
            preencherFormulario(usuarioOriginal.copy(nome = nomeEditado))
            clicarSalvar()

            verificar { 
                colaboradorNaLista(nomeEditado) 
            }
        }
    }

    @Test
    fun devePermitirExcluirColaborador() {
        val usuarioParaExcluir = Massa.valida

        onGerenciadorScreen(composeTestRule) {
            cadastrar(usuarioParaExcluir)
            
            clicarExcluirColaborador(usuarioParaExcluir.nome)
            confirmarExclusao()

            verificar { 
                colaboradorNaoEstaNaLista(usuarioParaExcluir.nome)
            }
        }
    }

    @Test
    fun deveLimparFormularioAoCancelarCadastro() {
        onGerenciadorScreen(composeTestRule) {
            clicarAbrirCadastro()
            preencherFormulario(Massa.valida)
            clicarCancelar()
            
            clicarAbrirCadastro()
            verificar { 
                formularioVazio() 
            }
        }
    }

    @Test
    fun deveManterColaboradorAoCancelarExclusao() {
        val usuario = Massa.valida

        onGerenciadorScreen(composeTestRule) {
            cadastrar(usuario)
            
            clicarExcluirColaborador(usuario.nome)
            cancelarExclusao()

            verificar { 
                dialogoExclusaoSumiu()
                colaboradorNaLista(usuario.nome)
            }
        }
    }
}
