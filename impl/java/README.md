Events Protocol
=================

[![Build Status](https://travis-ci.org/GuiaBolso/events-protocol.svg?branch=master)](https://travis-ci.org/GuiaBolso/events-protocol)
[ ![Download](https://api.bintray.com/packages/gb-opensource/maven/Events-Core/images/download.svg) ](https://bintray.com/gb-opensource/maven/Events-Core/_latestVersion)


Dependencias
---------------

Para utilizar as dependências é necessário habilitar o repositório do jcenter no gradle:

```
repositories {
    mavenCentral()
    jcenter()  <---------
}
```


A biblioteca é composta de 4 modulos. Server, Client, Core e Test.

```
 compile "br.com.guiabolso:events-client:2.6.1"
 compile "br.com.guiabolso:events-server:2.6.1"
 compile "br.com.guiabolso:events-core:2.6.1"
 testCompile "br.com.guiabolso:events-test:2.6.1"
```
Geralmente as dependências a serem importadas são:

* Server: Quando é necessário tratar requisições de eventos;
* Client: Quando é necessário realizar requisições de evento;
* Core: Quando é necessário acessar alguma classe específica usada pelo Server ou pelo Client, a partir de um modulo separado.
* Test: Quando você for escrever testes específicos para validação dos eventos. Mais informação na [documentação de testes](doc/test.md)


Motivação
---------------
**Padronizar a comunicação entre as aplicações do GuiaBolso.** Atualmente cada aplicação é livre para definir seus padrões de request/response e protocolo: Thrift, REST, "JSON over HTTP", etc. O que torna difícil trafegar dados padronizados (trackingID, por exemplo) entre as aplicações.

Essa proposta visa definir um padrão de comunicação único que permita:

 * Identificação única dos requests
 * Transações com requests/responses assíncronos ou síncronos
 * Envio de dados que devem passar por toda a stack (trackingID, por exemplo)
 * Versionamento de API
 * identificação de um usuário
 * campos relacionados a autenticação do usuário.
 * Metadados de transações (origin, timestamp, etc)

Arquitetura
----------------

### Evento
Toda informação é transmitida utilizando um JSON padronizado com o seguinte formato.
Qualquer comunicação entre sistemas é considerado um evento.

#### Exemplo:
```json
{
	"name": "some event",
	"version": 42,
	"id": "2465a86f-3857-423e-af86-41f67880172f",
	"flowId": "d00a5c99-ea0e-4b39-bfdc-bf1028a9c95f",
	"payload": {
		"data1": "fsadfsdf",
		"an_int64": 65484548474984654,
		"some_float": 1864.4568
	},
	"identity": {
		"userId": 99999999
	},
	"auth": {
		"token": "fk20f2p9o2v2l923"
	},
	"metadata": {
		"origin": "Documentation",
		"originId": "RFC-GB 0001",
		"timestamp": 1482162952
	}
}
```

Como usar
------------------

## Cliente

```TODO```

## Servidor

O protocolo foi desenhado para ser facilmente acoplado em qualquer estrutura para receber requisições: sockets, chamadas HTTP. Para tanto, dois tipos de objetos precisam ser cadastrados na sua aplicação para que tudo funcione.

### ```EventHandlerDiscovery```

É o responsável por avaliar se, dado o nome e versão de um evento, o sistema é capaz de fazer o tratamento do mesmo. A implementação padrão da interface é ```SimpleEventHandlerRegistry``` que cadastra uma série de ```EventHandler```'s (ou lambdas) para fazer o tratamento de um determinado evento pelo seu nome e versão. Em ambos os casos, um ```RequestEvent``` é recebido e um ```ResponseEvent``` é gerado baseado na requisição e a resposta do processamento realizado (se necessário).

#### Exemplo:
```groovy
simpleEventHandlerRegistry.add("say:hello", 13, (EventHandler) { RequestEvent event ->
	def firstname = event.payload.asJsonObject.get("firstname").asString
	def lastname = event.payload.asJsonObject.get("lastname").asString
	def eventBuilder = EventBuilder.javaResponseFor(event)
	eventBuilder.payload = helloService.to(firstname, lastname)
	eventBuilder.buildResponseEvent()
})
```

### ```EventProcessor```

Responsável por efetivamente processar os eventos. O evento nessa classe é representado por uma ```String``` que é traduzido para um objeto no modelo do protocolo de eventos e avaliado para garantir se há ou não suporte para que seja tratado. Um ```EventHandlerDiscovery``` que avalia qual o processador para um dado evento deve ser passado como parâmetro para esse objeto. É nele ainda que os tratamentos de erro e avaliação de métricas podem ser sobrescritos.

#### Exemplo:
```groovy
post("/events", { request, response ->
	eventProcessor.processEvent(req.body())
})
```

Nota: no caso de chamadas via HTTP, recomenda-se que se crie uma única rota para tratamento de eventos (vide exemplo) para que todos os eventos sejam processados de maneira igual. Quem define a rota do evento nesse caso é o ```EventProcessor```.

Tipos padrão de resposta
------------------

O protocolo de eventos se baseia na seguinte premissa. O cliente envia um evento para ser processado pelo servidor. O servidor devolve um outro evento como resposta ao cliente. Para isso existem tipos padrão de resposta.

### Response
Tem o objetivo de definir o padrão das mensagens de sucesso da plataforma.
Campos fixos no evento:

* **name:** some:event:response
* **version:** 1

#### Exemplo:

```json
{
	"name": "some event:response",
	"version": 1,
	"id": "e577b9d8-5b39-4aa6-bd74-418ec2f74174",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"someData": "this is the server response",
		"someOtherData": 42
	},
	"metadata": {
		"origin": "Lion",
		"timestamp": 1282162952
	}
}
```


### Erro genérico:
Tem o objetivo de definir o padrão do payload das mensagens de erro genéricos da plataforma.
Campos fixos no evento:

* **name:** some:event:error
* **version:** 1

#### Exemplo:

```json
{
	"name": "some:event:error",
	"version": 1,
	"id": "e577b9d8-5b39-4aa6-bd74-418ec2f74174",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "INVALID_EMAIL",
		"parameters": {
			"email": "wrong@gbmail.com"
		}
	},
	"metadata": {
		"origin": "Midgard",
		"timestamp": 1282162952
	}
}
```

### Erros contextualizados:

Tem o objetivo de definir o padrão do payload das mensagens de erros que carregam um contexto dentro da plataforma.

Esses erros podem ser:

### Bad Request:

Indica que o request foi feito ao servidor de maneira incorreta. Provavelmente com algum parametro do request faltando ou e formato incorreto.  

#### Exemplo:

```json
{
	"name": "some:event:bad_request",
	"version": 1,
	"id": "e577b9d8-5b39-4aa6-bd74-418ec2f74174",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "MISSING_FIELDS",
		"parameters": {
			"fields": ["cpf", "phone"]
		}
	},
	"metadata": {
		"origin": "Midgard",
		"timestamp": 1282162952
	}
}
```

### Unauthorized:

Indica que o cliente não está autorizado acessar aquela API no servidor. Provavelmente o cliente não está autenticado e precisa passar pelo processo de login.

#### Exemplo:

```json
{
	"name": "some:event:unauthorized",
	"version": 1,
	"id": "e577b9d8-5b39-4aa6-bd74-418ec2f74174",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "MISSING_TOKENS",
		"parameters": {
		}
	},
	"metadata": {
		"origin": "Midgard",
		"timestamp": 1282162952
	}
}
```

### Not Found:

Indica que o servidor não encontrou o recurso solicitado pelo cliente.

#### Exemplo:

```json
{
	"name": "some:event:not_found",
	"version": 1,
	"id": "e577b9d8-5b39-4aa6-bd74-418ec2f74174",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "NO_OFFERS",
		"parameters": {
		}
	},
	"metadata": {
		"origin": "Midgard",
		"timestamp": 1282162952
	}
}
```

### Forbidden:

Indica que o servidor impede o acesso a um recurso devido ser proibido por algum motivo. 

#### Exemplo:

```json
{
	"name": "some:event:forbidden",
	"version": 1,
	"id": "e577b9d8-5b39-4aa6-bd74-418ec2f74174",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "NOT_ALLOWED",
		"parameters": {
		}
	},
	"metadata": {
		"origin": "Midgard",
		"timestamp": 1282162952
	}
}
```

### Failed Dependency:

Tipo padronizado para indicar que não foi possível responder com sucesso pois algum worker/dependência não respondeu corretamente (base de dados não são consideradas dependências/workers).

#### Exemplo:

```json
{
	"name": "some:event:failed_dependency",
	"version": 1,
	"id": "d8cab3c2-b15b-439b-ae5b-c8a96c61d637",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "UNKNOWN_ERROR",
		"message": "Anubis returned an error"
	},
	"metadata": {
		"origin": "Ryzen",
		"timestamp": 1482165952
	}
}
```


Referências
-----------
* **JSON RPC:** [http://www.jsonrpc.org/specification#examples](http://www.jsonrpc.org/specification#examples)
* **Slack RealTime Messaging API:** [https://api.slack.com/rtm](https://api.slack.com/rtm)
