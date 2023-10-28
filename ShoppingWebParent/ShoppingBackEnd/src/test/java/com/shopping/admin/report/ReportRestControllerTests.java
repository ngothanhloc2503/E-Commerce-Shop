package com.shopping.admin.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@AutoConfigureMockMvc
public class ReportRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user1", password = "password", authorities = {"Salesperson"})
    public void testGetReportDataLast7Days() throws Exception {
        String requestURL = "/reports/sales_by_date/last_7_days";

        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    @WithMockUser(username = "user1", password = "password", authorities = {"Salesperson"})
    public void testGetReportDataLast6Months() throws Exception {
        String requestURL = "/reports/sales_by_date/last_6_months";

        mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
    }
}
