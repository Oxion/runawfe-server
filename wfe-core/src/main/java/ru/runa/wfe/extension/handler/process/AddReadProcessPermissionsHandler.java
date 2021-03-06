package ru.runa.wfe.extension.handler.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ru.runa.wfe.commons.TypeConversionUtil;
import ru.runa.wfe.execution.ExecutionContext;
import ru.runa.wfe.extension.handler.ParamBasedHandlerActionHandler;
import ru.runa.wfe.security.Permission;
import ru.runa.wfe.security.dao.PermissionDAO;
import ru.runa.wfe.user.Executor;

public class AddReadProcessPermissionsHandler extends ParamBasedHandlerActionHandler {
    @Autowired
    private PermissionDAO permissionDAO;

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        Object executorsParam = paramsDef.getInputParamValue("executors", executionContext.getVariableProvider());
        List<Executor> executors = TypeConversionUtil.convertTo(List.class, executorsParam);
        if (executors == null) {
            log.warn("Null executors in " + this + ", returning");
            return;
        }
        ru.runa.wfe.execution.Process identifiable = executionContext.getProcess();
        Permission permission = Permission.READ;
        for (Executor executor : executors) {
            List<Permission> permissions = permissionDAO.getIssuedPermissions(executor, identifiable);
            if (!permissions.contains(permission)) {
                permissions.add(permission);
                log.debug("Adding " + permission + " to " + executor + " on " + identifiable);
                permissionDAO.setPermissions(executor, permissions, identifiable);
            } else {
                log.debug(executor + " already contains " + permission + " on " + identifiable);
            }
        }
    }

}
