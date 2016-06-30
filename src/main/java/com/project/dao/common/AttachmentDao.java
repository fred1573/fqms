package com.project.dao.common;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.common.Attachment;

/**
 * @author 
 * mowei
 */
@Component
public class AttachmentDao extends HibernateDao<Attachment, Long> {
}
