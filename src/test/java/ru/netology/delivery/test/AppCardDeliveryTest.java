package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

class AppCardDeliveryTest {

    DataGenerator.UserInfo validUser;
    String city;
    String firstMeetingDate;
    String secondMeetingDate;
    String name;
    String phone;
    int daysToAddForFirstMeeting;
    int daysToAddForSecondMeeting;

    @BeforeEach
    void setup() {
        validUser = DataGenerator.Registration.generateUser("ru");
        city = validUser.getCity();
        name = validUser.getName();
        phone = validUser.getPhone();

        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        //Setup days
        daysToAddForFirstMeeting = 4;
        daysToAddForSecondMeeting = 7;
        firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        //First registration
        $("[data-test-id='city'] input").setValue(city);
        $("[data-test-id='date'] input").click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id='date'] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Запланировать")).click();

        $("[data-test-id='success-notification'] .notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Condition.visible);

        refresh();

        //Second registration
        $("[data-test-id='city'] input").setValue(city);
        $("[data-test-id='date'] input").click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id='date'] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Запланировать")).click();

        $("[data-test-id='replan-notification'] .notification__content").shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Condition.visible);

        //Replan
        $("[data-test-id='replan-notification'] button").click();

        $("[data-test-id='success-notification'] .notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Condition.visible);
    }
}
