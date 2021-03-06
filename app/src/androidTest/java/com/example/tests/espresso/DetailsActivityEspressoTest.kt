package com.example.tests.espresso

import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tests.R
import com.example.tests.TEST_NUMBER_OF_RESULTS_MINUS_1
import com.example.tests.TEST_NUMBER_OF_RESULTS_PLUS_1
import com.example.tests.TEST_NUMBER_OF_RESULTS_ZERO
import com.example.tests.tests_details.DetailsActivity
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsActivityEspressoTest {

    /**
     * создаем сценарий и через сценарий убеждаемся, что все базовые вещи работают.
     */
    private lateinit var scenario: ActivityScenario<DetailsActivity>

    /**
     *  ActivityScenario -  класс тестового фреймворка, умеет запускать нужные вам Активити без доступа к Контексту,
     *  так как разработчик в принципе не может самостоятельно запустить Активити, а только создает Интент и перенаправляет
    его ОС. Помимо этого ActivityScenario предоставляет вам возможность управлять жизненным циклом
    Активити, что помогает во многих сценариях тестирования.
     */
    @Before
    fun setup() {
        scenario = ActivityScenario.launch(DetailsActivity::class.java)
    }

    @Test
    fun activity_AssertNotNull() {
        scenario.onActivity {
            TestCase.assertNotNull(it)
        }
    }

    @Test
    fun activity_IsResumed() {
        TestCase.assertEquals(Lifecycle.State.RESUMED, scenario.state)
    }

    @Test
    fun activityTextView_NotNull() {
        scenario.onActivity {
            val totalCountTextView =
                it.findViewById<TextView>(R.id.totalCountTextViewDetails)
            TestCase.assertNotNull(totalCountTextView)
        }
    }

    /**
     * Убедимся, что TextView отображает текст, который положено отображать:
     */
    @Test
    fun activityTextView_HasText() {
        val assertion: ViewAssertion = matches(withText(TEST_NUMBER_OF_RESULTS_ZERO))
        onView(withId(R.id.totalCountTextViewDetails)).check(assertion)
    }

    /**
     * Метод isDisplayed() вернет true если хотя бы часть View отображается на экране.
     */
    @Test
    fun activityTextView_IsDisplayed() {
        onView(withId(R.id.totalCountTextViewDetails)).check(matches(isDisplayed()))
    }

    /**
     * Метод isCompletelyDisplayed() вернет true только если виджет полностью виден.
     */
    @Test
    fun activityTextView_IsCompletelyDisplayed() {
        onView(withId(R.id.totalCountTextViewDetails)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun activityButtons_AreEffectiveVisible() {
        onView(withId(R.id.incrementButton)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.decrementButton)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    /**
     * Тут мы выбираем нужную нам кнопку и имитируем нажатие пользователя. И убеждаемся, что
    TextView меняется как надо.
     */
    @Test
    fun activityButtonIncrement_IsWorking() {
        onView(withId(R.id.incrementButton)).perform(click())
        onView(withId(R.id.totalCountTextViewDetails)).check(matches(withText(TEST_NUMBER_OF_RESULTS_PLUS_1)))
    }

    @Test
    fun activityButtonDecrement_IsWorking() {
        onView(withId(R.id.decrementButton)).perform(click())
        onView(withId(R.id.totalCountTextViewDetails)).check(matches(withText(TEST_NUMBER_OF_RESULTS_MINUS_1)))
    }

    @After
    fun close() {
        scenario.close()
    }
}