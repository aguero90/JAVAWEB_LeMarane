/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.univaq.f4i.iw.framework.result;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import it.univaq.f4i.iw.framework.security.SecurityLayer;
import java.util.List;

/**
 *
 * @author Giuseppe Della Penna
 */
public class SplitSlashesFmkExt implements TemplateMethodModelEx {

    @Override
    public Object exec(List list) throws TemplateModelException {
        if (!list.isEmpty()) {
            return SecurityLayer.stripSlashes(list.get(0).toString());
        } else {
            return "";
        }
    }
}
