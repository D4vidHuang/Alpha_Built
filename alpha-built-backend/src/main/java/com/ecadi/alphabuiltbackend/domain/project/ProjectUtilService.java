package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshSnapshot;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility service for handling operations related to the Project domain.
 * Contains methods for constructing a Project from a ProjectSnapshot and
 * a Mesh from a MeshSnapshot.
 */
public class ProjectUtilService {

    /**
     * Constructs a Project object from a given ProjectSnapshot.
     * It also transforms the list of MeshSnapshot objects from the ProjectSnapshot
     * into Mesh objects and sets the newly constructed Project as their parent.
     *
     * @param projectSnapshot The ProjectSnapshot to construct from.
     * @return A new Project object corresponding to the given ProjectSnapshot.
     */
    public static Project constructProjectFromProjectSnapshot(ProjectSnapshot projectSnapshot) {
        List<Mesh> meshList = projectSnapshot.getMeshList().stream()
                .map(ProjectUtilService::constructInCompleteMeshFromMeshSnapshot).collect(Collectors.toList());
        int timeStamp = projectSnapshot.getTimeStamp();
        System.out.println("arrived here");
        Project newProject = new Project(projectSnapshot.getProjectId(), timeStamp, meshList);
        meshList.forEach(mesh -> mesh.setProjectRestricted(newProject));
        return newProject;
    }

    /**
     * Constructs an incomplete Mesh object from a given MeshSnapshot.
     * The constructed Mesh object only has meshId and properties fields populated,
     * the parent project field is not set in this method.
     *
     * @param meshSnapshot The MeshSnapshot to construct from.
     * @return A new Mesh object corresponding to the given MeshSnapshot.
     */
    private static Mesh constructInCompleteMeshFromMeshSnapshot(MeshSnapshot meshSnapshot) {
        return new Mesh(meshSnapshot.getMeshId(), meshSnapshot.getProperties());
    }


}
