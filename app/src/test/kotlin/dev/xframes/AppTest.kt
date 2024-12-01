package dev.xframes

import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
    @Test fun appHasAGreeting() {
        val classUnderTest = XFramesWrapper()
        assertNotNull(classUnderTest.greeting, "app should have a greeting")
    }
}
