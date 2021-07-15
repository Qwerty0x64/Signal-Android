package org.thoughtcrime.securesms.conversation.colors.ui

import androidx.lifecycle.LiveData
import org.signal.core.util.concurrent.SignalExecutors
import org.thoughtcrime.securesms.conversation.colors.ChatColors
import org.thoughtcrime.securesms.conversation.colors.ChatColorsPalette
import org.thoughtcrime.securesms.database.ChatColorsDatabase
import org.thoughtcrime.securesms.database.DatabaseFactory
import org.thoughtcrime.securesms.database.DatabaseObserver
import org.thoughtcrime.securesms.dependencies.ApplicationDependencies
import org.thoughtcrime.securesms.util.concurrent.SerialMonoLifoExecutor
import java.util.concurrent.Executor

class ChatColorsOptionsLiveData : LiveData<List<ChatColors>>() {
  private val chatColorsDatabase: ChatColorsDatabase = DatabaseFactory.getChatColorsDatabase(ApplicationDependencies.getApplication())
  private val observer: DatabaseObserver.Observer = DatabaseObserver.Observer { refreshChatColors() }
  private val executor: Executor = SerialMonoLifoExecutor(SignalExecutors.BOUNDED)

  override fun onActive() {
    refreshChatColors()
    ApplicationDependencies.getDatabaseObserver().registerChatColorsObserver(observer)
  }

  override fun onInactive() {
    ApplicationDependencies.getDatabaseObserver().unregisterObserver(observer)
  }

  private fun refreshChatColors() {
    executor.execute {
      val options = mutableListOf<ChatColors>().apply {
        addAll(ChatColorsPalette.Bubbles.all)
        addAll(chatColorsDatabase.getSavedChatColors())
      }

      postValue(options)
    }
  }
}
