package com.project.service.account;

import com.project.dao.MenuDao;
import com.project.entity.Menu;
import com.project.entity.account.Authority;
import com.project.entity.account.Role;
import com.project.entity.account.User;
import com.project.service.CurrentUserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * 菜单
 * @author yuneng.huang on 2016/5/23.
 */
@Service
@Transactional("mybatisTransactionManager")
public class MenuService {

    @Resource
    private MenuDao menuDao;
    @Resource
    private CurrentUserHolder currentUserHolder;

    public List<Menu> findAll() {
        User user = currentUserHolder.getUser();
        Role rootRole = user.getRootRole();
        List<Authority> currentAuthorityList = rootRole.getAuthorityList();
        List<Menu> menuList = menuDao.selectAll();
        Iterator<Menu> menuIterator = menuList.iterator();
        while (menuIterator.hasNext()) {
            Menu menu = menuIterator.next();
            List<Authority> authorityList = menu.getAuthorityList();
            Iterator<Authority> authorityIterator = authorityList.iterator();
            while (authorityIterator.hasNext()) {
                Authority authority = authorityIterator.next();
                boolean isExist = false;
                for (Authority currentAuthority : currentAuthorityList) {
                    if (currentAuthority.getId().equals(authority.getId())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    authorityIterator.remove();
                }
            }
            if (authorityList.size() == 0) {
                menuIterator.remove();
            }
        }
        return menuList;
    }
}
