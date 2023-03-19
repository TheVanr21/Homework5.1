package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.epam.reportportal.junit5.ReportPortalExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.util.ScreenShooterReportPortalExtension;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static ru.netology.delivery.util.LoggingUtils.logInfo;

@ExtendWith(ScreenShooterReportPortalExtension.class)
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
        register(firstMeetingDate);
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Condition.visible);
        refresh();

        //Second registration
        register(secondMeetingDate);
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Condition.visible);

        //Replan
        $("[data-test-id='replan-notification'] button").click();
        $("[data-test-id='success-notification'] .notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Condition.visible);


    }

    @Test
    @DisplayName("Should plan and replan meeting on today") //not valid test on purpose
    void shouldSuccessfulPlanAndReplanMeetingToday() {
        //Setup days
        daysToAddForFirstMeeting = 4;
        daysToAddForSecondMeeting = 0;
        firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        //First registration
        register(firstMeetingDate);
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Condition.visible);
        refresh();

        //Second registration
        register(secondMeetingDate);
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Condition.visible);

        //Replan
        $("[data-test-id='replan-notification'] button").click();
        $("[data-test-id='success-notification'] .notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Condition.visible);
    }

    void register(String date) {
        $("[data-test-id='city'] input").setValue(city);
        logInfo("В поле ввода введён город " + city);
        $("[data-test-id='date'] input").click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $("[data-test-id='date'] input").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(date);
        logInfo("В поле ввода введена дата " + date);
        $("[data-test-id='name'] input").setValue(name);
        logInfo("В поле ввода введено имя " + name);
        $("[data-test-id='phone'] input").setValue(phone);
        logInfo("В поле ввода введён телефон " + phone);
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Запланировать")).click();
        logInfo("Нажата кнопка \"Запланировать\"");
    }
}
