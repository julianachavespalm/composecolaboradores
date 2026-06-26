package io.github.julianachavespalm.composecolaboradores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.julianachavespalm.composecolaboradores.data.repository.ColaboradorRepository
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.domain.usecase.GetColaboradoresUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.RemoverColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.domain.usecase.SalvarColaboradorUseCase
import io.github.julianachavespalm.composecolaboradores.ui.ColaboradorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialização manual das dependências
        val repository = ColaboradorRepository()
        val viewModel = ColaboradorViewModel(
            GetColaboradoresUseCase(repository),
            SalvarColaboradorUseCase(repository),
            RemoverColaboradorUseCase(repository)
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GerenciadorColaboradoresScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciadorColaboradoresScreen(viewModel: ColaboradorViewModel) {
    val listaColaboradores by viewModel.colaboradores.collectAsState()
    var menuExpandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (viewModel.colaboradorEmEdicao == null) "Cadastrar Colaborador" else "Editar Colaborador",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = viewModel.nome,
            onValueChange = { viewModel.onNomeChange(it) },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = viewModel.nivelSelecionado.descricao,
                onValueChange = {},
                readOnly = true,
                label = { Text("Nível") },
                trailingIcon = {
                    IconButton(onClick = { menuExpandido = !menuExpandido }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Nivel.values().forEach { nivel ->
                    DropdownMenuItem(
                        text = { Text(nivel.descricao) },
                        onClick = {
                            viewModel.onNivelChange(nivel)
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.salvar() },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (viewModel.colaboradorEmEdicao == null) "Salvar" else "Atualizar")
            }

            if (viewModel.colaboradorEmEdicao != null || viewModel.nome.isNotBlank() || viewModel.email.isNotBlank()) {
                OutlinedButton(
                    onClick = { viewModel.limparCampos() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaColaboradores) { colaborador ->
                ListItem(
                    headlineContent = { Text(colaborador.nome) },
                    supportingContent = { Text("${colaborador.email} (${colaborador.nivel.descricao})") },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { viewModel.editar(colaborador) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { viewModel.remover(colaborador.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir")
                            }
                        }
                    }
                )
            }
        }
    }
}
