package io.github.julianachavespalm.composecolaboradores.ui.gerenciador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.ui.TestTags
import io.github.julianachavespalm.composecolaboradores.ui.gerenciador.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciadorColaboradoresScreen(viewModel: ColaboradorViewModel) {
    val listaColaboradores by viewModel.colaboradores.collectAsState()
    var colaboradorParaExcluir by remember { mutableStateOf<Int?>(null) }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold
                    ) 
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag(TestTags.LISTA_COLABORADORES),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            if (viewModel.colaboradorEmEdicao == null) R.string.label_cadastrar 
                            else R.string.label_editar
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    ColaboradorForm(
                        nome = viewModel.nome,
                        onNomeChange = { viewModel.onNomeChange(it) },
                        email = viewModel.email,
                        onEmailChange = { viewModel.onEmailChange(it) },
                        nivelSelecionado = viewModel.nivelSelecionado,
                        onNivelChange = { viewModel.onNivelChange(it) },
                        podeSalvar = viewModel.podeSalvar,
                        estaEditando = viewModel.colaboradorEmEdicao != null,
                        isEmailValido = viewModel.isEmailValido,
                        jaExiste = viewModel.jaExiste,
                        onSalvar = { viewModel.salvar() },
                        onCancelar = { viewModel.limparCampos() },
                        focusRequester = focusRequester
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }

            if (listaColaboradores.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 32.dp), Alignment.Center) {
                        Text(
                            text = stringResource(R.string.nenhum_colaborador_cadastrado),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(listaColaboradores, key = { it.id }) { colaborador ->
                    ColaboradorCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colaborador = colaborador,
                        onEdit = {
                            viewModel.editar(colaborador)
                            focusRequester.requestFocus()
                        },
                        onDelete = { colaboradorParaExcluir = colaborador.id }
                    )
                }
            }
        }
    }

    if (colaboradorParaExcluir != null) {
        ConfirmacaoExclusaoDialog(
            onConfirm = {
                colaboradorParaExcluir?.let { viewModel.remover(it) }
                colaboradorParaExcluir = null
            },
            onDismiss = { colaboradorParaExcluir = null }
        )
    }
}
