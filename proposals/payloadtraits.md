# Payload Traits

- **Propositante**: [@octaviogb](https://github.com.br/octaviogb)
- **Escopo**: payload
- **Criado em**: 15 jan 2020
- **Estado**: rascunho

## Problema

Há estruturas que se tornam obrigatórias no payload ao definir um `prefixo`

Não há estruturas semânticas que aumentem a especificidade do `payload`

## Proposta

Criação de `traits` opcionais e obrigatórias que serão utilizadas e validades em função do `payload`

## Trait

Trait é um conjunto de chaves que serão colocadas no `payload` com valor semântico

Uma trait pode refletir um `prefixo` do `name`

Uma trait pode aumentar a `especificidade` do `payload`

### traits

#### obrigatórias

##### Error trait

**caso**: em caso de prefixo de erro

**chaves**
```
code    =   chave de identificação do erro
message =   mensagem sugerida pelo servidor
```

- **code**: chave de identificação do erro semântica. Nunca deve ser um erro e deve ser usada para indexar os erros para o cliente
- **message**: mensagem sugerida pelo servidor, de conteúdo aberto

#### de especificidade

Devem ser chaves prefixadas com `_` e não alteram o valor de negócio do payload

##### Schema

**caso**: para garantir a validação do payload

**chaves**
```
_schema =   conjunto de chaves que reflete as chaves do payload
```

- **_schema**: conjunto de chaves que reflete as chaves do payload que deve ser usado para o servidor validar o payload, utilizando a sintaxe de `{ campo: tipo, ... }`. Ex

```
***

payload
    _schema
        name: string
    name "jorge"
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
