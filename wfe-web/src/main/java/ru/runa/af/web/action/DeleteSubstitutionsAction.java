package ru.runa.af.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.runa.common.web.action.ActionBase;
import ru.runa.common.web.form.IdsForm;
import ru.runa.wfe.service.SubstitutionService;
import ru.runa.wfe.service.delegate.Delegates;

import com.google.common.collect.Lists;

/**
 * Created on 14.08.2010
 * 
 * @struts:action path="/deleteSubstitutions" name="idsForm" validate="false"
 * @struts.action-forward name="success" path="/manage_executor.do"
 * @struts.action-forward name="failure" path="/manage_executor.do"
 */
public class DeleteSubstitutionsAction extends ActionBase {

    public static final String ACTION_PATH = "/deleteSubstitutions";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse responce) {
        try {
            SubstitutionService substitutionService = Delegates.getSubstitutionService();
            substitutionService.deleteSubstitutions(getLoggedUser(request), Lists.newArrayList(((IdsForm) form).getIds()));
        } catch (Exception e) {
            addError(request, e);
            return mapping.findForward(ru.runa.common.web.Resources.FORWARD_FAILURE);
        }
        return mapping.findForward(ru.runa.common.web.Resources.FORWARD_SUCCESS);
    }

}
