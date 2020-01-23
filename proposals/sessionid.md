# SessionId

- **Propositante**: [@octaviogb](https://github.com.br/octaviogb)
- **Escopo**: propriedades
- **Criado em**: 15 jan 2020
- **Estado**: rascunho

## Problema

A semântica do termo `id` não traduz a sua natureza de identificador de ciclo de vida da sessão

A semântica do termo `id` representa melhor um identificador do lançamento de um evento

## Proposta

Criação da propriedade `sessionId`, respeitando as mesmas regras de composição de `id` 

Substituição da função da propriedade `id`, abrangendo a natureza de identificador do lançamento de um evento

## Protocolo

```
***

flowId      =   identificador do fluxo, do início ao fim
sessionId   =   identificador da sessão do terminal
id          =   identificador do lançamento do evento

```

# Licença

Copyright @ 2016 Guiabolso

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
