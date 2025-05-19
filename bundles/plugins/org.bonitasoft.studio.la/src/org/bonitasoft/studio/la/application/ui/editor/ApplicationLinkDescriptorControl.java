/**
 * Copyright (C) 2024 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 */
package org.bonitasoft.studio.la.application.ui.editor;

import org.bonitasoft.engine.business.application.xml.ApplicationLinkNode;
import org.bonitasoft.studio.la.application.ui.editor.customPage.CustomPageProvider;
import org.bonitasoft.studio.la.i18n.Messages;
import org.eclipse.jface.layout.FillLayoutFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Controls the descriptor widgets for an application link.
 */
public class ApplicationLinkDescriptorControl extends ApplicationDescriptorControl {

    public ApplicationLinkDescriptorControl(Section applicationSection, ApplicationLinkNode application,
            ApplicationFormPage formPage, CustomPageProvider customPageProvider) {
        super(applicationSection, application, formPage, customPageProvider);
        Composite client = toolkit.createComposite(applicationSection);
        client.setLayout(FillLayoutFactory.fillDefaults().create());
        client.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        // TODO PROD-246 support with a richer editor

        toolkit.createLabel(client, Messages.applicationLinkNoSupport);

        applicationSection.setClient(client);
    }

}
