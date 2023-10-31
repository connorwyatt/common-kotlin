package io.connorwyatt.common.time

import io.connorwyatt.common.time.clock.Clock
import io.connorwyatt.common.time.clock.RealClock
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.new

val timeDependenciesModule by DI.Module { bindProvider<Clock> { new(::RealClock) } }
