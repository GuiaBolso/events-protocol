# Protocolo de eventos

O protocolo de eventos é um esquema de abstração sobre a camada de transporte, garantindo rastreabilidade.

Essa abstração se baseia em um conjunto de convenções entre Ids e pacotes de metadados e autenticação que ajudam no rastreio e manutenção de sistemas distribuídos.

## O problema

Quando trabalhamos com sistemas distribuídos, ao utilizarmos a implementação padrão do REST acabamos amarrados ao protocolo HTTP, mas não é uma realidade fazer a comunicação somente via HTTP. Há disponíveis protocolos de fila, socket, troca de arquivos.

Ao fazer uma requisição, facilmente perdemos rastreio do ponto em que algo se perdeu.

## A proposta

Criar um esquema que garanta a troca de chaves, o registro de metadatos e o encapsulamento do payload, garantindo o histórico da chamada e a intenção do evento.

Ao invés de representar um estado, representar um evento gerado por um terminal. Esse evento vai ser transformado e acompanhado em todo seu ciclo de vida.

## O protocolo

O protocolo é construído em propriedades, que podem ser traduzidas em

```
name        =   identificador do evento
version     =   versao única do payload evento

flowId      =   identificador do fluxo, do início ao fim
id          =   identificador da sessão do terminal

payload     =   conteúdo do pacote do evento
metadata    =   dados informativos sobre o evento
identity    =   identidade do terminal
auth        =   chaves de autenticação
```

### name

Identificador do evento, identificação com o domínio, o recurso e a ação que foi tomada sobre esse recurso

Cabe ao nome também trazer a natureza do event

No caso de uma resposta de sucesso, será sufixados `response`, no caso de um erro, o código de identificação

#### notação

O nome possui uma notação específica, que traduz o domínio, o recurso e a ação que gerou o evento

Para garantir a universalidade, a notação é separada por `:` e as identificações serão em letras minúsculas apenas e underscores

```
[a-z_]+[a-z]:[a-z_]+[a-z]:[a-z_]+[a-z](:[a-z]+[a-z])*
```

Exemplo:

```
user:name:set
```

#### sufixos

Os sufixos são utilizados nas respostas à um evento

Todo evento deve ser respondido

Os sufixos podem ser de sucesso ou falha

##### sucesso

- `response`: utilizado em caso de completude da requisição

##### falha

- `error`: erro genérico de origem no servidor
- `bad_request`: erro de natureza do requisitante
- `unauthorized`: `auth` sem capacidade de transcrição ou gerador de permissão
- `not_found`: evento indisponível no servidor
- `forbidden`: `identity` proibido de acessar recurso
- `failed_dependency`: falha por dependência não devolver resposta esperada

### version

Versão única do payload do evento, garantindo a compatibilidade dos clientes e servidores envolvidos naquele ciclo

A versão também pode ser utilizada para identificar aquele evento como `beta`, `alpha` ou `test`

### flowId

Identificador do fluxo, do início ao fim. Este identificador deve ser uma chave que garanta a unicidade do fluxo

Um fluxo é o conjunto de eventos que começa no terminal, as chamadas para os próximos servidores e clientes, até a resposta retornar ao terminal

### id

Identificador da sessão do terminal, que deve encapsular um conjunto de eventos em um período de interação em um terminal

### payload

Conteúdo do pacote do evento, sendo crucial para o sucesso ou falha daquele evento

### metadata

Dados informativos sobre o evento, não podendo interferir no sucesso ou falha daquele evento

### identity

Identidade do terminal, contendo dados que possam identifica unicamente o terminal

Não deve ser preenchida concomitantemente com `auth`

### auth

Chaves de autenticação, tokens como `JWT`, cifradas e assinadas

Não deve ser preenchida concomitantemente com `identity`

Não deve conter identificadore abertos

# Licença

Copyright 2016 Guiabolso

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.