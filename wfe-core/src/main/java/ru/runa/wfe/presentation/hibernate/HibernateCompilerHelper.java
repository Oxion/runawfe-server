/*
 * This file is part of the RUNA WFE project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package ru.runa.wfe.presentation.hibernate;

import java.util.Arrays;
import java.util.List;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import ru.runa.wfe.InternalApplicationException;
import ru.runa.wfe.commons.ApplicationContextFactory;
import ru.runa.wfe.presentation.BatchPresentation;
import ru.runa.wfe.presentation.ClassPresentation;
import ru.runa.wfe.presentation.FieldDescriptor;
import ru.runa.wfe.presentation.FieldFilterMode;
import ru.runa.wfe.presentation.FieldState;

/**
 * Helper class with some functions, required to compile query for batch presentation.
 */
public final class HibernateCompilerHelper {

    /**
     * Check, if field must affects SQL query.
     * 
     * @param field
     *            Filed to check.
     * @param batchPresentation
     *            {@link BatchPresentation}, used to build query.
     * @return True, is field will affects SQL; false otherwise.
     */
    public static boolean isFieldSQLAffects(FieldDescriptor field, BatchPresentation batchPresentation) {
        if (field.fieldState == FieldState.DISABLED) {
            return false;
        }
        final List<FieldDescriptor> dysplayFields = Arrays.asList(batchPresentation.getDisplayFields());
        if (!field.isWeakJoin && dysplayFields.contains(field)) {
            return true;
        }
        FieldDescriptor[] allFields = batchPresentation.getAllFields();
        int idx = 0;
        for (; idx < allFields.length; ++idx) {
            if (allFields[idx].equals(field)) {
                break;
            }
        }
        return (batchPresentation.isFieldFiltered(idx) && field.filterMode == FieldFilterMode.DATABASE)
                || ((batchPresentation.isSortingField(idx) || batchPresentation.isFieldGroupped(idx)) && field.sortable
                        && (!field.displayName.startsWith(ClassPresentation.filterable_prefix)
                                || field.displayName.startsWith(ClassPresentation.filterable_prefix) && batchPresentation.isFieldGroupped(idx)));
    }

    /**
     * Parse identifier from string.
     * 
     * @param sqlRequest
     *            String to parse identifier from.
     * @param tableName
     *            Table name to search identifier.
     * @param forwardSearch
     *            true, to search forward and false otherwise.
     * @return Parsed identifier.
     */
    public static String getIdentifier(StringBuilder sqlRequest, String tableName, boolean forwardSearch) {
        return getIdentifier(sqlRequest, sqlRequest.indexOf(" ", sqlRequest.indexOf(tableName)), forwardSearch);
    }

    /**
     * Parse identifier from string.
     * 
     * @param string
     *            String to parse identifier from.
     * @param idx
     *            Start index, to search identifier.
     * @param forwardSearch
     *            true, to search forward and false otherwise.
     * @return Parsed identifier.
     */
    public static String getIdentifier(CharSequence string, int idx, boolean forwardSearch) {
        while (Character.isWhitespace(string.charAt(idx))) {
            idx = forwardSearch ? idx + 1 : idx - 1;
        }
        int idx1 = idx;
        while (true) {
            char character = string.charAt(idx);
            if (!(Character.isLetter(character) || character == '_' || Character.isDigit(character))) {
                break;
            }
            idx = forwardSearch ? idx + 1 : idx - 1;
        }
        return forwardSearch ? string.subSequence(idx1, idx).toString() : string.subSequence(idx + 1, idx1 + 1).toString();
    }

    /**
     * Get table name from Entity class.
     * 
     * @param entityClass
     *            Class to parse table name from.
     * @return Parsed table name.
     */
    public static String getTableName(Class<?> entityClass) {
        final ClassMetadata meta = ApplicationContextFactory.getSessionFactory().getClassMetadata(entityClass);
        if (!(meta instanceof SingleTableEntityPersister)) {
            throw new InternalApplicationException(
                    "ClassMetadate for " + entityClass.getName() + " is not SingleTableEntityPersister. Please call to developer.");
        }
        return ((SingleTableEntityPersister) meta).getTableName();
    }
}
