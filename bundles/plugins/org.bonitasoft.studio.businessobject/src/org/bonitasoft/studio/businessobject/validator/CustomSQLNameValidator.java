/**
 * Copyright (C) 2020 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.businessobject.validator;

import java.util.stream.Collectors;

import org.bonitasoft.engine.bdm.validator.SQLNameValidator;
import org.bonitasoft.engine.bdm.validator.SQLNameValidator.Grammar;
import org.bonitasoft.studio.businessobject.i18n.Messages;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;

public class CustomSQLNameValidator {

    private SQLNameValidator engineValidator;

    public CustomSQLNameValidator(int maxLength) {
        this.engineValidator = new SQLNameValidator(maxLength);
    }

    public IStatus validate(String name) {
        if (engineValidator.isValid(name)) {
            // at least, we can make it work, but still check for warnings
            var discouragingGrammars = engineValidator.isKeywordDiscouragedBy(name);
            if (discouragingGrammars.isEmpty()) {
                return ValidationStatus.ok();
            } else {
                String gramList = discouragingGrammars.stream().map(Grammar::toString)
                        .collect(Collectors.joining(", "));
                String msg = NLS.bind(Messages.discouragedUsageOfKnownReservedKeyword, name, gramList);
                return ValidationStatus.warning(msg);
            }
        } else if (engineValidator.isSQLKeyword(name)) {
            return ValidationStatus.error(NLS.bind(Messages.reservedKeyWord, name));
        } else {
            return ValidationStatus.error(NLS.bind(Messages.invalidSQLIdentifier, name));
        }
    }

}
