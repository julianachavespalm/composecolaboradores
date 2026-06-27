# Compose Colaboradores

Projeto Android desenvolvido em Kotlin utilizando **Jetpack Compose** para gerenciamento de colaboradores.

## Tecnologias e Dependências

O projeto utiliza as seguintes bibliotecas e ferramentas:

### Core & UI
- **Kotlin**: Linguagem de programação principal (v2.0.21).
- **Jetpack Compose (BOM 2024.10.01)**: Kit de ferramentas moderno para construção de UI nativa.
- **Material Design 3**: Componentes de interface seguindo as diretrizes mais recentes do Material Design.
- **AndroidX Core KTX**: Extensões para facilitar o desenvolvimento Android com Kotlin.
- **Lifecycle Runtime KTX**: Suporte para o ciclo de vida dos componentes do Android.

### Arquitetura
- **ViewModel**: Gerenciamento de dados relacionados à UI de forma consciente ao ciclo de vida.

### Testes
- **JUnit 4**: Framework para testes unitários.
- **AndroidX Test JUnit & Espresso**: Ferramentas para testes instrumentados no Android.
- **Compose UI Test**: Suporte específico para testes de interfaces desenvolvidas em Compose.
- **UI Automator**: Utilizado para interações com o sistema (como desbloqueio automático de tela).

## Execução de Testes Instrumentados

Para garantir a estabilidade dos testes de interface (especialmente em dispositivos físicos), siga estes passos:

1. **Desabilitar Animações**: No dispositivo/emulador, vá em *Opções do Desenvolvedor* e desative (ou coloque em 0.5x):
   - Escala de animação da janela
   - Escala de animação de transição
   - Escala de duração do animador
2. **Tela**: O teste tenta acordar a tela automaticamente via UI Automator, mas certifique-se de que o dispositivo não esteja protegido por senha/biometria durante a execução para que o desbloqueio funcione.
3. **Comando**: Execute `./gradlew connectedDebugAndroidTest` no terminal.

## Como executar o projeto

1. Clone o repositório.
2. Abra o projeto no **Android Studio (Ladybug ou superior)**.
3. Sincronize o Gradle.
4. Execute o aplicativo em um emulador ou dispositivo físico.