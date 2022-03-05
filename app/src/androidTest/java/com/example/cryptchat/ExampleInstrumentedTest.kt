package com.example.cryptchat

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.levibostian.recyclerviewmatcher.RecyclerViewMatcher
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private companion object{
        const val TEST_LOGIN = testus
        const val TEST_PASSWORD = 12345678
    }

    @Test
    @DisplayName("Тестирование входа в чат и выхода в список чатов")
    fun testChatOpenAndRead(){
        ActivityScenario.launch(MainActivity::class.java)
            .moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.btToLogin))
            .perform(click())

        onView(withId(R.id.fragment_login))
            .check(matches(isDisplayed()))
        onView(withId(R.id.inputNicknameLogin))
            .perform(typeText(TEST_LOGIN))
        onView(withId(R.id.inputPasswordLogin))
            .perform(typeText(TEST_PASSWORD))
        onView(withId(R.id.btSignIn))
            .perform(click())

        Thread.sleep(3000)


        repeat(3){
            onView(RecyclerViewMatcher.recyclerViewWithId(R.id.rcViewChatPersons)
                .viewHolderViewAtPosition(it + 1, R.id.txChatPerson))
                .perform(click())

            Thread.sleep(3000)

            onView(withId(R.id.fragment_chat))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btStartChat))
                .perform(click())

            Thread.sleep(2000)

            onView(isRoot())
                .perform(pressBack())

            Thread.sleep(2200)
        }

    }

    @Test
    @DisplayName("Тестирование входа в чат и использования чата")
    fun testChatWriting(){
        ActivityScenario.launch(MainActivity::class.java)
            .moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.btToLogin))
            .perform(click())

        onView(withId(R.id.fragment_login))
            .check(matches(isDisplayed()))
        onView(withId(R.id.inputNicknameLogin))
            .perform(typeText(TEST_LOGIN))
        onView(withId(R.id.inputPasswordLogin))
            .perform(typeText(TEST_PASSWORD))
        onView(withId(R.id.btSignIn))
            .perform(click())

        Thread.sleep(2500)

            onView(withId(R.id.fragment_menu))
                .check(matches(isDisplayed()))
            onView(RecyclerViewMatcher.recyclerViewWithId(R.id.rcViewChatPersons)
                .viewHolderViewAtPosition(1, R.id.txChatPerson))
                .perform(click())

            Thread.sleep(3000)

            onView(withId(R.id.fragment_chat))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btStartChat))
                .perform(click())

            Thread.sleep(2000)

            onView(withId(R.id.inputSendMessage))
                .perform(typeText("Hello!"))
                .perform(closeSoftKeyboard())
            onView(withId(R.id.btSendMessage))
                .perform(click())

            onView(withId(R.id.inputSendMessage))
                .perform(typeText("Not hello!"))
                .perform(closeSoftKeyboard())
            onView(withId(R.id.btSendMessage))
                .perform(click())

            Thread.sleep(1000)

    }

    @Test
    @DisplayName("Тестирование логирования и разлогирования")
    fun testSignOut(){
        ActivityScenario.launch(MainActivity::class.java)
            .moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.btToLogin))
            .perform(click())

        onView(withId(R.id.fragment_login))
            .check(matches(isDisplayed()))
        onView(withId(R.id.inputNicknameLogin))
            .perform(typeText(TEST_LOGIN))
        onView(withId(R.id.inputPasswordLogin))
            .perform(typeText(TEST_PASSWORD))
        onView(withId(R.id.btSignIn))
            .perform(click())

        Thread.sleep(2500)

        onView(withId(R.id.fragment_menu))
            .check(matches(isDisplayed()))


        try {
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        } catch (e: Exception) {

        }

        Thread.sleep(200)

        onView(withSubstring("Sign out"))
            .perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.fragment_main_menu))
            .check(matches(isDisplayed()))

        Thread.sleep(1000)

    }

}