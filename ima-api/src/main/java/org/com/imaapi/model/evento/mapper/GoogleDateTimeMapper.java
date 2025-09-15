package org.com.imaapi.model.evento.mapper;

import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class GoogleDateTimeMapper {

    private  static ZoneId zonaPadrao;

    public GoogleDateTimeMapper() {
        zonaPadrao = ZoneId.systemDefault();
    }

    public GoogleDateTimeMapper(ZoneId defaultZone) {
        zonaPadrao = defaultZone;
    }

    public static DateTime toGoogleDateTime(LocalDateTime localDateTime) {
        return new DateTime(Date.from(localDateTime.atZone(zonaPadrao).toInstant()));
    }
}
