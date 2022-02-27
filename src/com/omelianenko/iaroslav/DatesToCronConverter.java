package com.omelianenko.iaroslav;
import java.util.List;

public interface DatesToCronConverter {
    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    String convert(List<String> var1) throws DatesToCronConvertException;

    String getImplementationInfo();
}
