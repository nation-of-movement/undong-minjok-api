package com.undongminjok.api.point.service.provider;


import com.undongminjok.api.point.domain.Point;
import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.point.repository.PointRepository;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.service.service.provider.TemplateProviderService;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.service.provider.UserProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointProviderServiceImpl implements PointProviderService {

  private final PointRepository pointRepository;
  private final UserProviderService userProviderService;
  private final TemplateProviderService templateProviderService;

  @Override
  @Transactional
  public void createPointHistory(PointHistoryDTO pointHistoryDTO) {

    // user, template 조회
    User user = userProviderService.findByIdForUpdate(pointHistoryDTO.getUserId());
    user.updateAmount(pointHistoryDTO.getAmount());

    Template template = templateProviderService.getTemplate(pointHistoryDTO.getTemplateId());

    // PointHistoryDto -> Point entity
    Point point = Point.createPoint(pointHistoryDTO, user, template);

    // history 저장
    pointRepository.save(point);
  }

  @Transactional
  public void createPointHistory(User user, PointHistoryDTO dto) {
    user.updateAmount(dto.getAmount());
    Point point = Point.createPoint(dto, user, null);
    pointRepository.save(point);
  }


}
