package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.TreeNode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TreeNodeSerializer : KSerializer<TreeNode> {
    private val mapSerializer = MapSerializer(String.serializer(), JsonNodeSerializer)

    private object TreeNodeDescriptor : SerialDescriptor by mapSerializer.descriptor {
        @ExperimentalSerializationApi
        override val serialName: String = TreeNode::class.qualifiedName!!
    }

    override val descriptor: SerialDescriptor = TreeNodeDescriptor

    override fun serialize(encoder: Encoder, value: TreeNode) {
        mapSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): TreeNode {
        return TreeNode(
            mapSerializer.deserialize(decoder).toMutableMap()
        )
    }
}
