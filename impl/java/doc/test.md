Testando Eventos
--


O módulo de testes do protocolo de evento tem por objetivo facilitar os testes relacionados ao protocolo de eventos, com matchers do [Kotest](https://github.com/kotest/kotest) e outras utilidades para validação de eventos.





### Matchers

Para entender como os matchers do Kotest funcionam, dê uma olhada na [documentação do Kotest](https://github.com/kotest/kotest/blob/master/doc/matchers.md). Num geral a sintaxe é simples e permanece a mesma para todos os matchers:

```kotlin
a should* b
a shouldNot* c
``` 

Por exemplo, o matcher de nome seria utilizado dessa forma:
```kotlin
myEvent shouldHaveName "ABC"
myEvent shouldNotHaveName "XYZ"
```

Todo matcher possui sua versão negativa.

| Eventos Gerais | |
| -------- | ---- |
| event.shouldHaveName(name) | Verifica que o evento possui nome `name` |
| event.shouldHaveVersion(ver) | Verifica que o evento possui versão `ver` |
| event.shouldHaveId(id) | Verifica que o evento possui id `id` |
| event.shouldHaveFlowId(flowId) | Verifica que o evento possui flowId `flowId` |
| event.shouldContainPayload(key, value) | Verifica que a chave `key` do campo `payload` possui valor `value`. Aceita caminhos, por exemplo `$.chave.outrachave` |
| event.shouldHavePayload(map) | Verifica que o campo `payload` é exatamente `map` |
| event.shouldContainIdentity(key, value) | Verifica que a chave `key` do campo `identity` possui valor `value`. Aceita caminhos, por exemplo `$.chave.outrachave`|
| event.shouldHaveIdentity(map) | Verifica que o campo identity é exatamente `map` |
| event.shouldContainAuth(key, value) | Verifica que a chave `key` do campo `auth` possui valor `value`. Aceita caminhos, por exemplo `$chave.outrachave` |
| event.shouldHaveAuth(map) | Verifica que o campo `auth` é exatamente `map` |
| event.shouldContainMetadata(key, value) | Verifica que a chave `key` do campo `metadata` possui valor `value`. Aceita caminhos, por exemplo `$chave.outrachave` |
| event.shouldHaveMetadata(map) | Verifica que o campo `metadata` é exatamente `map` |
| event.shouldContainUserId(long) | Verifica que o campo `identity` possui a variável `userId` com valor `long` |
| event.shouldHaveOrigin(str) | Verifica que o campo `metadata` possui a variável `origin` com valor `str` |

| Response Events | |
| ------ | ---- |
| response.shouldBeSuccess() | Verifica que esta resposta foi um sucesso |
| response.shouldBeRedirect() | Verifica que esta resposta foi um redirect |
| response.shouldBeError() | Verifica que esta resposta foi um erro |
| response.shouldHaveErrorType(errorType) | Verifica que esta reposta possui error type _errorType_ |
