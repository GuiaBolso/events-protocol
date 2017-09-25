Events Protocol
=================

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
```javascript
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
	"identity":{
		"userId": 99999999
	}
	"metadata": {
		"origin": "Documentation",
		"originId": "RFC-GB 0001",
		"timestamp": 1482162952
	}
}
```

Tipos padrão de resposta
------------------

O protocolo de eventos se baseia na seguinte premissa. O cliente envia um evento para ser processado pelo servidor. O servidor devolve um outro evento como resposta ao cliente. Para isso existem tipos padrão de resposta.

### Response
Tem o objetivo de definir o padrão das mensagens de sucesso da plataforma.
Campos fixos no evento:

* **name:** some:event:response
* **version:** 1

#### Exemplo:

```javascript
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

```javascript
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

```javascript
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

```javascript
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

```javascript
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

### Failed Dependency:

Tipo padronizado para indicar que não foi possível responder com sucesso pois algum worker/dependência não respondeu corretamente (base de dados não são consideradas dependências/workers).

#### Exemplo:

```javascript
{
	"name": "some:event:failed_dependency",
	"version": 1,
	"id": "d8cab3c2-b15b-439b-ae5b-c8a96c61d637",
	"flowId": "49689dcf-80c4-45a6-9f82-61a240e49a5c",
	"payload": {
		"code": "UNKNOWN_ERROR"
		"message": "Anubis returned an error",
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