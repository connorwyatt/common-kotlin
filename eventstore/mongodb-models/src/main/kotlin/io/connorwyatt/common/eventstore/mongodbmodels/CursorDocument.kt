package io.connorwyatt.common.eventstore.mongodbmodels

import io.connorwyatt.common.eventstore.eventhandlers.EventHandler.Cursor
import io.connorwyatt.common.mongodb.CollectionName
import org.bson.codecs.pojo.annotations.BsonId

@CollectionName("cursors")
data class CursorDocument(
    val subscriptionName: String,
    val streamName: String,
    val lastHandledStreamPosition: Long,
    @BsonId val _id: String = primaryKey(subscriptionName, streamName),
) {
    companion object {
        fun fromCursor(todo: Cursor): CursorDocument =
            CursorDocument(
                todo.subscriptionName,
                todo.streamName,
                todo.lastHandledStreamPosition,
            )

        private fun primaryKey(subscriptionName: String, streamName: String) =
            "$subscriptionName::$streamName"
    }
}
