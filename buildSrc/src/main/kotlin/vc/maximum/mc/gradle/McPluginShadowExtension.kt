package vc.maximum.mc.gradle

open class McPluginShadowExtension {
    var archiveFileName: String? = null

    private val relocations = mutableListOf<Pair<String, String>>()

    fun relocation(fromPackage: String, toPackage: String) {
        relocations.add(fromPackage to toPackage)
    }

    internal fun relocations(): List<Pair<String, String>> = relocations.toList()
}
