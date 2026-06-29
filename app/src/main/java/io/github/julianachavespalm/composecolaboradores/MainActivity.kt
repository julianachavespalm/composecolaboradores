package io.github.julianachavespalm.composecolaboradores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.julianachavespalm.composecolaboradores.data.repository.InMemoryColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.usecase.GetColaboradoresUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.RemoverColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.SalvarColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.ValidarEmailUseCase
import io.github.julianachavespalm.composecolaboradores.ui.gerenciador.ColaboradorViewModel
import io.github.julianachavespalm.composecolaboradores.ui.gerenciador.GerenciadorColaboradoresScreen
import io.github.julianachavespalm.composecolaboradores.ui.theme.ComposeColaboradoresTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ColaboradorViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = InMemoryColaboradorRepository()
                return ColaboradorViewModel(
                    GetColaboradoresUseCase(repository),
                    SalvarColaboradorUseCase(repository),
                    RemoverColaboradorUseCase(repository),
                    ValidarEmailUseCase(repository)
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ComposeColaboradoresTheme {
                GerenciadorColaboradoresScreen(viewModel)
            }
        }
    }
}
