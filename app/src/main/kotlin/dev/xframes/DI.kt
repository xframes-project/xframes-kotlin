package dev.xframes

import org.koin.dsl.module

val appModule = module {
    single { CounterService() }
}