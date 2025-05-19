/**
 * Copyright (C) 2024 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.application.dialog;

import java.awt.Desktop;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.bonitasoft.studio.application.i18n.Messages;
import org.bonitasoft.studio.common.RedirectURLBuilder;
import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.bonitasoft.studio.common.ui.PlatformUtil;
import org.bonitasoft.studio.preferences.BonitaPreferenceConstants;
import org.bonitasoft.studio.preferences.BonitaStudioPreferencesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DataCollectionDialog extends MessageDialogWithToggle {

    public static DataCollectionDialog open(Shell parent) {
        return open(QUESTION, parent, Messages.dataCollectionTitle, Messages.dataCollectionMsg, Messages.rememberDecision,
                true, BonitaStudioPreferencesPlugin.getDefault().getPreferenceStore(),
                BonitaPreferenceConstants.REMEMBER_AGREEMENT_KEY, SWT.NONE);
    }

    public static DataCollectionDialog open(int kind, Shell parent, String title,
            String message, String toggleMessage, boolean toggleState,
            IPreferenceStore store, String key, int style) {
        DataCollectionDialog dialog = new DataCollectionDialog(parent,
                title, null, // accept the default window icon
                message, kind, 0,
                toggleMessage, toggleState);
        style &= SWT.SHEET;
        dialog.setShellStyle(dialog.getShellStyle() | style);
        dialog.setPrefStore(store);
        dialog.setPrefKey(key);
        var rememberAgreement = store.getBoolean(key);
        if (rememberAgreement) {
            dialog.setReturnCode(IDialogConstants.OK_ID);
            return dialog;
        }
        dialog.open();
        return dialog;
    }

    protected DataCollectionDialog(Shell parentShell, String dialogTitle, Image image, String message,
            int dialogImageType, int defaultIndex, String toggleMessage, boolean toggleState) {
        super(parentShell, dialogTitle, image, message, dialogImageType, dialogButtons(), defaultIndex, toggleMessage,
                toggleState);
    }

    private static LinkedHashMap<String, Integer> dialogButtons() {
        LinkedHashMap<String, Integer> buttonLabelToIdMap = new LinkedHashMap<>();
        buttonLabelToIdMap.put(Messages.continueLabel, IDialogConstants.OK_ID);
        return buttonLabelToIdMap;
    }

    @Override
    protected Control createMessageArea(Composite composite) {
        if (message != null) {
            var textArea = new Browser(composite, SWT.BORDER | SWT.MULTI);
            textArea.setText(message);
            textArea.addOpenWindowListener(e -> {
                try {
                    Desktop.getDesktop().browse(RedirectURLBuilder.createURI("761"));
                } catch (IOException err) {
                    BonitaStudioLog.error(err);
                }
            });
            GridDataFactory
                    .fillDefaults()
                    .hint(600, 350)
                    .align(SWT.FILL, SWT.FILL)
                    .grab(true, true)
                    .applyTo(textArea);
        }
        return composite;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
        var store = getPrefStore();
        var key = getPrefKey();
        if (PlatformUtil.isACommunityBonitaProduct() && buttonId == IDialogConstants.CLOSE_ID) {
            store.setValue(key, false);
        } else {
            store.setValue(key, getToggleState());
        }
        store.setValue(BonitaPreferenceConstants.ENABLE_USER_DATA_COLLECTION_KEY, true);
    }

}
