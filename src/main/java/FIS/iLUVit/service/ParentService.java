package FIS.iLUVit.service;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.dto.ChildInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    public List<ChildInfo> ChildrenInfo(Long id) {
        Parent findParent = parentRepository.findWithChildren(id)
                .orElseThrow(() -> new UserException("존재하지 않는 User 입니다."));

        List<ChildInfo> childInfoList = new ArrayList<>();

        findParent.getChildren().forEach(child->{
            childInfoList.add(new ChildInfo(child));
        });

        return childInfoList;
    }
}
