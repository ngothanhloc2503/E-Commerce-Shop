package com.shopping.admin.customer;

import com.shopping.admin.AbstractExporter;
import com.shopping.common.entity.Customer;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CustomerCsvExporter extends AbstractExporter {

    public void export(List<Customer> listCustomers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "customers_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"ID", "E-mail", "First Name", "Last Name"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName"};

        csvBeanWriter.writeHeader(csvHeader);

        for (Customer customer : listCustomers) {
            csvBeanWriter.write(customer, fieldMapping);
        }

        csvBeanWriter.close();
    }
}
