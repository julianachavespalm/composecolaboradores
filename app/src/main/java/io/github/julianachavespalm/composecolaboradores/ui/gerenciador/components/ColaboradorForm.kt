package io.github.julianachavespalm.composecolaboradores.ui.gerenciador.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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

        @OptIn(ExperimentalMaterial3Api::class)
        ExposedDropdownMenuBox(
            expanded = menuExpandido,
            onExpandedChange = {
                focusManager.clearFocus()
                menuExpandido = !menuExpandido
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = if (nivelSelecionado == Nivel.NENHUM) "" else stringResource(nivelSelecionado.descricao),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_nivel)) },
                placeholder = { Text(stringResource(R.string.placeholder_nivel)) },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .testTag(TestTags.CAMPO_NIVEL),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Nivel.entries.filter { it != Nivel.NENHUM }.forEach { nivel ->
                    DropdownMenuItem(
                        text = { Text(stringResource(nivel.descricao)) },
                        onClick = {
                            onNivelChange(nivel)
                            menuExpandido = false
                            focusManager.clearFocus()
                        }
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
                    focusManager.clearFocus()
                    onCancelar()
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
