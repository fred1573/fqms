package com.project.web.view;

import com.project.entity.account.Role;
import com.project.service.account.RoleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuneng.huang on 2016/5/19.
 */
@Component
public class RoleTreeBuilder implements TreeBuilder<Role> {

    @Resource
    private RoleService roleService;

    @Override
    public TreeView build(Role roleTreeNode) {
        if (roleTreeNode == null) {
            return null;
        }
        TreeView treeView = new TreeView();
        treeView.setId(String.valueOf(roleTreeNode.getId()));
        treeView.setName(roleTreeNode.getSysRoleName());
        List<TreeView> children = new ArrayList<>();
        for (Role role : roleService.findChildren(roleTreeNode.getId())) {
            children.add(build(role));
        }
        treeView.setChildren(children);
        return treeView;
    }

}
