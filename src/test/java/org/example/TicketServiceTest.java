package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class TicketServiceTest {

    private final TicketService service = new TicketService();

    @Test
    void getPlanInfo_ShouldReturnCorrectPricing() {
        String basicInfo = service.getPlanInfo("basic");
        assertThat(basicInfo)
                .contains("Basic Plan")
                .contains("$20");

        String proInfo = service.getPlanInfo("pro");
        assertThat(proInfo)
                .contains("Pro Plan")
                .contains("$50");
    }

    @Test
    void checkRefund_ShouldApproveRecentPurchase() {
        String today = LocalDate.now().toString();
        String result = service.checkRefund(today);

        assertThat(result).isEqualTo("Eligible for refund.");
    }

    @Test
    void checkRefund_ShouldRejectOldPurchase() {
        String oldDate = "2023-01-01";
        String result = service.checkRefund(oldDate);

        assertThat(result)
                .contains("Not eligible")
                .contains(String.valueOf(AppConfig.REFUND_POLICY_DAYS));
    }

    @Test
    void checkRefund_ShouldHandleInvalidDateFormat() {
        String result = service.checkRefund("invalid-date");

        assertThat(result)
                .contains("Error")
                .contains("YYYY-MM-DD");
    }
}