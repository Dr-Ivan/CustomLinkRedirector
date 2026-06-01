package com.example.redirector.mapper;

import com.example.redirector.dto.CreateRedirectRequest;
import com.example.redirector.dto.CreateRedirectResponse;
import com.example.redirector.entity.LinkEntity;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {

    public LinkEntity toEntity(CreateRedirectRequest request){
        return LinkEntity.builder()
                .fullLink(request.getRealLink())
                .shortLink(request.getShortName())
                .build();
    }

    public CreateRedirectResponse toResponse(LinkEntity entity){
        return CreateRedirectResponse.builder()
                .id(entity.getId())
                .realLink(entity.getFullLink())
                .shortName(entity.getShortLink())
                .build();
    }

}
