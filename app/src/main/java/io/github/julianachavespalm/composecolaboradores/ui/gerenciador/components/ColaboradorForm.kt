package io.github.julianachavespalm.composecolaboradores.ui.gerenciador.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.julianachavespalm.composecolaboradores.R
import io.github.julianachavespalm.composecolaboradores.domain.model.Nivel
import io.github.julianachavespalm.composecolaboradores.ui.TestTags
import io.github.julianachavespalm.composecolaboradores.ui.components.AppTextField

@Composable
fun ColaboradorForm(
    nome: String,
    onNomeChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    nivelSelecionado: Nivel,
    onNivelChange: (Nivel) -> Unit,
    podeSalvar: Boolean,
    estaEditando: Boolean,
    isEmailValido: Boolean,
    jaExiste: Boolean,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
    focusRequester: FocusRequester
) {
    var menuExpandido by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        AppTextField(
            value = nome,
            onValueChange = onNomeChange,
            label = stringResource(R.string.label_nome),
            icon = Icons.Default.Person,
            modifier = Modifier
                .focusRequester(focusRequester)
                .testTag(TestTags.CAMPO_NOME),
            isError = jaExiste,
            onImeAction = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.label_email),
            icon = Icons.Default.Email,
            modifier = Modifier.testTag(TestTags.CAMPO_EMAIL),
            isError = (email.isNotBlank() && !isEmailValido) || jaExiste,
            supportingText = when {
                email.isNotBlank() && !isEmailValido -> {
                    if (email.contains(" ")) stringResource(R.string.erro_email_espaco)
                    else stringResource(R.string.erro_email_invalido)
                }
                jaExiste -> stringResource(R.string.erro_colaborador_ja_cadastrado)
                else -> null
            },
            onImeAction = { menuExpandido = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (nivelSelecionado == Nivel.NENHUM) "" else nivelSelecionado.descricao,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_nivel)) },
                placeholder = { Text(stringResource(R.string.placeholder_nivel)) },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                trailingIcon = {
                    IconButton(onClick = { menuExpandido = !menuExpandido }) {
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.CAMPO_NIVEL),
                shape = MaterialTheme.shapes.medium
            )
            DropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Nivel.entries.filter { it != Nivel.NENHUM }.forEach { nivel ->
                    DropdownMenuItem(
                        text = { Text(nivel.descricao) },
                        onClick = {
                            onNivelChange(nivel)
                            menuExpandido = false
                        },
                        modifier = Modifier.testTag("menu_item_${nivel.name}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    onCancelar()
                    focusManager.clearFocus()
                },
                modifier = Modifier.weight(1f).testTag(TestTags.BOTAO_CANCELAR),
                shape = MaterialTheme.shapes.medium,
                enabled = nome.isNotEmpty() || email.isNotEmpty() || nivelSelecionado != Nivel.NENHUM || estaEditando
            ) {
                Text(stringResource(R.string.acao_cancelar))
            }

            Button(
                onClick = {
                    onSalvar()
                    focusManager.clearFocus()
                },
                modifier = Modifier.weight(1f).testTag(TestTags.BOTAO_SALVAR),
                enabled = podeSalvar,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    stringResource(if (estaEditando) R.string.acao_atualizar else R.string.acao_salvar)
                )
            }
        }
    }
}
