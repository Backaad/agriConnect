package com.agriconnect.notification.mapper;

import com.agriconnect.notification.domain.entity.NotificationRecord;
import com.agriconnect.notification.dto.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "isRead", source = "read")
    NotificationResponse toResponse(NotificationRecord record);
}
