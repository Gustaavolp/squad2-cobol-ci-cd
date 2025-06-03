# Guia de Estilo de Código

## Princípios Gerais

- Mantenha o código simples e legível
- Prefira funções puras e imutabilidade
- Siga o princípio DRY (Don't Repeat Yourself)
- Componentes devem ter responsabilidade única

## Nomenclatura

- **Classes**: PascalCase (ex: `UserService`)
- **Interfaces**: PascalCase com prefixo I (ex: `IUserData`)
- **Variáveis e funções**: camelCase (ex: `getUserData()`)
- **Constantes**: SNAKE_CASE_MAIÚSCULO (ex: `MAX_RETRY_COUNT`)
- **Arquivos**: kebab-case (ex: `user-service.ts`)

## Estrutura de Arquivos

- Um componente por arquivo
- Nome do arquivo deve refletir o conteúdo (ex: `user-list.component.ts`)
- Agrupar arquivos relacionados em diretórios

## Angular Específico

- Prefixar seletores de componentes com `app-` (ex: `app-user-list`)
- Usar OnPush change detection quando possível
- Evitar manipulação direta do DOM, preferir Renderer2
- Usar lazy loading para módulos de feature

## TypeScript

- Sempre definir tipos explícitos
- Evitar `any` - usar `unknown` quando necessário
- Usar interfaces para definir contratos
- Preferir readonly para propriedades imutáveis

## HTML/CSS

- Usar SCSS para estilos
- Evitar estilos inline
- Manter templates limpos e legíveis