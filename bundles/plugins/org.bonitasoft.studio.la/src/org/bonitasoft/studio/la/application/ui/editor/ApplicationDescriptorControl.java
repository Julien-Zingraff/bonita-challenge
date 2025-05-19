/*******************************************************************************
 * Copyright (C) 2017 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package org.bonitasoft.studio.la.application.ui.editor;

import org.bonitasoft.engine.business.application.xml.AbstractApplicationNode;
import org.bonitasoft.studio.common.ui.jface.SWTBotConstants;
import org.bonitasoft.studio.la.application.ui.editor.customPage.CustomPageProvider;
import org.bonitasoft.studio.la.application.ui.editor.listener.CloneApplicationDescriptorListener;
import org.bonitasoft.studio.la.application.ui.editor.listener.DeleteApplicationDescriptorListener;
import org.bonitasoft.studio.la.i18n.Messages;
import org.bonitasoft.studio.pics.Pics;
import org.bonitasoft.studio.pics.PicsConstants;
import org.bonitasoft.studio.preferences.PreferenceUtil;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public abstract class ApplicationDescriptorControl {

    protected Section control;
    protected AbstractApplicationNode application;
    protected final DataBindingContext ctx;
    protected final FormToolkit toolkit;
    protected IObservableValue<String> tokenObservable;

    public ApplicationDescriptorControl(Section applicationSection,
            AbstractApplicationNode application,
            ApplicationFormPage formPage,
            CustomPageProvider customPageProvider) {

        this.control = applicationSection;
        this.application = application;
        this.ctx = new DataBindingContext();

        this.toolkit = formPage.getToolkit();
        final ToolBar toolBar = new ToolBar(applicationSection, SWT.FLAT | SWT.RIGHT);

        final ToolItem cloneToolItem = new ToolItem(toolBar, SWT.PUSH);
        cloneToolItem.setToolTipText(Messages.duplicateApplicationDescriptor);
        Image cloneImage = PreferenceUtil.isDarkTheme()
                ? Pics.getImage(PicsConstants.duplicate_16_dark)
                : Pics.getImage(PicsConstants.duplicate_16);
        cloneToolItem.setImage(cloneImage);
        cloneToolItem.addListener(SWT.Selection, new CloneApplicationDescriptorListener(application, formPage));
        cloneToolItem.setData(SWTBotConstants.SWTBOT_WIDGET_ID_KEY,
                "org.bonitasoft.studio.la.ui.editor.clone." + application.getToken());
        final ToolItem deleteToolItem = new ToolItem(toolBar, SWT.PUSH);
        deleteToolItem.setToolTipText(Messages.deleteApplicationDescriptor);
        deleteToolItem.setImage(Pics.getImage(PicsConstants.delete));
        deleteToolItem.addListener(SWT.Selection,
                new DeleteApplicationDescriptorListener(application, formPage));
        deleteToolItem.setData(SWTBotConstants.SWTBOT_WIDGET_ID_KEY,
                "org.bonitasoft.studio.la.ui.editor.delete." + application.getToken());

        applicationSection.setTextClient(toolBar);
        applicationSection.setLayout(GridLayoutFactory.fillDefaults().create());
        applicationSection
                .setLayoutData(
                        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());

        tokenObservable = PojoProperties.<AbstractApplicationNode, String> value("token").observe(application);

        ctx.bindValue(PojoProperties.value("text").observe(applicationSection), tokenObservable);
    }

    public Section getControl() {
        return control;
    }

    public AbstractApplicationNode getApplication() {
        return application;
    }

    public IObservableValue<String> getTokenObservable() {
        return tokenObservable;
    }

    public void updateTargets() {
        // do nothing, subclasses may override
    }

}
