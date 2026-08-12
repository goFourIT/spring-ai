package io.github.go4it.ai.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateTimeTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeTools.class);

    @Tool(description = "Get the current date and time")
    public String getCurrentDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.now();
        LOGGER.info("Current date and time: {}", localDateTime);

        return localDateTime.toString();
    }

    @Tool(description = "Add a number of days to a given date and return the resulting date")
    public String addDaysToDate(
            @ToolParam(description = "Date in ISO format, e.g. 2026-08-12")String date,
            @ToolParam(description = "Number of days to add") Integer days
    ) {
        final LocalDate localDate = LocalDate.parse(date);
        final LocalDate resultDate = localDate.plusDays(days);
        LOGGER.info("Local date: {}, days to add: {}, resulting date: {}", localDate, days, resultDate);

        return resultDate.toString();
    }

    @Tool(description = "Subtract a number of days from a given date and return the resulting date")
    public String subtractDaysFromDate(
            @ToolParam(description = "Date in ISO format, e.g. 2026-08-12")String date,
            @ToolParam(description = "Number of days to subtract") Integer days
    ) {
        final LocalDate localDate = LocalDate.parse(date);
        final LocalDate resultDate = localDate.minusDays(days);
        LOGGER.info("Local date: {}, days to subtract: {}, resulting date: {}", localDate, days, resultDate);

        return resultDate.toString();
    }


}
