# Compose Colaboradores 🚀

Projeto Android demonstrativo focado em boas práticas de engenharia de software, utilizando **Jetpack Compose**, **Clean Architecture** e **Testes Automatizados**. O aplicativo permite o gerenciamento completo (CRUD) de colaboradores com validações de negócio robustas.

## ✨ Funcionalidades

- **Gerenciamento de Colaboradores**: Cadastro, edição, listagem e exclusão.
- **Validações em Tempo Real**: Feedback visual para e-mails inválidos, duplicados ou campos obrigatórios.
- **Experiência de Usuário**: UI moderna com Material 3, suporte a teclado (IME actions) e gestão de foco automática.
- **Estabilidade**: Cobertura de testes unitários e instrumentados para fluxos críticos.

## 🏗️ Arquitetura

O projeto segue os princípios da **Clean Architecture**, garantindo testabilidade, desacoplamento e facilidade de manutenção.

- **Domain Layer**: Contém os modelos de negócio (`Colaborador`), as interfaces dos repositórios e os **Use Cases** individuais (Salvar, Remover, Buscar, Validar). É pura Kotlin e não depende de frameworks.
- **Data Layer**: Implementa o contrato definido pelo domínio. Atualmente utiliza um `InMemoryColaboradorRepository` para persistência volátil, mas pronto para ser estendido para Room ou Retrofit.
- **UI Layer**: Implementada com Jetpack Compose seguindo o padrão **MVVM**. Utiliza **State Hoisting** para componentes puros e desacoplados do ViewModel.

## 🛠️ Tech Stack

- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) com Material Design 3
- **Arquitetura**: MVVM + Clean Architecture
- **Gerenciamento de Estado**: StateFlow e Compose States
- **Injeção de Dependência**: Manual (preparado para Hilt/Koin)
- **CI/CD**: GitHub Actions com emulador automatizado (KVM)

## 🧪 Qualidade e Testes

A qualidade é garantida por uma suíte de testes abrangente e uma esteira de integração contínua.

### Testes Unitários (JUnit 4)
- **Foco**: Validação de regras de negócio, lógica de IDs, persistência em memória e validação de formatos (e-mail).
- **Localização**: `app/src/test/java/.../`
- **Execução**: `./gradlew testDebugUnitTest`

### Testes Instrumentados (Compose Test)
- **Page Object Pattern**: Encapsulamento da lógica de busca de elementos de UI no `ComposeColaboradoresPage`.
- **Massa de Dados (Object Mother)**: Centralização de dados para testes.
- **Test Tags Estáticas**: Uso de um objeto central de constantes para garantir que refatorações na UI não quebrem os identificadores de teste.
- **Execução**: `./gradlew connectedDebugAndroidTest`

### CI/CD Pipeline
Configurado via **GitHub Actions** (`.github/workflows/android.yml`), executando testes unitários e instrumentados em cada Pull Request:
- **Estratégia**: Execução em ambiente Linux (Ubuntu) com aceleração **KVM** habilitada.
- **Emulador**: Android API 29 (estabilidade e compatibilidade com imagens disponíveis no runner).

## 🚀 Como executar

1. Clone o repositório.
2. Abra no **Android Studio Ladybug** (ou superior).
3. Para rodar os testes instrumentados localmente:
   - Certifique-se de ter um emulador aberto (recomendado API 29+).
   - Execute: `./gradlew connectedDebugAndroidTest`

---
Desenvolvido com ❤️ por Juliana Chaves Palm.
