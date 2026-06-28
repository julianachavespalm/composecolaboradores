package io.github.julianachavespalm.composecolaboradores

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.github.julianachavespalm.composecolaboradores.page.ComposeColaboradoresPage.Companion.Massa
import io.github.julianachavespalm.composecolaboradores.page.ComposeColaboradoresPage.Companion.onGerenciadorScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ComposeColaboradoresTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun unlockDevice() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wakeUp()
        device.executeShellCommand("wm dismiss-keyguard")
    }

    @Test
    fun deveSalvarCadastroValidoNaLista() {
        onGerenciadorScreen(composeTestRule) {
            cadastrar(Massa.valida)
            
            verificar { colaboradorNaLista(Massa.valida) }
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
                colaboradorApareceApenasUmaVez(Massa.valida)
            }
        }
    }
    
    @Test
    fun devePermitirCadastroComDadosParcialmenteRepetidos() {
        onGerenciadorScreen(composeTestRule) {
            cadastrar(Massa.valida)
            cadastrar(Massa.validaParcialmenteDiferente)

            verificar {
                colaboradorApareceApenasUmaVez(Massa.valida)
                colaboradorApareceApenasUmaVez(Massa.validaParcialmenteDiferente)
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
                Massa.lista.forEach { colaboradorNaLista(it) }
            }
        }
    }
    
    @Test 
    fun devePermitirEditarCadastro() {
        val usuarioOriginal = Massa.valida
        val nomeEditado = "Usuario Atualizado"

        val usuarioEditado = usuarioOriginal.copy(nome = nomeEditado)

        onGerenciadorScreen(composeTestRule) {
            cadastrar(usuarioOriginal)
            
            clicarEditarColaborador(usuarioOriginal)
            preencherFormulario(usuarioEditado)
            clicarSalvar()

            verificar { 
                colaboradorNaLista(usuarioEditado) 
            }
        }
    }

    @Test
    fun devePermitirExcluirColaborador() {
        val usuarioParaExcluir = Massa.valida

        onGerenciadorScreen(composeTestRule) {
            cadastrar(usuarioParaExcluir)
            
            clicarExcluirColaborador(usuarioParaExcluir)
            confirmarExclusao()

            verificar { 
                colaboradorNaoEstaNaLista(usuarioParaExcluir)
            }
        }
    }

    @Test
    fun deveLimparFormularioAoCancelarCadastro() {
        onGerenciadorScreen(composeTestRule) {
            clicarAbrirCadastro()
            preencherFormulario(Massa.valida)
            clicarCancelar()
            
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
            
            clicarExcluirColaborador(usuario)
            cancelarExclusao()

            verificar { 
                dialogoExclusaoSumiu()
                colaboradorNaLista(usuario)
            }
        }
    }
}
