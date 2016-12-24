package com.finalysis.research;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fei on 7/08/2014.
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String AUSSIE_DATE_FORMAT = "dd/MM/yyyy";

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date parse(String str, String format) {
        if(StringUtils.hasText(str)) {
            try {
                return new SimpleDateFormat(format).parse(str);
            } catch (ParseException e) {
                logger.error(e.getMessage());
                //ignore;
            }
        }
        return null;
    }
}
