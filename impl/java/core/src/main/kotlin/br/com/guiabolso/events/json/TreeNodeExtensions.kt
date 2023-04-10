package br.com.guiabolso.events.json

fun <T> TreeNode.withCheckedJsonNull(checkedParam: String, block: (node: TreeNode) -> T?): T? {
    val jsonNode = this[checkedParam]
    return if (jsonNode is JsonNull || jsonNode == null) {
        null
    } else {
        block(this)
    }
}
