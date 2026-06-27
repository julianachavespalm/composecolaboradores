package io.github.julianachavespalm.composecolaboradores

import androidx.compose.ui.test.junit4.createComposeRule
import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
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
            cadastrar(Massa.valido)
            
            verificar { colaboradorNaLista(Massa.valido.nome) }
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
            preencherFormulario(Massa.invalido)
            
            verificar { botaoSalvarDesabilitado() }
        }
    }

    @Test
    fun preenchimentoParcialDeveExibirBotoesDeAcao() {
        onGerenciadorScreen(composeTestRule) {
            clicarAbrirCadastro()
            preencherFormulario(Massa.valido)
            
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
        val usuarioOriginal = Massa.valido
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
        val usuarioParaExcluir = Massa.valido

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
            preencherFormulario(Massa.valido)
            clicarCancelar()
            
            clicarAbrirCadastro()
            verificar { 
                formularioVazio() 
            }
        }
    }

    @Test
    fun deveManterColaboradorAoCancelarExclusao() {
        val usuario = Massa.valido

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
