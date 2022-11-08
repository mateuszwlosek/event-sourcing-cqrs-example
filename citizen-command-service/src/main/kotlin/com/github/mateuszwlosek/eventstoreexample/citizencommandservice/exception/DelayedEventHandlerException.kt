package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.exception

class DelayedEventHandlerException : Exception("Event Handler hasn't caught up to the event stream yet!")
