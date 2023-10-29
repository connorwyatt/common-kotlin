package io.connorwyatt.common.eventstore.events

data class VersionedEventType(val name: String, val version: Int) {
    override fun toString() = "$name.v$version"

    companion object {
        private val regex = Regex("(?<name>.+\\..+)\\.v(?<version>\\d+)")

        fun parse(value: String): VersionedEventType {
            return regex.matchEntire(value)?.let {
                VersionedEventType(it.groups["name"]!!.value, it.groups["version"]!!.value.toInt())
            }
                ?: throw Exception("Value provided does not match the pattern.")
        }
    }
}
