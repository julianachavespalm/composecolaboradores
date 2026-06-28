# Compose Colaboradores

Este projeto é uma aplicação Android para gerenciamento de colaboradores, desenvolvida com Jetpack Compose e seguindo os princípios de Clean Architecture. O objetivo é demonstrar a implementação de um CRUD completo com validações de dados e testes automatizados.

## Funcionalidades

*   Gerenciamento de Colaboradores: Cadastro, edição, listagem e exclusão.
*   Validações: Verificação de e-mails, campos obrigatórios e duplicidade.
*   Interface: Implementada com Material Design 3 e suporte a estados de foco.
*   Testes: Suíte de testes instrumentados e unitários.

## Arquitetura

O projeto utiliza Clean Architecture para separar as responsabilidades e facilitar a manutenção:

*   Domain: Modelos de negócio, interfaces de repositório e casos de uso (puro Kotlin).
*   Data: Implementação do repositório em memória.
*   UI: Interface em Jetpack Compose utilizando o padrão MVVM e State Hoisting.

## Tecnologias

*   Kotlin e Jetpack Compose
*   StateFlow e ViewModel
*   JUnit 4 e Compose Test
*   GitHub Actions para integração contínua

## Estrutura do Código

```text
app/
└── kotlin+java/.../composecoladores
    ├── data/                     # Infraestrutura: Implementações e persistência
    ├── domain/                   # Núcleo: Regras de negócio puras (Independente)
    │   ├── model/                # Entidades de negócio
    │   ├── repository/           # Contratos (Interfaces)
    │   └── usecase/              # Casos de Uso individuais (SRP)
    └── ui/                       # Apresentação: Jetpack Compose + MVVM
        ├── components/           # Design System (Componentes reutilizáveis)
        └── gerenciador/          # Tela de Gerenciamento (Feature)
            ├── components/       # UI específica da feature
            ├── ColaboradorViewModel.kt
            └── GerenciadorColaboradoresScreen.kt
```

## Testes

Para executar os testes locais:
*   Unitários: `./gradlew testDebugUnitTest`
*   Instrumentados: `./gradlew connectedDebugAndroidTest`

---
Desenvolvido por Juliana Chaves Palm.
