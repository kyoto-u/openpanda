package org.theospi.portfolio.presentation.tool;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.security.tool.AudienceTool;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 3:56:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedViewer {

    private Agent base;
    private AudienceTool parent;
    private boolean selected = false;

    public DecoratedViewer(Agent base) {
        this.base = base;
    }

    public String getDisplayName() {
        //String baseName = base.getEid().getValue();
        if (isRole()) {
            return base.getDisplayName() + (" (Role)");
        } else {
            if (base.getDisplayName() != null && base.getDisplayName().length() > 0) {
                return base.getDisplayName();
            } else {
                try {
                    return UserDirectoryService.getUserByEid(base.getEid().toString()).getEmail();
                }

                catch (UserNotDefinedException e) {
                    return "";
                }
            }

        }
    }

    public boolean isRole() {
        return base.isRole();
    }

    public String getEmail() {
        if (base.isRole()) {
            return "ROLE" + "." + base.getDisplayName();
        } else {
            try {
                return UserDirectoryService.getUserByEid(base.getEid().toString()).getEmail();
            }

            catch (UserNotDefinedException e) {
                return "";
            }
        }

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Agent getBase() {
        return base;
    }

    public void setBase(Agent base) {
        this.base = base;
    }

    public AudienceTool getParent() {
        return parent;
    }

    public void setParent(AudienceTool parent) {
        this.parent = parent;
    }

    public Agent getRole() {
        List roles = getBase().getWorksiteRoles(getParent().getSite().getId());
        if (roles.size() > 0) {
            return (Agent) roles.get(0);
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecoratedViewer)) {
            return false;
        }

        final DecoratedViewer decoratedMember = (DecoratedViewer) o;

        if (!base.equals(decoratedMember.base)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return base.hashCode();
    }
}
