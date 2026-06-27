package io.github.julianachavespalm.composecolaboradores.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.julianachavespalm.composecolaboradores.domain.model.Colaborador
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.domain.usecase.GetColaboradoresUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.RemoverColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.SalvarColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.ValidarEmailUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ColaboradorViewModel(
    private val getColaboradoresUseCase: GetColaboradoresUseCase,
    private val salvarColaboradorUseCase: SalvarColaboradorUseCase,
    private val removerColaboradorUseCase: RemoverColaboradorUseCase,
    private val validarEmailUseCase: ValidarEmailUseCase
) : ViewModel() {

    val colaboradores = getColaboradoresUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var nome by mutableStateOf("")
    var email by mutableStateOf("")
    var nivelSelecionado by mutableStateOf(Nivel.NENHUM)
    var colaboradorEmEdicao by mutableStateOf<Colaborador?>(null)

    val isEmailValido: Boolean
        get() = validarEmailUseCase(email)

    val podeSalvar: Boolean
        get() = nome.isNotBlank() && isEmailValido && nivelSelecionado != Nivel.NENHUM

    val estaEditandoOuPreenchido: Boolean
        get() = nome.isNotEmpty() || email.isNotEmpty() || nivelSelecionado != Nivel.NENHUM || colaboradorEmEdicao != null

    fun onNomeChange(novoNome: String) { nome = novoNome }
    fun onEmailChange(novoEmail: String) { email = novoEmail }
    fun onNivelChange(novoNivel: Nivel) { nivelSelecionado = novoNivel }

    fun salvar() {
        if (podeSalvar) {
            val colaborador = Colaborador(
                id = colaboradorEmEdicao?.id ?: 0,
                nome = nome,
                email = email,
                nivel = nivelSelecionado
            )
            salvarColaboradorUseCase(colaborador)
            limparCampos()
        }
    }

    fun editar(colaborador: Colaborador) {
        colaboradorEmEdicao = colaborador
        nome = colaborador.nome
        email = colaborador.email
        nivelSelecionado = colaborador.nivel
    }

    fun remover(id: Int) {
        removerColaboradorUseCase(id)
        if (colaboradorEmEdicao?.id == id) {
            limparCampos()
        }
    }

    fun limparCampos() {
        nome = ""
        email = ""
        nivelSelecionado = Nivel.NENHUM
        colaboradorEmEdicao = null
    }
}
