import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode

data class DataWrapper(val data: JsonNode)

data class StringDataWrapper(val data: StringNode?)
