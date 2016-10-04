package craftedcart.smblevelworkshop.project;

import craftedcart.smblevelworkshop.Project;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class ProjectManager {

    private static Project currentProject;

    public static void setCurrentProject(Project currentProject) {
        ProjectManager.currentProject = currentProject;
    }

    public static Project getCurrentProject() {
        return currentProject;
    }

}
