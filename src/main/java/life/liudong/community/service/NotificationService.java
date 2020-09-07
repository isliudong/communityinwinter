package life.liudong.community.service;

import life.liudong.community.dto.NotificationDTO;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.enums.NotificationStatusEnum;
import life.liudong.community.enums.NotificationTypeEnum;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import life.liudong.community.mapper.NotificationMapper;
import life.liudong.community.model.Notification;
import life.liudong.community.model.NotificationExample;
import life.liudong.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author liudong
 */
@Service
public class NotificationService {
    private final NotificationMapper notificationMapper;
    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public PaginationDTO<NotificationDTO> list(Long userId, Integer page, Integer size) {

        int totalPage;

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        int totalCount = (int) notificationMapper.countByExample(notificationExample);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        } else if (page > totalPage) {
            page = totalPage;
        }
        //分页参数
        int offset = size * (page - 1);
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        //没有通知
        if (notifications.size() == 0){
            return paginationDTO;
        }


        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOList.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOList);
        paginationDTO.setPagination(totalPage, page);


        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification==null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }if (!notification.getReceiver().equals(user.getId())){
            throw new CustomizeException(CustomizeErrorCode.READ_FAIL);
        }
        //标记已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));


        return notificationDTO;

    }
}
