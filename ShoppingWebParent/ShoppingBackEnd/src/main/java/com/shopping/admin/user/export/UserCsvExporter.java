package com.shopping.admin.user.export;

import com.shopping.admin.AbstractExporter;
import com.shopping.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "users_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstname", "lastname", "roles", "enabled"};

        csvBeanWriter.writeHeader(csvHeader);

        for (User user : listUsers) {
            csvBeanWriter.write(user, fieldMapping);
        }

        csvBeanWriter.close();
    }
}
